package com.cruvex.cubepanionleaderboardsubmitter.events.handler;

import com.cruvex.cubepanionleaderboardsubmitter.events.custom.CubeEvents;
import com.cruvex.cubepanionleaderboardsubmitter.external.CubepanionAPI;
import com.cruvex.cubepanionleaderboardsubmitter.managers.CubeCraftManager;

public class CubeJoinEventHandler {
    public static void register() {
        CubeEvents.CUBE_JOIN.register(() -> {
            CubeCraftManager.getInstance().reset();
            CubeCraftManager.getInstance().onCubeJoin();

            CubepanionAPI.I().loadInitialData();
        });
    }
}
