import model.LogSegment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

class DatabaseEngine {
    public ArrayList<LogSegment> logSegments;
    public String name;

    public static DatabaseEngine create(String name) throws IOException {
        DatabaseEngine engine = new DatabaseEngine();
        engine.name = name;
        var firstLogSegmentName = "log-segment-1";
        var firstLogSegmentLocation = format("%s/%s", name, firstLogSegmentName);
        var list = new ArrayList<LogSegment>();
        list.add(new LogSegment(firstLogSegmentLocation, new HashMap<>(), 0L));
        engine.logSegments = list;
        DatabaseFileHandler.createFile(name, firstLogSegmentName);
        return engine;
    }

    public void store(String key, String value) throws Exception {
        var liveLogSegment = this.logSegments.get(0);
        var result = DatabaseFileHandler.appendKeyValueToFile(liveLogSegment.location(), key, value);
        var indexOfKeyValue = result.getLeft();
        var sizeOfKeyValue = result.getRight();
        liveLogSegment.offsetMap().put(key, indexOfKeyValue);
        var newLogSegment = new LogSegment(liveLogSegment.location(), liveLogSegment.offsetMap(), liveLogSegment.size() + sizeOfKeyValue);
        this.logSegments.set(0, newLogSegment);
    }
}