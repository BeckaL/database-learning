import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.net.URISyntaxException;

class DatabaseFileHandler {
    public static Pair<Long, Integer> appendKeyValueToFile(String fileName, String key, String value) throws IOException, URISyntaxException {
        String fullPath = FileWriterUtil.getFilePath(fileName);
        Long startIndex = getExistingFileSize(fullPath);
        FileWriter writer = new FileWriter(fullPath, true);
        String string = String.format("%s%s%s%s", paddedLengthOfStringInBinary(key), key, paddedLengthOfStringInBinary(value), value);
        writer.append(string).close();
        return Pair.of(startIndex, string.length());
    }

    public static void createFile(String databaseName, String logSegmentName) throws IOException {
        var directoryFile = new File(FileWriterUtil.getFilePath(databaseName));
        if (!directoryFile.exists()) {
            directoryFile.mkdir();
        }
        var f = new File(FileWriterUtil.getFilePath(databaseName + "/" + logSegmentName + ".txt"));
        var fileCreated = f.createNewFile();
        if (!fileCreated) {
            throw new RuntimeException("File already exists");
        }
    }

    private static Long getExistingFileSize(String fullPath) {
        return new File(fullPath).length();
    }

    private static String paddedLengthOfStringInBinary(String s) {
        //TODO maxSize
        String binaryString = Integer.toBinaryString(s.length());
        return StringUtils.repeat("0", 8 - binaryString.length()) + binaryString;
    }
}