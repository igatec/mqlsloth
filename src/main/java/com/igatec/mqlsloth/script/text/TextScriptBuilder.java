package com.igatec.mqlsloth.script.text;

import com.igatec.mqlsloth.iface.util.Readable;
import com.igatec.mqlsloth.script.MqlAction;
import com.igatec.mqlsloth.script.MqlCommand;
import com.igatec.mqlsloth.script.assertion.Assertion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TextScriptBuilder implements Readable {

    public static final int DWFAULT_COMMENTS_WIDTH = 80;
    public static final int COMMENT_MIN_SIZE = 30;
    public static final int COMMENT_MAX_SIZE = 255;
    public static final int SOME_MAGIC_NUMBER = 100; // todo rename
    private Iterator<MqlAction> it;
    private int commentsWidth;
    private Queue<String> buffer = new LinkedList<>();
    private boolean emptyLineBetweenItems = true;
    private String cmdParamTab = "    ";

    public TextScriptBuilder(Iterator<MqlAction> iterator) {
        it = iterator;
        setCommentsWidth(DWFAULT_COMMENTS_WIDTH);
    }

    private static String buildCommentLine(String text) {
        return "# " + text;
    }

    private static String concat(String[] strings) {
        StringBuilder sb = new StringBuilder();
        int length = strings.length;
        for (int i = 0; i < length; i++) {
            sb.append(strings[i]);
            if (i < length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public void setCommentsWidth(int charCount) {
        if (charCount < COMMENT_MIN_SIZE || charCount > COMMENT_MAX_SIZE) {
            throw new RuntimeException("MQL script comment width must be between 30 and 255");
        }
        commentsWidth = charCount;
    }

    @Override
    public String read() {
        if (buffer.size() == 0) {
            if (it.hasNext()) {
                MqlAction action = it.next();
                if (action instanceof MqlCommand) {
                    MqlCommand command = (MqlCommand) action;
                    String[][] params = command.getParams();
                    buffer.add(concat(command.getHead()) + (params.length == 0 ? ";" : ""));
                    for (int i = 0; i < params.length; i++) {
                        buffer.add(cmdParamTab + concat(params[i]) + (i == params.length - 1 ? ";" : ""));
                    }
                } else if (action instanceof Assertion) {
                    buffer.addAll(buildCommentBlock(((Assertion) action).getDescription()));
                } else {
                    buffer.addAll(buildCommentBlock("Unknown script action: " + action));
                }
                if (emptyLineBetweenItems) {
                    buffer.add("");
                }
            } else {
                return null;
            }
        }
        return buffer.poll();
    }

    private List<String> buildCommentBlock(String text) {
        int width = commentsWidth - 2;
        List<String> result = new LinkedList<>();
        int nextStart = -width;
        int nextEnd;
        byte counter = 0;
        do {
            counter++;
            nextStart += width;
            nextEnd = Math.min(nextStart + width, text.length());
            result.add(buildCommentLine(text.substring(nextStart, nextEnd)));
        } while (nextEnd < text.length() && counter < SOME_MAGIC_NUMBER);
        return result;
    }

}
