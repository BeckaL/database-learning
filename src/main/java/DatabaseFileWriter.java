import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class DatabaseFileHandler {
    public static Long appendKeyValueToFile(String fileName, String key, String value) throws IOException, URISyntaxException {
        String fullPath = FileWriterUtil.getFilePath(fileName);
        Long startIndex = getExistingFileSize(fullPath);
        FileWriter writer = new FileWriter(fullPath, true);
        String string = String.format("%s%s%s%s", paddedLengthOfStringInBinary(key), key, paddedLengthOfStringInBinary(value), value);
        writer.append(string).close();
        return startIndex;
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