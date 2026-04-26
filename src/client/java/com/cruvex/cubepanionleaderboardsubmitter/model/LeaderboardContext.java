package com.cruvex.cubepanionleaderboardsubmitter.model;

import java.util.List;

public record LeaderboardContext(int page, Game game, List<CCItemStack> items) {}