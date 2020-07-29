package com.igatec.mqlsloth.kernel.session;

import com.igatec.mqlsloth.kernel.SlothException;

public class ExportSessionBuilder extends AbstractSessionBuilder {
    @Override
    public ExportSession build() throws SlothException {
        ExportSession session = new ExportSession(
                context,
                sourceLocation,
                targetLocation,
                searchLocation,
                searchPattern,
                syncronous
        );
        SlothApp.registerSession(session);
        return session;
    }
}
