import java.io.*;
import java.util.Arrays;

import static java.lang.String.format;

public class TestUtils {
    public static String getFirstLineOfFile(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        String line = new BufferedReader(fileReader).lines().findFirst().get();
        fileReader.close();
        return line;
    }

    public static void truncateFile(String fileName) throws IOException {
        new FileWriter(fileName).close();
    }

    public static void deleteFile(String fileName) throws IOException {
        var file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteDirectoryRecursive(String directoryName) throws IOException {
        var file = new File(FileWriterUtil.getFilePath(directoryName));

        if (file.exists()) {
            if (file.isDirectory()) {
                var childFiles = file.listFiles();
                if (Arrays.stream(childFiles).allMatch(File::isFile)) {
                    for(File childFile : childFiles) {
                        childFile.delete();
                    }
                    file.delete();
                } else {
                    throw new RuntimeException(format("%s contains children that could not be deleted - are there subdirectories?", directoryName));
                }
            } else {
                throw new RuntimeException(format("%s is not a directory", directoryName));
            }
        }
    }
}
