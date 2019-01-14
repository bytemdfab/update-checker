package config;

import java.util.ArrayList;
import java.util.List;

public class ConnectConfiguration {
    
    private String redmineUrl;
    private String apiKey;
    private String projectKey;
    private int trackerId;
    private String trackerName;
    private int versionId;
    private List<String> watchers;

    private String v8configurationName;
    private String v8configurationDisplayName;
    private String v8username;
    private String v8password;

    public ConnectConfiguration() {
        watchers = new ArrayList<>();
    }

    public String getRedmineUrl() {
        return redmineUrl;
    }

    public void setRedmineUrl(String redmineUrl) {
        this.redmineUrl = redmineUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public int getTrackerId() {
        return trackerId;
    }
    
    public String getTrackerName() {
        return trackerName;
    }

    public void setTracker(int trackerId, String trackerName) {
        this.trackerId = trackerId;
        this.trackerName = trackerName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public List<String> getWatchers() {
        return watchers;
    }

    public void addWatcher(String watcher) {
        this.watchers.add(watcher);
    }

    public String getV8username() {
        return v8username;
    }

    public void setV8username(String v8username) {
        this.v8username = v8username;
    }

    public String getV8password() {
        return v8password;
    }

    public void setV8password(String v8password) {
        this.v8password = v8password;
    }

    public String getV8configurationName() {
        return v8configurationName;
    }

    public void setV8configurationName(String v8configurationName) {
        this.v8configurationName = v8configurationName;
    }

    public String getV8configurationDisplayName() {
        return v8configurationDisplayName;
    }

    public void setV8configurationDisplayName(String v8configurationDisplayName) {
        this.v8configurationDisplayName = v8configurationDisplayName;
    }
}
