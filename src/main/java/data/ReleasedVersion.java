package data;

import java.util.HashMap;
import java.util.Map;

public class ReleasedVersion extends Version {
    
    private String descriptionUrl;
    private String changesUrl;
    private String changedObjectsUrl;
    private String updateUrl;
    private String distrUrl;
    private String readMeUrl;
    private Map<String, String> files = new HashMap<>();

    public ReleasedVersion(String versionNumber, String versionURL, String releaseDate) {
        setReleaseDate(releaseDate);
        setVersionNumber(versionNumber);
        setVersionURL(versionURL);
    }
    
    public ReleasedVersion(String versionNumber) {
        setVersionNumber(versionNumber);
    }
    
    public Map<String, String> getFiles() {
        return files;
    }
    
    public void addFile(String fileUrl, String fileDescription) {
        files.put(fileUrl, fileDescription);
    }
    
    public String getDescriptionUrl() {
        return descriptionUrl;
    }

    public void setDescriptionUrl(String descriptionUrl) {
        this.descriptionUrl = descriptionUrl;
    }

    public String getChangesUrl() {
        return changesUrl;
    }

    public void setChangesUrl(String changesUrl) {
        this.changesUrl = changesUrl;
    }

    public String getChangedObjectsUrl() {
        return changedObjectsUrl;
    }

    public void setChangedObjectsUrl(String changedObjectsUrl) {
        this.changedObjectsUrl = changedObjectsUrl;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getDistrUrl() {
        return distrUrl;
    }

    public void setDistrUrl(String distrUrl) {
        this.distrUrl = distrUrl;
    }

    public String getReadMeUrl() {
        return readMeUrl;
    }

    public void setReadMeUrl(String readMeUrl) {
        this.readMeUrl = readMeUrl;
    }
    
    public String getPlannedVersionNumber() {
        return getVersionNumber().substring(0, getVersionNumber().lastIndexOf('.'));
    }
}
