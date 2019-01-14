import data.ReleasedVersion;
import org.junit.Before;
import org.junit.Test;
import parser.PageProvider;
import parser.ReleasedVersionService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReleasedVersionServiceTest {
    
    private PageProvider pageProvider;
    private ReleasedVersionService service;

    @Before
    public void setUp() throws Exception {
        pageProvider = mock(PageProvider.class);
        
        String testPageVersions = new String(Files.readAllBytes(Paths.get(getClass().getResource("ReleasedVersionsTest.html").toURI())));
        String testPageVersion = new String(Files.readAllBytes(Paths.get(getClass().getResource("ReleasedVersionTest.html").toURI())));

        when(pageProvider.getPage("https://releases.1c.ru/project/EnterpriseUkr13")).thenReturn(testPageVersions);
        when(pageProvider.getPage(startsWith("https://releases.1c.ru/version_files?nick=EnterpriseUkr13&ver="))).thenReturn(testPageVersion);
        when(pageProvider.getPage(contains("news.htm"))).thenReturn("<HTML><HEAD><BODY>Тестовое описание</BODY></HTML>");

        service = new ReleasedVersionService("EnterpriseUkr13", pageProvider);
    }

    @Test
    public void testAll() throws Exception {

        List<ReleasedVersion> testVersions = new ArrayList<>();
        testVersions.add(new ReleasedVersion("1.3.1.10"));
        testVersions.add(new ReleasedVersion("1.3.34.5"));
        testVersions.add(new ReleasedVersion("1.3.35.2"));

        assertThat(service.all(), is(testVersions));
    }

    @Test
    public void testAllAfter() throws Exception {

        List<ReleasedVersion> testVersions = new ArrayList<>();
        testVersions.add(new ReleasedVersion("1.3.34.5"));
        testVersions.add(new ReleasedVersion("1.3.35.2"));

        assertThat(service.allAfter("1.3.1.10"), is(testVersions));
    }

    @Test
    public void testAllAfterIsEmpty() throws Exception {
        assertThat(service.allAfter("1.3.35.2"), is(empty()));
    }
    
    @Test
    public void testExist() throws Exception {
        assertTrue(service.exist("1.3.35.2"));
    }
    
    @Test
    public void testNotExist() throws Exception {
        assertFalse(service.exist("1.3.35.3"));
    }

    @Test
    public void testGet() throws Exception {
        ReleasedVersion testVersion = new ReleasedVersion("1.3.35.2");

        assertEquals(testVersion, service.get("1.3.35.2"));
    }

    @Test
    public void testGetNotExist() throws Exception {
        assertNull(service.get("1.3.35.3"));
    }
}
