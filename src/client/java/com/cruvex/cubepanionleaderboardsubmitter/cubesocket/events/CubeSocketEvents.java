package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.events;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.session.CubeSocketState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class CubeSocketEvents {

    public static final Event<CubeSocketConnected> SOCKET_CONNECT =
            EventFactory.createArrayBacked(CubeSocketConnected.class,
                    callbacks -> () -> {
                        for (CubeSocketConnected cb : callbacks) cb.onConnected();
                    });

    public static final Event<CubeSocketDisconnected> SOCKET_DISCONNECT =
            EventFactory.createArrayBacked(CubeSocketDisconnected.class,
                    callbacks -> reason -> {
                        for (CubeSocketDisconnected cb : callbacks) cb.onDisconnected(reason);
                    });

    public static final Event<CubeSocketReloadRequested> SOCKET_RELOAD_REQUEST =
            EventFactory.createArrayBacked(CubeSocketReloadRequested.class,
                    callbacks -> () -> {
                        for (CubeSocketReloadRequested cb : callbacks) cb.onReloadRequested();
                    });

    public static final Event<CubeSocketStateUpdated> SOCKET_STATE_UPDATE =
            EventFactory.createArrayBacked(CubeSocketStateUpdated.class,
                    callbacks -> state -> {
                        for (CubeSocketStateUpdated cb : callbacks) cb.onStateUpdated(state);
                    });

    @FunctionalInterface
    public interface CubeSocketConnected {
        void onConnected();
    }

    @FunctionalInterface
    public interface CubeSocketDisconnected {
        void onDisconnected(String reason);
    }

    @FunctionalInterface
    public interface CubeSocketReloadRequested {
        void onReloadRequested();
    }

    @FunctionalInterface
    public interface CubeSocketStateUpdated {
        void onStateUpdated(CubeSocketState state);
    }
}