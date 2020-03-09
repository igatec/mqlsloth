package utiltest;

import com.igatec.mqlsloth.util.StringComposition;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringCompositionTest {

    @Test
    public void test1() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("main", "Some content" + System.lineSeparator() + "vdfgverge");
        map1.put("content", "-!-!-");
        map1.put("cccc", "");
        mapTest(map1);
    }

    private void mapTest(Map<String, String> map) {
        StringComposition sc = new StringComposition();
        for (String k : map.keySet()) {
            sc.addPart(k, map.get(k));
        }
        String s = sc.toString();
        StringComposition scNew = StringComposition.parse(s);
        Map<String, String> mapNew = scNew.getParts();
        assertEquals(map, mapNew);
    }
}
