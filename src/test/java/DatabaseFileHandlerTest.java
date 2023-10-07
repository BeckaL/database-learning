import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;

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
        truncateFile(filePath);
        deleteFile(FileWriterUtil.getFilePath(otherFile));
        deleteFile(FileWriterUtil.getFilePath("database-one"));
        deleteFile(FileWriterUtil.getFilePath("existing-database/segment-1.txt"));
    }

    @Test
    void writingToAFileStoresTheBytesValueAndKeyAndValue() throws IOException, URISyntaxException, InterruptedException {
        DatabaseFileHandler.appendKeyValueToFile(fileName, key, value);

        assertEquals(expectedLine, getFirstLineOfFile(filePath));
    }

    @Test
    void writingToAFileReturnsTheCorrectIndices() throws IOException, URISyntaxException {
        Long firstIndex = DatabaseFileHandler.appendKeyValueToFile(fileName, key, value);
        Long secondIndex = DatabaseFileHandler.appendKeyValueToFile(fileName, key, value);

        assertEquals(expectedLine + expectedLine, getFirstLineOfFile(filePath));
        assertEquals(0, firstIndex);
        assertEquals(expectedLine.length(), secondIndex);
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

    private String getFirstLineOfFile(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        String line = new BufferedReader(fileReader).lines().findFirst().get();
        fileReader.close();
        return line;
    }

    private void truncateFile(String fileName) throws IOException {
        new FileWriter(fileName).close();
    }

    private void deleteFile(String fileName) throws IOException {
        var file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
