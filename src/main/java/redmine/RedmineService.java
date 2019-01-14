package redmine;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.*;
import config.ConnectConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedmineService {
    
    private final ConnectConfiguration config;
    private final RedmineManager mgr;
    private final IssueManager imgr;

    private static final Logger log = LoggerFactory.getLogger(RedmineService.class);

    public RedmineService(ConnectConfiguration configuration) {
        
        this.config = configuration;
        mgr = RedmineManagerFactory.createWithApiKey(config.getRedmineUrl(), config.getApiKey());
        imgr = mgr.getIssueManager();
    }
    
    public Map<String, Integer> createIssues(List<VersionIssue> issues) {

        Map<String, Integer> result = new HashMap<>();
        issues.forEach(issue -> result.put(issue.getVersionNumber(), createIssue(issue)));
        
        return result;
    }
    
    public Integer createIssue(VersionIssue issue) {

        List<Watcher> redmineWatchers = getWatchers(issue.getWatchers());

        try {

            boolean create = issue.getTaskToUpdate() == null;

            Issue newIssue;
            if (create) {
                newIssue = IssueFactory.createWithSubject(issue.getSubject());
            } else {
                newIssue = imgr.getIssueById(issue.getTaskToUpdate());
                newIssue.setSubject(issue.getSubject());
            }
            newIssue.setDescription(issue.getDescription());
            newIssue.setTracker(TrackerFactory.create(issue.getTrackerId(), issue.getTrackerName()));
            newIssue.setTargetVersion(VersionFactory.create(issue.getVersionId()));
            newIssue.addWatchers(redmineWatchers);

            Issue createdIssue;
            if (create) {
                createdIssue = imgr.createIssue(config.getProjectKey(), newIssue);
            } else {
                imgr.update(newIssue);
                createdIssue = newIssue;
            }

            if (createdIssue != null) {
                
                int issueId = createdIssue.getId();
                if (create) {
                    log.info("Created new issue #{}: '{}'", issueId, createdIssue.getSubject());
                } else {
                    log.info("Updated issue #{}: '{}'", issueId, createdIssue.getSubject());
                }
                
                return issueId;
            }
                
            
        } catch (RedmineException e) {
            log.error("Error while creating issue", e);
        }
        
        return 0;
    }
    
    private List<Watcher> getWatchers(List<String> watcherNames) {

        UserManager userManager = mgr.getUserManager();

        List<Watcher> watchers = new ArrayList<>();

        try {
            List<User> redmineUsers = userManager.getUsers();
            for (User user : redmineUsers) {
                if (watcherNames.contains(user.getLogin())) {
                    Watcher newWatcher = WatcherFactory.create(user.getId());
                    newWatcher.setName(user.getFullName());
                    watchers.add(newWatcher);
                }
            }
        } catch (RedmineException e) {
            log.error("Error while getting redmine users", e);
        }
        return watchers;
    }
}
