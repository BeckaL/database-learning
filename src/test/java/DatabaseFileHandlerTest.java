import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.URISyntaxException;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseFileHandlerTest {

    private final String fileName = "some-file.txt";
    private final String filePath = FileWriterUtil.getFilePath(fileName);
    private final String key = "a_key";
    private final String value = "a_value";
    private final String keyLengthInPaddedString = "00000101";
    private final String valueLengthInPaddedString = "00000111";
    private final String expectedLine = format("%s%s%s%s", keyLengthInPaddedString, key, valueLengthInPaddedString, value);

    private final String otherFile = "database-one/segment-1.txt";

    public DatabaseFileHandlerTest() throws IOException {}

    @AfterEach
    void tearDown() throws IOException {
        TestUtils.truncateFile(filePath);
        TestUtils.deleteDirectoryRecursive(FileWriterUtil.getFilePath("database-one"));
        TestUtils.deleteFile(FileWriterUtil.getFilePath("existing-database/segment-1.txt"));
    }

    @Test
    void writingToAFileStoresTheBytesValueAndKeyAndValue() throws IOException, URISyntaxException, InterruptedException {
        DatabaseFileHandler.appendKeyValueToFile(fileName, key, value);

        assertEquals(expectedLine, TestUtils.getFirstLineOfFile(filePath));
    }

    @Test
    void writingToAFileReturnsTheCorrectIndicesAndKeyValueSize() throws IOException, URISyntaxException {
        var firstResult = DatabaseFileHandler.appendKeyValueToFile(fileName, key, value);
        var secondResult = DatabaseFileHandler.appendKeyValueToFile(fileName, key, value);

        assertEquals(expectedLine + expectedLine, TestUtils.getFirstLineOfFile(filePath));
        assertEquals(0, firstResult.getLeft());
        assertEquals(expectedLine.length(), firstResult.getRight());
        assertEquals(expectedLine.length(), secondResult.getLeft());
        assertEquals(expectedLine.length(), secondResult.getRight());
    }

    @Test
    void creatingAFileCreatesAFileUnderTheGivenDatabaseName() throws IOException {
        DatabaseFileHandler.createFile("database-one", "segment-1");

        var file = new File(FileWriterUtil.getFilePath(otherFile));
        assertTrue(file.exists());
    }

    @Test
    void creatingAFileCreatesAFileUnderTheGivenDatabaseNameWhenTheDatabaseFolderAlreadyExists() throws IOException {
        DatabaseFileHandler.createFile("existing-database", "segment-1");

        var file = new File(FileWriterUtil.getFilePath("existing-database/segment-1.txt"));
        assertTrue(file.exists());
    }
}
