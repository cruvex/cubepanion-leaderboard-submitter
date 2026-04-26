package com.cruvex.cubepanionleaderboardsubmitter.events;

public class ModEvents {
    public static void register() {
        ScreenEventHandler.register();
        ServerJoinEventHandler.register();
    }
}
