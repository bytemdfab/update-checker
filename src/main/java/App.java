import config.ConnectConfiguration;
import config.LoadedReleases;
import data.PlannedVersion;
import data.ReleasedVersion;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.PageProvider;
import parser.PageProviderUsers1C;
import parser.PlannedVersionService;
import parser.ReleasedVersionService;
import redmine.RedmineService;
import redmine.VersionIssue;
import redmine.VersionIssueManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private ConnectConfiguration config;
    private LoadedReleases data;
    
    @Option(name = "-p", usage = "configuration file path", metaVar = "PATH", required = true)
    public String configPath;
    
    @Option(name = "-d", usage = "path to file with loaded releases configuration", metaVar = "PATH", required = true)
    public String dataPath;

    @Option(name = "-c", usage = "cookie store path", metaVar = "PATH", required = true)
    public String cookiePath;

    public void run() {

        if (loadProperties()) {

            RedmineService redmineService = new RedmineService(config);
            VersionIssueManager issueManager = new VersionIssueManager(config);

            try (PageProvider pageProvider = new PageProviderUsers1C(config.getV8username(), config.getV8password(), cookiePath)) {

                // 1. Загружаем данные о релизах. Надо сохранять id загруженных ранее планируемых версий
                //    и обновлять соответствующие задачи вместо создания новых
                ReleasedVersionService versionService = new ReleasedVersionService(config.getV8configurationName(), pageProvider);
                List<ReleasedVersion> issues = versionService.allAfter(data.getLastReleasedVersion());

                if (issues.isEmpty()) {
                    log.info("New released versions not found");                    
                } else {
                    Map<String, Integer> createdIssues = redmineService.createIssues(issueManager.getReleasedIssues(issues, data.getPlannedVersions()));
                    data.storeReleasedVersions(createdIssues);
                }

                // 2. Загружаем данные о планируемой версии
                PlannedVersionService plannedVersionService = new PlannedVersionService(config.getV8configurationName(), pageProvider);
                PlannedVersion plannedVersion = plannedVersionService.get();

                if (plannedVersion == null || data.getPlannedVersions().containsKey(plannedVersion.getVersionNumber())) {
                    log.info("New planned versions not found");
                } else {
                    int createdIssue = redmineService.createIssue(issueManager.getPlannedIssue(plannedVersion));
                    data.storePlannedVersion(plannedVersion.getVersionNumber(), createdIssue);
                }

                // 3. Загружаем данные о новых внешних файлах для текущей релизной версии
                List<ReleasedVersion> versions = new ArrayList<>();
                versions.add(versionService.get(data.getCurrentVersion()));
                versions.addAll(versionService.allAfter(data.getCurrentVersion()));

                for (ReleasedVersion currentVersion : versions) {
                    for (Map.Entry<String, String> file : currentVersion.getFiles().entrySet()) {
                        if (!data.getExtFiles().contains(file.getKey())) {
                            VersionIssue issueToCreate = issueManager.getExtFilesIssue(currentVersion.getVersionNumber(), file.getKey(), file.getValue());
                            Integer createdIssueId = redmineService.createIssue(issueToCreate);

                            if (createdIssueId != null && createdIssueId != 0) {
                                data.addExtFile(file.getKey());
                            }

                        }
                    }
                }
            } catch (IOException e) {
                log.error("Unknown error", e);
            } finally {
                storeData();
            }
            
        } else {
            log.error("Can't load properties. Application will exit.");
            System.exit(1);
        }
    }

    private void storeData() {

        Properties props = new Properties();

        try (FileOutputStream dataOut = new FileOutputStream(dataPath)) {

            props.setProperty("released.last.version", data.getLastReleasedVersion());

            data.getPlannedVersions().forEach(new BiConsumer<String, Integer>() {
                @Override
                public void accept(String versionNumber, Integer taskId) {

                    String versionPropertyPart = versionNumber.replace(".", "");
                    props.setProperty(String.format("planned.%s.version", versionPropertyPart), versionNumber);
                    props.setProperty(String.format("planned.%s.task", versionPropertyPart), String.valueOf(taskId));
                }
            });
            
            props.setProperty("current.version", data.getCurrentVersion());
            props.setProperty("current.files", String.join(";", data.getExtFiles()));
            
            props.store(dataOut, null);

        } catch (IOException e) {
            log.error("Error while writing properties", e);
        }
    }

    private boolean loadProperties() {

        try (FileInputStream pi = new FileInputStream(configPath);
             FileInputStream di = new FileInputStream(dataPath)) {

            readConfig(pi);
            readData(di);
            
            return true;

        } catch (IOException e) {
            log.error("Error while reading properties", e);
        }

        return false;
    }

    private void readData(FileInputStream di) throws IOException {

        Properties d = new Properties();
        d.load(di);

        data = new LoadedReleases();
        data.setLastReleasedVersion(d.getProperty("released.last.version"));

        for (String prop : d.stringPropertyNames()) {
            if (prop.matches("planned.\\d*.version")) {
                String versionPart = prop.substring(prop.indexOf('.')+1, prop.lastIndexOf('.'));
                data.addPlannedVersion(d.getProperty(prop), Integer.valueOf(d.getProperty(String.format("planned.%s.task", versionPart))));
            }
        }
        
        data.setCurrentVersion(d.getProperty("current.version"));
        
        String[] files = d.getProperty("current.files").split(";");
        for (String file : files) {
            data.addExtFile(file);
        }
    }

    private void readConfig(FileInputStream pi) throws IOException {

        Properties p = new Properties();
        p.load(pi);

        config = new ConnectConfiguration();
        config.setRedmineUrl(p.getProperty("redmine.url"));
        config.setApiKey(p.getProperty("redmine.api"));
        config.setProjectKey(p.getProperty("redmine.project"));
        config.setTracker(Integer.valueOf(p.getProperty("redmine.tracker.id")), p.getProperty("redmine.tracker.name"));
        config.setVersionId(Integer.valueOf(p.getProperty("redmine.version.id")));

        String[] watchers = p.getProperty("redmine.watchers").split(";");
        for (String watcher : watchers) {
            config.addWatcher(watcher);
        }

        config.setV8username(p.getProperty("usersv8.username"));
        config.setV8password(p.getProperty("usersv8.password"));
        config.setV8configurationName(p.getProperty("configuration.name"));
        config.setV8configurationDisplayName(p.getProperty("configuration.display.name"));
    }
}
