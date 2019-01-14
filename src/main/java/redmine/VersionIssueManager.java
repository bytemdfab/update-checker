package redmine;

import config.ConnectConfiguration;
import data.PlannedVersion;
import data.ReleasedVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VersionIssueManager {

    public static final String RELEASES_ROOT = "https://releases.1c.ru";

    private ConnectConfiguration config;

    public VersionIssueManager(ConnectConfiguration config) {
        this.config = config;
    }

    public List<VersionIssue> getReleasedIssues(List<ReleasedVersion> versions, Map<String, Integer> plannedTasks) {

        List<VersionIssue> issuesToCreate = new ArrayList<>();

        for (ReleasedVersion version : versions) {

            VersionIssue newIssue = new VersionIssue(version.getVersionNumber());
            newIssue.setSubject("Новая версия конфигурации " + config.getV8configurationDisplayName() + " - " + version.getVersionNumber());
            newIssue.setDescription(String.format(RELEASES_ROOT+"%s\n{{html\n%s\n}}", version.getVersionURL(), version.getDescription()));
            newIssue.setTracker(config.getTrackerId(), config.getTrackerName());
            newIssue.setVersionId(config.getVersionId());
            newIssue.setWatchers(config.getWatchers());

            Integer taskId = plannedTasks.get(version.getPlannedVersionNumber());
            if (taskId != null) {
                newIssue.setTaskToUpdate(taskId);
            }
            
            issuesToCreate.add(newIssue);
        }

        return issuesToCreate;
    }
    
    public VersionIssue getPlannedIssue(PlannedVersion version) {

        VersionIssue newIssue = new VersionIssue(version.getVersionNumber());
        newIssue.setSubject("Планируется новая версия конфигурации " + config.getV8configurationDisplayName() + " - " + version.getVersionNumber());
        newIssue.setDescription(String.format("+Планируемая дата:+ %s\n+Планируемые изменения:+\n\n{{html\n%s\n}}", version.getReleaseDate(), version.getDescription()));
        newIssue.setTracker(config.getTrackerId(), config.getTrackerName());
        newIssue.setVersionId(config.getVersionId());
        newIssue.setWatchers(config.getWatchers());
        
        return newIssue;
    }
    
    public VersionIssue getExtFilesIssue(String versionNumber, String fileUrl, String fileDescription) {

        VersionIssue newIssue = new VersionIssue(versionNumber);
        newIssue.setSubject(String.format("Новый внешний файл для версии %s - %s", versionNumber, fileDescription));
        newIssue.setDescription(String.format("+Описание:+ %s\n+Ссылка для скачивания:+ %s", fileDescription, RELEASES_ROOT + fileUrl));
        newIssue.setTracker(config.getTrackerId(), config.getTrackerName());
        newIssue.setVersionId(config.getVersionId());
        newIssue.setWatchers(config.getWatchers());

        return newIssue;
    }
}
