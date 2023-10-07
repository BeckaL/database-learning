package model;

import java.util.Map;

public record LogSegment(String location, Map<String, Integer> offsetMap) {}

