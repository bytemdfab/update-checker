import data.Version;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class VersionTest {
    
    @Test
    public void testCompareTo() throws Exception {
        
        Version v1 = new Version();
        v1.setVersionNumber("1.3.12.1");
        
        Version v2 = new Version();
        v2.setVersionNumber("1.3.2.1");
        
        assertThat(v1.compareTo(v2), is(greaterThan(0)));
    }

    @Test
    public void testCompareToString() throws Exception {

        Version v1 = new Version();
        v1.setVersionNumber("1.3.1.1");

        Version v2 = new Version();
        v2.setVersionNumber("1.3.1.1RC");

        assertThat(v1.compareTo(v2), is(lessThan(0)));
    }

    @Test
    public void testEquals() throws Exception {

        Version v1 = new Version();
        v1.setVersionNumber("1.3.1.1");

        Version v2 = new Version();
        v2.setVersionNumber("1.3.1.1");
        
        assertTrue(v1.equals(v2));
        
    }
}
