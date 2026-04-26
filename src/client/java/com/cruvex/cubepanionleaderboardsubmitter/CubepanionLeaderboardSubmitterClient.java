package com.cruvex.cubepanionleaderboardsubmitter;

import com.cruvex.cubepanionleaderboardsubmitter.events.ModEvents;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubepanionLeaderboardSubmitterClient implements ClientModInitializer {

	public static final String MOD_ID = "cubepanion-leaderboard-submitter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Cubepanion Leaderboard Submitter");

		ModEvents.register();
	}
}