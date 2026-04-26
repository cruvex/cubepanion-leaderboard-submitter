package com.cruvex.cubepanionleaderboardsubmitter.events;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.external.CubepanionAPI;
import com.cruvex.cubepanionleaderboardsubmitter.util.Util;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;

public class ServerJoinEventHandler {

    private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            String ip = Util.getServerIp(client);
            LOGGER.debug("Player connected to server with IP: {}", ip);
            if (Util.isKubusMaken(ip)) {
                CubepanionAPI.I().loadInitialData();
            }
        });
    }
}
