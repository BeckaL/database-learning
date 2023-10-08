public class Configuration {
    public Long getMaxLogSegmentSize() {
        return Long.parseLong(System.getenv().getOrDefault("MAX_LOG_SEGMENT_SIZE", "10000"));
    }
}
