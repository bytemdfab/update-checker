package redmine;

import java.util.List;

public class VersionIssue {
    
    private String versionNumber;
    
    private String subject;
    private String description;
    private int trackerId;
    private String trackerName;
    private int versionId;
    private List<String> watchers;
    private Integer taskToUpdate;

    public VersionIssue(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<String> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<String> watchers) {
        this.watchers = watchers;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTrackerId() {
        return trackerId;
    }

    public void setTracker(int trackerId, String trackerName) {
        this.trackerId = trackerId;
        this.trackerName = trackerName;
    }

    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public Integer getTaskToUpdate() {
        return taskToUpdate;
    }

    public void setTaskToUpdate(int taskToUpdate) {
        this.taskToUpdate = taskToUpdate;
    }
}
