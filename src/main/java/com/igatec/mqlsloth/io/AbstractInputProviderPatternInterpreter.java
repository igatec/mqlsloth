package com.igatec.mqlsloth.io;

import com.igatec.mqlsloth.ci.constants.SlothAdminType;
import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.ci.util.StringCIName;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractInputProviderPatternInterpreter extends AbstractInputProvider {

    private List<SlothAdminType> types = SlothAdminType.getSorted();

    protected AbstractInputProviderPatternInterpreter(int namesQueueCapacity, int ciStringsQueueCapacity, int cisQueueCapacity) {
        super(namesQueueCapacity, ciStringsQueueCapacity, cisQueueCapacity);
    }

    @Override
    protected Collection<CIFullName> getFullNamePatternsByNamePattern(String namePattern) {
        Collection<CIFullName> result = new LinkedList<>();
        for (SlothAdminType type:types) {
            result.add(new CIFullName(type, new StringCIName(namePattern)));
        }
        return result;
    }

    @Override
    protected Collection<CIFullName> getExistingFullNamesByName(String name) {
        Collection<CIFullName> result = new LinkedList<>();
        for (SlothAdminType type:types) {
            result.add(new CIFullName(type, new StringCIName(name)));
        }
        result.removeIf(ciFullName -> !containsCIDefinition(ciFullName));
        return result;
    }

}


