package com.cruvex.cubepanionleaderboardsubmitter.events.handler;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.events.custom.ScoreboardTeamChanged;
import com.cruvex.cubepanionleaderboardsubmitter.managers.CubeCraftManager;
import org.slf4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardTeamChangeEventHandler {

    private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

    private static final Pattern DATE_SERVER_ID_REGEX = Pattern.compile(
            "[0-9]{2}/[0-9]{2}/[0-9]{2} \\((.{5})\\)");

    public static void register() {
        ScoreboardTeamChanged.SCOREBOARD_TEAM_CHANGE.register((team) -> {
            String prefix = team.getPlayerPrefix().getString();
            Matcher matcher = DATE_SERVER_ID_REGEX.matcher(prefix);
			if (matcher.matches()) {
				String serverId = matcher.group(1);
                LOGGER.debug("Server ID extracted: {}", serverId);
				CubeCraftManager.getInstance().setServerID(serverId);
			}
        });
    }
}
