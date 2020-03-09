package utiltest;

import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;
import com.igatec.mqlsloth.io.fs.FSUtil;
import com.igatec.mqlsloth.parser.mql.MqlParser;
import com.igatec.mqlsloth.util.CollectionComparator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {

    @Test
    public void splitMqlHeader(){
        Map<String, Integer> data = new HashMap<>();
        data.put("type 'sdvds sdcs' 1", 3);
        data.put("'eService Trigger Program Parameters' 'sdvds sdcs' 1", 3);
        data.put("'eService Trigger Program Parameters' 'sdvds sdcs' ''", 3);

        data.keySet().forEach( line -> {
            List<String> parts = MqlParser.splitHeaderLine(line);
            assertEquals(parts.size(), data.get(line));
        });
    }

    @Test
    public void collectionComparatorTest(){

        CollectionComparator<String> comp = new CollectionComparator<>();

        Collection<String> c1 = new LinkedList<>();
        Collections.addAll(c1, "abbdf", "vdf");
        Collection<String> c2 = new ArrayList<>();
        Collection<String> c3 = new LinkedList<>();
        Collections.addAll(c3, "abbdf", "vdf", "csvs");
        Collection<String> c4 = new LinkedList<>();
        Collections.addAll(c4, "abbdf", "vdf", "acsvs");

        assertTrue(comp.compare(c1, c1) == 0);
        assertTrue(comp.compare(c2, c1) < 0);
        assertTrue(comp.compare(c2, c2) == 0);
        assertTrue(comp.compare(c3, c1) > 0);
        assertTrue(comp.compare(c4, c3) < 0);

    }

    @Test
    public void mqlPatternTest(){
        String[][] matchData = new String[][]{
                new String[]{"IGATestAttribute",
                        "*I*G*A*A**e*", "*G*A*A**e*", "I*G*A*A**e*", "IGATestAttribute", "*", "*IGATestAttribute*"},
                new String[]{"IIIIIIIIIIIII",
                        "*I*", "I*I*I*I*I*III"}
        };
        String[][] dismatchData = new String[][]{
                new String[]{"IIIIIIIIIIIII",
                        "I*I*I*I*I*III*III*III*II"}
        };

        for (String[] set:matchData){
            CIFullName fullName = new CIFullName(SlothAdminType.ATTRIBUTE, new StringCIName(set[0]));
            for (int i=1; i<set.length; i++){
                String mqlRegex = set[i];
                assertTrue(FSUtil.matchesMqlPattern(fullName, mqlRegex));
            }
        }

        for (String[] set:dismatchData){
            CIFullName fullName = new CIFullName(SlothAdminType.ATTRIBUTE, new StringCIName(set[0]));
            for (int i=1; i<set.length; i++){
                String mqlRegex = set[i];
                assertFalse(FSUtil.matchesMqlPattern(fullName, mqlRegex));
            }
        }
    }
}
