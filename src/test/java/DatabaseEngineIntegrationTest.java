import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        var engine = DatabaseEngine.create("my-database");

        engine.store(key, value);

        var latestLogSegment = engine.logSegments.get(0);
        var line = TestUtils.getFirstLineOfFile(FileWriterUtil.getFilePath(latestLogSegment.location()));
        var expectedLine = keyLengthInPaddedString + key + valueLengthInPaddedString + value;

        assertEquals(expectedLine, line);
        assertEquals(latestLogSegment.offsetMap(), Map.of(key, 0L));
        assertEquals(latestLogSegment.size(), expectedLine.length());
    }
}