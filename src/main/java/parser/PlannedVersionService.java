package parser;

import data.PlannedVersion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PlannedVersionService {

    public static final String DESCRIPTION_TEXT = "В релизе планируется:";
    public static final String VERSION_NUMBER_TEXT = "Номер версии";
    public static final String VERSION_DATE_TEXT = "Ориентировочная дата выхода";
    public static final String RELEASES_PAGE_URL_TEMPLATE = "https://releases.1c.ru/project/%s";

    private final String configurationDescriptor;
    private final PageProvider pageProvider;

    private static final Logger log = LoggerFactory.getLogger(PlannedVersionService.class);

    public PlannedVersionService(String configurationDescriptor, PageProvider pageProvider) {
        this.configurationDescriptor = configurationDescriptor;
        this.pageProvider = pageProvider;
    }
    
    public PlannedVersion get() {

        String url = String.format(RELEASES_PAGE_URL_TEMPLATE, configurationDescriptor);
        String pageContent = pageProvider.getPage(url);

        if (pageContent != null) {

            Document versions = Jsoup.parse(pageContent);
            
            Elements plannedTableRows = versions.select("table.planTable tr");

            Map<String, String> versionData = new HashMap<>();
            
            for (Element row : plannedTableRows) {
                
                Elements cols = row.select("td");
                
                if (cols.size() >= 2) {
                    versionData.put(cols.get(0).text().replaceAll(String.valueOf((char) 160), "").trim(), cols.get(1).text().replaceAll(String.valueOf((char) 160), "").trim());
                }
            }
            
            String versionNumber = versionData.get(VERSION_NUMBER_TEXT);
            
            if (versionNumber != null) {
                PlannedVersion version = new PlannedVersion();
                version.setVersionNumber(versionNumber);
                version.setReleaseDate(versionData.get(VERSION_DATE_TEXT));
                
                version.setDescription(getDescription(versions));
                
                return version;
            } else {
                log.warn("Can't get planned version number on '{}'", url);
            }
        } else {
            log.warn("Content of '{}' is null", url);
        }
        
        return null;
    }

    private String getDescription(Document versions) {
        
        Elements rows = versions.select("div.formLine");
        
        for (Element row : rows) {
            String rowText = row.text();
            
            if (rowText.startsWith(DESCRIPTION_TEXT)) {
                return rowText.substring(DESCRIPTION_TEXT.length());
            }
        }
        
        log.warn("Can't find description block for planned version");
        
        return "";
    }
}
