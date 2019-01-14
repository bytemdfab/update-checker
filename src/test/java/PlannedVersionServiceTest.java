import data.PlannedVersion;
import org.junit.Before;
import org.junit.Test;
import parser.PageProvider;
import parser.PlannedVersionService;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlannedVersionServiceTest {

    private PageProvider pageProvider;
    private PlannedVersionService service;

    @Before
    public void setUp() throws Exception {
        pageProvider = mock(PageProvider.class);

        String testPage = new String(Files.readAllBytes(Paths.get(getClass().getResource("PlannedVersionServiceTest.html").toURI())));

        when(pageProvider.getPage(anyString())).thenReturn(testPage);
        service = new PlannedVersionService("EnterpriseUkr13", pageProvider);
    }
    
    @Test
    public void testGet() throws Exception {
        
        PlannedVersion testVersion = new PlannedVersion("1.3.36");

        assertEquals(testVersion, service.get());
    }

    @Test
    public void testGetDescription() throws Exception {

        String testDescription = "Тестовое описание";

        assertEquals(testDescription, service.get().getDescription());
    }

    @Test
    public void testGetReleaseDate() throws Exception {
        
        assertEquals("06.03.15", service.get().getReleaseDate());
    }
}
