package com.igatec.mqlsloth.script;

import com.igatec.mqlsloth.ci.util.CIFullName;
import com.igatec.mqlsloth.script.action.JPOCompileAction;
import com.igatec.mqlsloth.script.action.ModSymbolicNameAction;

import java.util.*;

import static com.igatec.mqlsloth.script.MqlKeywords.M_ADD;
import static com.igatec.mqlsloth.script.MqlKeywords.M_RANGE;

public class RichMqlScript implements Iterable<MqlAction> {

    private NavigableMap<Integer, Map<CIFullName, NavigableMap<CmdType, List<ScriptChunk>>>> content = new TreeMap<>();
    private int version = Integer.MIN_VALUE;
    private boolean hasItems = false;

    public RichMqlScript addChunk(ScriptChunk chunk) {
        version++;
        hasItems = true;
        int priority = chunk.getPriority();
        Map<CIFullName, NavigableMap<CmdType, List<ScriptChunk>>> onePriorityMap = content.computeIfAbsent(priority, k -> new HashMap<>());
        CIFullName ci = chunk.getRelatedCI();
        SortedMap<CmdType, List<ScriptChunk>> oneCIMap = onePriorityMap.computeIfAbsent(ci, k -> new TreeMap<>());
        CmdType cmdType = getCmdType(chunk);
        List<ScriptChunk> oneCmdTypeChunks = oneCIMap.computeIfAbsent(cmdType, k -> new LinkedList<>());
        oneCmdTypeChunks.add(chunk);
        return this;
    }

    public RichMqlScript addChunks(Collection<ScriptChunk> chunks) {
        for (ScriptChunk chunk : chunks) {
            addChunk(chunk);
        }
        return this;
    }

    private static CmdType getCmdType(ScriptChunk chunk) {
        if (chunk instanceof CreatingChunk)
            return CmdType.CREATE;
        if (chunk instanceof DeletingChunk)
            return CmdType.DELETE;
        return CmdType.MODIFY;
    }

    @Override
    public Iterator<MqlAction> iterator() {
        return new MqlChunkIterator();
    }

    class MqlChunkIterator implements Iterator<MqlAction> {

        private int iterVersion = version;
        private boolean hasNext;

        private Iterator<Map<CIFullName, NavigableMap<CmdType, List<ScriptChunk>>>> prioritiesMapIter = content.values().iterator();
        private Iterator<NavigableMap<CmdType, List<ScriptChunk>>> cisMapIter;
        private NavigableMap<CmdType, List<ScriptChunk>> currentCIMap;

        private Queue<MqlAction> currentActions = new LinkedList<>();

        MqlChunkIterator() {
            if (hasItems) {
                cisMapIter = prioritiesMapIter.next().values().iterator();
                currentCIMap = cisMapIter.next();
                hasNext = true;
            }
        }

        @Override
        public MqlAction next() {
            if (iterVersion != version)
                throw new ConcurrentModificationException();
            if (!hasNext())
                throw new NoSuchElementException();

            if (currentActions.isEmpty()) {
                List<ScriptChunk> createAndModChunks = new ArrayList<>();
                if (currentCIMap.containsKey(CmdType.CREATE)) {
                    createAndModChunks.addAll(currentCIMap.get(CmdType.CREATE));
                }
                if (currentCIMap.containsKey(CmdType.MODIFY)) {
                    createAndModChunks.addAll(currentCIMap.get(CmdType.MODIFY));
                }

                int createAndModifyChunksLength = createAndModChunks.size();
                for (int i = 0; i < createAndModifyChunksLength; i++) { // TODO modify this piece of code to concatenate commands that can be joined
                    ScriptChunk chunk = createAndModChunks.get(i);

                    // JPO
                    if (chunk instanceof JPOCompileChunk) {
                        JPOCompileChunk ch = (JPOCompileChunk) chunk;
                        currentActions.add(new JPOCompileAction(ch.getName(), ch.getCode()));
                        continue;
                    }

                    // Symbolic name
                    if (chunk instanceof ModSymbolicNameChunk) {
                        ModSymbolicNameChunk ch = (ModSymbolicNameChunk) chunk;
                        currentActions.add(new ModSymbolicNameAction(ch.getCiType(), ch.getCiName(), ch.getSymbolicName()));
                        continue;
                    }

                    List<AttachableChunk> chunksToAttach = new LinkedList<>();
                    if (chunk instanceof HeadChunk && !chunk.hasPostAsserions()) {
                        while (i < createAndModifyChunksLength - 1) {
                            i++;
                            ScriptChunk nextChunk = createAndModChunks.get(i);
                            if (nextChunk instanceof AttachableChunk && ((AttachableChunk) nextChunk).canBeAttached() && !nextChunk.hasPreAsserions()) {
                                chunksToAttach.add((AttachableChunk) nextChunk);
                            } else {
                                i--;
                                break;
                            }
                        }
                    }

                    boolean hasAssertions = chunk.hasAssertions();
                    if (hasAssertions) {
                        currentActions.addAll(chunk.getPreAsserions());
                    }
                    MqlCommand command = new MqlCommand(chunk.getCommand());
                    final int chunksToAttachCount = chunksToAttach.size();
                    if (chunksToAttachCount > 0) {
                        String[][] params = new String[chunksToAttachCount][];
                        for (int j = 0; j < chunksToAttachCount; j++) {
                            String[] temp = chunksToAttach.get(j).getCommandParam();
                            // todo refactor this! This is temporary solution to fix bug when new attr containing range definitions is being created
                            if (chunk instanceof AttributeCreateChunk && M_ADD.equals(temp[0]) && temp.length > 1 && M_RANGE.equals(temp[1]))
                                params[j] = Arrays.copyOfRange(temp, 1, temp.length);
                            else
                                params[j] = temp;
                        }
                        command.setParams(params);
                    }
                    currentActions.add(command);
                    if (hasAssertions) {
                        currentActions.addAll(chunk.getPostAsserions());
                    }
                }

                if (currentCIMap.containsKey(CmdType.DELETE)) {
                    List<ScriptChunk> chunks = currentCIMap.get(CmdType.DELETE);
                    for (ScriptChunk chunk : chunks) {
                        boolean hasAssertions = chunk.hasAssertions();
                        if (hasAssertions) {
                            currentActions.addAll(chunk.getPreAsserions());
                        }
                        MqlCommand command = new MqlCommand(chunk.getCommand());
                        currentActions.add(command);
                        if (hasAssertions) {
                            currentActions.addAll(chunk.getPostAsserions());
                        }
                    }
                }

                if (cisMapIter.hasNext())
                    currentCIMap = cisMapIter.next();
                else if (prioritiesMapIter.hasNext()) {
                    cisMapIter = prioritiesMapIter.next().values().iterator();
                    currentCIMap = cisMapIter.next();
                } else {
                    hasNext = false;
                }
            }

            return currentActions.poll();
        }

        @Override
        public boolean hasNext() {
            if (iterVersion != version)
                throw new ConcurrentModificationException();
            return currentActions.size() > 0 || hasNext;
        }

    }

}

enum CmdType {
    CREATE, MODIFY, DELETE
}

class CmdTypeComparator implements Comparator<CmdType> {
    @Override
    public int compare(CmdType o1, CmdType o2) {
        if (o1 == o2)
            return 0;
        if (o1 == CmdType.CREATE) {
            return -1;
        }
        if (o1 == CmdType.MODIFY) {
            if (o2 == CmdType.CREATE)
                return 1;
            else
                return -1;
        }
        if (o1 == CmdType.DELETE) {
            return 1;
        }
        return 0;
    }
}

