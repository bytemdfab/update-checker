package parser;

import data.ReleasedVersion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReleasedVersionService {

    public static final String PROJECT_URL_TEMPLATE = "https://releases.1c.ru/project/%s";
    public static final String VERSION_URL_TEMPLATE = "/version_files?nick=%s&ver=%s";
    public static final String RELEASES_ROOT = "https://releases.1c.ru";

    private final String configurationDescriptor;
    private final PageProvider pageProvider;

    private static final Logger log = LoggerFactory.getLogger(ReleasedVersionService.class);

    public ReleasedVersionService(String configurationDescriptor, PageProvider pageProvider) {
        this.configurationDescriptor = configurationDescriptor;
        this.pageProvider = pageProvider;
    }
    // Public API

    public List<ReleasedVersion> all() {
        return getAllVersions().stream().sorted().peek(this::fillData).collect(Collectors.toList());
    }

    public List<ReleasedVersion> allAfter(ReleasedVersion version) {
        
        List<ReleasedVersion> all = getAllVersions();
        
        return all.stream().filter(v -> v.compareTo(version) > 0).sorted().peek(this::fillData).collect(Collectors.toList());
    }

    public List<ReleasedVersion> allAfter(String strVersion) {
        
        ReleasedVersion version = new ReleasedVersion(strVersion);
        
        return allAfter(version);
    }

    public boolean exist(ReleasedVersion version) {
        
        List<ReleasedVersion> all = getAllVersions();
        
        return all.contains(version);
    }

    public boolean exist(String strVersion) {
        
        ReleasedVersion version = new ReleasedVersion(strVersion);
        
        return exist(version);
    }
    
    public ReleasedVersion get(ReleasedVersion version) {
        
        List<ReleasedVersion> all = getAllVersions();

        Optional<ReleasedVersion> foundVersion = all.stream().filter(Predicate.isEqual(version)).findFirst();
        
        if (foundVersion.isPresent()) {
            ReleasedVersion releasedVersion = foundVersion.get();
            fillData(releasedVersion);
            return releasedVersion;
        } else {
            log.warn("Can't find released version '{}'", version.getVersionNumber());
        }
        return null;
    }
    
    public ReleasedVersion get(String strVersion) {

        ReleasedVersion version = new ReleasedVersion(strVersion);
        
        return get(version);
    }

    // parsing HTML

    private List<ReleasedVersion> getAllVersions() {

        List<ReleasedVersion> allVersions = new ArrayList<>();

        String url = String.format(PROJECT_URL_TEMPLATE, configurationDescriptor);
        String pageContent = pageProvider.getPage(url);

        if (pageContent != null) {

            Document versions = Jsoup.parse(pageContent);

            Elements versionsRows = versions.select("table#versionsTable tr");

            for (Element row : versionsRows) {

                Elements names = row.select("td.versionColumn > a");

                if (names.size() == 0) {
                    continue;
                }

                Elements dates = row.select("td.dateColumn");

                if (dates.size() == 0) {
                    continue;
                }

                String name = names.first().ownText().trim();
                String versionUrl = names.first().attr("href");
                String date = dates.first().ownText().trim();

                allVersions.add(new ReleasedVersion(name, versionUrl, date));

            }
        } else {
            log.warn("Content of '{}' is null", url);
        }

        return allVersions;
    }

    private void fillData(ReleasedVersion version) {
        
        String versionUrl = version.getVersionURL();
        
        if (versionUrl == null || versionUrl.isEmpty()) {
            versionUrl = String.format(VERSION_URL_TEMPLATE, configurationDescriptor, version.getVersionNumber());
            version.setVersionURL(versionUrl);
        }

        String url = RELEASES_ROOT + versionUrl;
        String versionPageContent = pageProvider.getPage(url);
        
        Document versionPage = Jsoup.parse(versionPageContent);

        if (versionPage != null) {
            Elements links = versionPage.select("div.formLine > a");

            for (Element link : links) {

                String linkUrl = link.attr("href");

                if (linkUrl.contains("news.htm")) {
                    version.setDescriptionUrl(linkUrl);
                    version.setDescription(getPageBody(RELEASES_ROOT+linkUrl));
                }
                else if (linkUrl.contains("change.htm"))
                    version.setChangesUrl(linkUrl);
                else if (linkUrl.contains("object.htm"))
                    version.setChangedObjectsUrl(linkUrl);
                else if (linkUrl.contains("1cv8upd.htm"))
                    version.setUpdateUrl(linkUrl);
                else if (linkUrl.contains("updsetup.exe"))
                    version.setDistrUrl(linkUrl);
                else if (linkUrl.contains("ReadMe.txt"))
                    version.setReadMeUrl(linkUrl);
                else if (linkUrl.contains(".zip"))
                    version.addFile(linkUrl, link.text());
            }
        }  else {
            log.warn("Content of '{}' is null", url);
        }
    }

    private String getPageBody(String url) {
        return pageProvider.getPage(url);
    }
}
