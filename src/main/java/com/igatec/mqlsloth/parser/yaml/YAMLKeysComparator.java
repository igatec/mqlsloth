package com.igatec.mqlsloth.parser.yaml;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class YAMLKeysComparator implements Comparator<String> {
    private static final YAMLKeysComparator INSTANCE;

    static {
        INSTANCE = new YAMLKeysComparator();
    }

    private YAMLKeysComparator() {
        String fileContent = null;
        try (InputStream is = getClass().getResourceAsStream("/sloth_yaml_keys_order.txt")) {
            fileContent = IOUtils.toString(is);
        } catch (Exception ex) {
            System.out.println("WARNING: File 'sloth_yaml_keys_sort.txt' not found");
            System.out.println("     " + ex.getClass());
            System.out.println("     " + ex.getMessage());
        }
        if (fileContent != null) {
            String[] lines = fileContent.split("(\n|\r\n)");
            keys = Arrays.stream(lines)
                    .filter(k -> !k.isEmpty() && !k.startsWith("#"))
                    .collect(Collectors.toList());
        } else {
            keys = new LinkedList<>();
        }
    }

    private final List<String> keys;

    public static YAMLKeysComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(String o1, String o2) {
        int index1 = keys.indexOf(o1);
        int index2 = keys.indexOf(o2);
        if (index1 != -1) {
            if (index2 == -1) {
                return -1;
            } else {
                return Integer.compare(index1, index2);
            }
        } else {
            if (index2 == -1) {
                return o1.compareTo(o2);
            } else {
                return 1;
            }
        }
    }
}
