package com.cruvex.cubepanionleaderboardsubmitter.model;

import java.util.List;

public record Submission(String uuid, int gameId, List<LeaderboardRow> entries) {}
