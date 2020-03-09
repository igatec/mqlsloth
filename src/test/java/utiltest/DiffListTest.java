package utiltest;

import com.igatec.mqlsloth.util.DiffList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiffListTest {

    private final List<String> l1 = Arrays.asList("1", "4", "5", "6", "7", "12", "13");
    private final List<String> l2 = Arrays.asList("1", "4", "5", "6", "7", "12", "13");

    private final List<String> l3 = Arrays.asList("20", "21", "22");
    private final List<String> l4 = Arrays.asList("19", "20", "21", "22.5", "23");

    private final List<String> l5 = Arrays.asList("22", "21", "20");
    private final List<String> l6 = Arrays.asList("21", "22", "21");

    private final Function<String, Object> identifier = (s -> s);
    private final BiFunction<String, String, String> diffBuilder = ((s1, s2) -> s1 + "->" + s2);

    @Test
    public void test() {
        DiffList<String> dl1;
        dl1 = new DiffList<>(l1, l2, identifier, diffBuilder);
        assertEquals(l1.size(), dl1.size());
        for (int i = 0; i < dl1.size(); i++) {
            assertEquals(dl1.getMode(i), DiffList.Mode.UPDATE);
        }

        dl1 = new DiffList<>(l1, l3, identifier, diffBuilder);
        assertEquals(l1.size() + l3.size(), dl1.size());
        for (int i = 0; i < l3.size(); i++) {
            assertEquals(dl1.getMode(i), DiffList.Mode.CREATE);
        }
        for (int i = l3.size(); i < l1.size() + l3.size(); i++) {
            assertEquals(dl1.getMode(i), DiffList.Mode.REMOVE);
        }

        dl1 = new DiffList<>(l3, l4, identifier, diffBuilder);
        System.out.println(dl1);
        assertEquals(dl1.size(), 6);
        assertEquals(dl1.getMode(0), DiffList.Mode.CREATE);
        assertEquals(dl1.getMode(1), DiffList.Mode.UPDATE);
        assertEquals(dl1.getMode(2), DiffList.Mode.UPDATE);
        assertEquals(dl1.getMode(3), DiffList.Mode.CREATE);
        assertEquals(dl1.getMode(4), DiffList.Mode.CREATE);
        assertEquals(dl1.getMode(5), DiffList.Mode.REMOVE);

        assertThrows(IllegalArgumentException.class, () -> {
            new DiffList<>(l3, l5, identifier, diffBuilder);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new DiffList<>(l1, l6, identifier, diffBuilder);
        });
    }
}


