package com.cruvex.cubepanionleaderboardsubmitter.events.handler;


public class ModEvents {
    public static void register() {
        ScreenEventHandler.register();
        ServerEventHandler.register();
        CubeJoinEventHandler.register();
        ScoreboardTeamChangeEventHandler.register();
    }
}
