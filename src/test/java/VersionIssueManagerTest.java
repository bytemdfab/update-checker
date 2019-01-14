import config.ConnectConfiguration;
import data.PlannedVersion;
import data.ReleasedVersion;
import org.junit.Before;
import org.junit.Test;
import redmine.VersionIssue;
import redmine.VersionIssueManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VersionIssueManagerTest {
    
    private ConnectConfiguration conf;
    private VersionIssueManager mgr;
    
    @Before
    public void setUp() throws Exception {
        
        conf = new ConnectConfiguration();
        conf.setVersionId(1);
        conf.setTracker(1, "Задача");
        conf.addWatcher("user1");
        conf.addWatcher("user2");
        conf.setV8configurationDisplayName("УПП");

        mgr = new VersionIssueManager(conf);
    }

    @Test
    public void testGetReleasedIssues() throws Exception {

        ReleasedVersion version = new ReleasedVersion("1.3.35.2");
        version.setVersionURL("/test");
        version.setDescription("test");
        
        List<VersionIssue> issues = mgr.getReleasedIssues(Arrays.asList(version), new HashMap<>());
        
        assertEquals(1, issues.size());
        
        VersionIssue issue = issues.get(0);
        
        assertEquals("Новая версия конфигурации УПП - 1.3.35.2", issue.getSubject());
        assertEquals("https://releases.1c.ru/test\n{{html\ntest\n}}", issue.getDescription());
        assertEquals(1, issue.getTrackerId());
        assertEquals("Задача", issue.getTrackerName());
        assertEquals(1, issue.getVersionId());
        assertEquals(Arrays.asList("user1", "user2"), issue.getWatchers());
    }

    @Test
    public void testGetPlannedIssue() throws Exception {

        PlannedVersion version = new PlannedVersion("1.3.36");
        version.setDescription("test");
        version.setReleaseDate("01.01.2015");
        
        VersionIssue issue = mgr.getPlannedIssue(version);

        assertEquals("Планируется новая версия конфигурации УПП - 1.3.36", issue.getSubject());
        assertEquals("+Планируемая дата:+ 01.01.2015\n+Планируемые изменения:+\n\n{{html\ntest\n}}", issue.getDescription());
        assertEquals(1, issue.getTrackerId());
        assertEquals("Задача", issue.getTrackerName());
        assertEquals(1, issue.getVersionId());
        assertEquals(Arrays.asList("user1", "user2"), issue.getWatchers());
    }
}
