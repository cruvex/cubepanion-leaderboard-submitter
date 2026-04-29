package com.cruvex.cubepanionleaderboardsubmitter.events.handler;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.events.custom.CubeEvents;
import com.cruvex.cubepanionleaderboardsubmitter.managers.CubeCraftManager;
import com.cruvex.cubepanionleaderboardsubmitter.util.Util;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;

public class ServerEventHandler {

    private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            String ip = Util.getServerIp(client);
            LOGGER.debug("Player connected to server with IP: {}", ip);
            if (!Util.isKubusMaken(ip)) {
                CubeCraftManager.getInstance().reset();
                return;
            }

            CubeEvents.CUBE_JOIN.invoker().onCubeJoin();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            CubeCraftManager.getInstance().reset();
        });
    }
}
