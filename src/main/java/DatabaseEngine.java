import model.LogSegments;

import javax.xml.crypto.Data;

class DatabaseEngine {
    private static LogSegments logSegments;

    public DatabaseEngine create(LogSegments logSegments) {
        DatabaseEngine engine = new DatabaseEngine();
        engine.logSegments = logSegments;
        return engine;
    }

    public void store(String key, String value) throws Exception {
        throw new Exception("implement me");
    }
}