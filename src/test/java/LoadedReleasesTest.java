import config.LoadedReleases;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoadedReleasesTest {

    @Test
    public void testStoreReleasedVersions() throws Exception {

        LoadedReleases loadedReleases = new LoadedReleases();
        loadedReleases.addPlannedVersion("1.3.36", 1111);

        Map<String, Integer> releasedVersions = new HashMap<>();
        releasedVersions.put("1.3.36.1", 1111);
        loadedReleases.storeReleasedVersions(releasedVersions);

        assertTrue(loadedReleases.getPlannedVersions().isEmpty());
    }

    @Test
    public void testGetLastReleasedVersion() throws Exception {

        LoadedReleases loadedReleases = new LoadedReleases();

        Map<String, Integer> releasedVersions = new HashMap<>();
        releasedVersions.put("1.3.34.3", 0);
        releasedVersions.put("1.3.36.1", 0);
        releasedVersions.put("1.3.35.2", 0);

        loadedReleases.storeReleasedVersions(releasedVersions);

        assertEquals(loadedReleases.getLastReleasedVersion(), "1.3.36.1");

    }
}