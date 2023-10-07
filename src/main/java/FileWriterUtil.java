import java.io.IOException;

public class FileWriterUtil {
    public static String getFilePath(String fileName) throws IOException {
        var path = isRunningInTestContext() ? "src/test/resources/" : "src/main/resources/";
        return path + fileName;
    }

    private static boolean isRunningInTestContext() {
        return "true".equals(System.getProperty("test.context"));
    }
}