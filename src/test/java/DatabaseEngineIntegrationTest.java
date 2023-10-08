import model.LogSegment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseEngineIntegrationTest {

    private String key = "some-key";
    private String value = "some-value";
    private final String keyLengthInPaddedString = "00001000";
    private final String valueLengthInPaddedString = "00001010";

    private final String databaseName = "my-database";

    @AfterEach
    void teardown() throws IOException {
        TestUtils.deleteDirectoryRecursive(databaseName);
    }

    @Test
    void storingAValueUpdatesTheIndex() throws Exception {
        var engine = DatabaseEngine.create("my-database", testConfiguration(100L));

        engine.store(key, value);

        var latestLogSegment = engine.logSegments.get(0);
        var line = TestUtils.getFirstLineOfFile(FileWriterUtil.getFilePath(latestLogSegment.location()));
        var expectedLine = keyLengthInPaddedString + key + valueLengthInPaddedString + value;

        assertEquals(expectedLine, line);
        assertEquals(latestLogSegment.offsetMap(), Map.of(key, 0L));
        assertEquals(latestLogSegment.size(), expectedLine.length());
    }

    @Test
    void storingAValueWritesToANewLogStatementWhenWritingToTheLiveSegmentWouldExceedTheMaxSize() throws Exception {
        var maxSize = 50L;
        var engine = DatabaseEngine.create("my-database", testConfiguration(maxSize));

        var lineToStore = keyLengthInPaddedString + key + valueLengthInPaddedString + value;

        var logSegments = new ArrayList<LogSegment>();
        var liveLogSegment = engine.logSegments.get(0);
        var updatedLiveLogSegment = new LogSegment(liveLogSegment.location(), liveLogSegment.offsetMap(), maxSize - lineToStore.length() + 1);
        logSegments.add(updatedLiveLogSegment);
        engine.logSegments = logSegments;

        engine.store(key, value);

        var latestLogSegment = engine.logSegments.get(0);
        var line = TestUtils.getFirstLineOfFile(FileWriterUtil.getFilePath(latestLogSegment.location()));

        assertEquals(lineToStore, line);
        assertEquals(2, engine.logSegments.size());
        assertEquals(updatedLiveLogSegment, engine.logSegments.get(1));
        //TODO sort out this nonsense
        var expectedMap = new HashMap<String, Long>();
        expectedMap.put(key, 0L);
        assertEquals(new LogSegment(latestLogSegment.location(), expectedMap, (long) lineToStore.length()), latestLogSegment);
    }

    private Configuration testConfiguration(Long maxSegmentSize) {
        return new Configuration() {
            @Override
            public Long getMaxLogSegmentSize() {
                return maxSegmentSize;
            }
        };
    }
}