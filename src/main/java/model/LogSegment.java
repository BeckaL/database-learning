package model;

import java.util.HashMap;

public record LogSegment(String location, HashMap<String, Long> offsetMap, Long size) {}

