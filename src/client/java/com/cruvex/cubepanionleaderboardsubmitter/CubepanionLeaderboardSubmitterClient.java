package com.cruvex.cubepanionleaderboardsubmitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubepanionLeaderboardSubmitterClient implements ClientModInitializer {

	public static final String MOD_ID = "cubepanion-leaderboard-submitter";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Cubepanion Leaderboard Submitter client");

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			LOGGER.info("Screen initialized: {}", screen.getClass().getName());
		});
	}
}