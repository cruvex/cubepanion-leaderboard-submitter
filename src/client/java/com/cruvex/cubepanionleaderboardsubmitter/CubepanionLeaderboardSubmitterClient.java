package com.cruvex.cubepanionleaderboardsubmitter;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.CubeSocket;
import com.cruvex.cubepanionleaderboardsubmitter.events.handler.ModEvents;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubepanionLeaderboardSubmitterClient implements ClientModInitializer {

	public static final String MOD_ID = "cubepanion-leaderboard-submitter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static CubeSocket socket;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Cubepanion Leaderboard Submitter");
		LOGGER.debug("DEBUG");

		ModEvents.register();

		socket = new CubeSocket();
	}
}