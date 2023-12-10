import dev.efnilite.ws.util.GroupUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GroupUtilTest {

    @Test
    public void testGetGroupFromWorld() {
        Map<String, List<String>> groups = new HashMap<>();
        groups.put("group1", List.of("world1", "world2"));
        groups.put("group2", List.of("world3", "world4"));

        assertEquals("group1", GroupUtil.getGroupFromWorld(groups, "world1"));
        assertEquals("group1", GroupUtil.getGroupFromWorld(groups, "world2"));
        assertEquals("group2", GroupUtil.getGroupFromWorld(groups, "world3"));
        assertEquals("group2", GroupUtil.getGroupFromWorld(groups, "world4"));
        assertEquals("world5", GroupUtil.getGroupFromWorld(groups, "world5"));
    }
}