import model.LogSegment;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

class DatabaseEngine {
    public ArrayList<LogSegment> logSegments;
    public String name;

    public Configuration configuration;

    public static DatabaseEngine create(String name, Configuration configuration) throws IOException {
        DatabaseEngine engine = new DatabaseEngine();
        engine.configuration = configuration;
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
        var stringToWrite = stringToWrite(key, value);
        if ((liveLogSegment.size() + stringToWrite.length()) > configuration.getMaxLogSegmentSize()) {
            createNewLogSegmentAndPrependToExisting();
            liveLogSegment = this.logSegments.get(0);
        }
        var result = DatabaseFileHandler.appendKeyValueToFile(liveLogSegment.location(), key, value);
        var indexOfKeyValue = result.getLeft(); //TODO this doesn't need to be a pair anymore
        liveLogSegment.offsetMap().put(key, indexOfKeyValue);
        var newLogSegment = new LogSegment(liveLogSegment.location(), liveLogSegment.offsetMap(), liveLogSegment.size() + stringToWrite.length());
        this.logSegments.set(0, newLogSegment);
    }

    private void createNewLogSegmentAndPrependToExisting() {
        //TODO generate random string for segment name
        var newSegment = new LogSegment(format("%s/%s", name, "secondSegment"), new HashMap<>(), 0L);
        this.logSegments.add(0, newSegment);
    }

    //TODO remove duplication from DatabaseFileHandler - maybe these methods go in utils
    private String stringToWrite(String key, String value) {
        return String.format("%s%s%s%s", paddedLengthOfStringInBinary(key), key, paddedLengthOfStringInBinary(value), value);
    }

    private static String paddedLengthOfStringInBinary(String s) {
        //TODO maxSize
        String binaryString = Integer.toBinaryString(s.length());
        return StringUtils.repeat("0", 8 - binaryString.length()) + binaryString;
    }
}