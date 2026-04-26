package com.cruvex.cubepanionleaderboardsubmitter.events;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.tracker.LeaderboardTracker;
import com.cruvex.cubepanionleaderboardsubmitter.util.Util;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.slf4j.Logger;

import static com.cruvex.cubepanionleaderboardsubmitter.util.Util.isKubusMaken;

public class ScreenEventHandler {
    private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

    public static void register() {
        LeaderboardTracker leaderboardTracker = new LeaderboardTracker();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            String ip = Util.getServerIp(client);
            if (ip == null || !isKubusMaken(ip)) return;
            if (!(screen instanceof ContainerScreen)) return;
            if (client.player == null) return;
            AbstractContainerMenu menu = client.player.containerMenu;
            if ((!(menu instanceof ChestMenu))) return;

            LOGGER.info("Server IP: {}", ip);
            LOGGER.info("KubusMaken: {}", isKubusMaken(ip));
            LOGGER.info("Screen initialized: {}", screen.getClass().getName());

            String title = screen.getTitle().getString();
            if (!title.contains("Leaderboard")) return;
            LOGGER.info("Container title: {}", title);

            leaderboardTracker.processScreen();
        });
    }


}
