package config;

import data.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadedReleases {

    private String lastReleasedVersion;
    private Map<String, Integer> plannedVersions;
    private String currentVersion;
    private List<String> extFiles;

    public LoadedReleases() {
        plannedVersions = new HashMap<>();
        extFiles = new ArrayList<>();
    }

    public String getLastReleasedVersion() {
        return lastReleasedVersion;
    }

    public void setLastReleasedVersion(String lastReleasedVersion) {
        this.lastReleasedVersion = lastReleasedVersion;
    }

    public void storeReleasedVersions(Map<String, Integer> createdIssues) {
        
        if (createdIssues.isEmpty())
            return;
        
        String maxVersion = "";
        List<String> versionsToDelete = new ArrayList<>();

        for (String version : createdIssues.keySet()) {

            for (Map.Entry<String, Integer> plannedEntry : plannedVersions.entrySet()) {
                if (plannedEntry.getValue().equals(createdIssues.get(version)))
                    versionsToDelete.add(plannedEntry.getKey());
            }

            if (maxVersion.isEmpty() || Version.compareStr(maxVersion, version) < 0)
                maxVersion = version;
        }

        versionsToDelete.forEach(plannedVersions::remove);

        setLastReleasedVersion(maxVersion);
    }

    public Map<String, Integer> getPlannedVersions() {
        return plannedVersions;
    }

    public void addPlannedVersion(String versionNumber, int taskId) {
        plannedVersions.put(versionNumber, taskId);
    }

    public int getPlannedTaskId(String versionNumber) {
        return plannedVersions.get(versionNumber);
    }

    public void storePlannedVersion(String versionNumber, int taskId) {
        plannedVersions.put(versionNumber, taskId);
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public List<String> getExtFiles() {
        return extFiles;
    }

    public void addExtFile(String fileName) {
        extFiles.add(fileName);
    }
}
