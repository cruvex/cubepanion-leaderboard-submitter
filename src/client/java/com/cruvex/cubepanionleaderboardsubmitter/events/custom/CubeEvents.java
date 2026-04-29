package com.cruvex.cubepanionleaderboardsubmitter.events.custom;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class CubeEvents {
    public static final Event<CubeJoinEvent> CUBE_JOIN = EventFactory.createArrayBacked(
            CubeJoinEvent.class,
            callbacks -> () -> {
                for (CubeJoinEvent callback : callbacks) {
                    callback.onCubeJoin();
                }
            });

    @FunctionalInterface
    public interface CubeJoinEvent {
        void onCubeJoin();
    }
}