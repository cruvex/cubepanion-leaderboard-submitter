package com.cruvex.cubepanionleaderboardsubmitter.util;

import net.minecraft.client.Minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Util {
    public static String getServerIp(Minecraft client) {
        if (client.getCurrentServer() == null) return null;
        return client.getCurrentServer().ip;
    }

    public static boolean isKubusMaken(String address) {
        address = address.toLowerCase();
        if (address.endsWith("cubecraft.net")) {
            return true;
        }
        if (address.endsWith("cubecraftgames.net")) {
            return true;
        }
        if (address.endsWith("ccgn.co") && !address.contains("maps")) {
            return true;
        }
        if (address.contains("-dev-cc") || address.endsWith("test.ziax.com")) {
            // Copied from Cubepanion but irrelevant to this mod
            // this.manager.setDevServer(true);
            return true;
        }

        return false;
    }

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();

    public static <T> CompletableFuture<T> try10Times(int tries, BooleanSupplier check, Supplier<T> res) {
        if (tries >= 10) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<T> future = new CompletableFuture<>();

        SCHEDULER.schedule(() -> {
            if (check.getAsBoolean()) {
                future.complete(res.get());
            } else {
                try10Times(tries + 1, check, res)
                        .thenAccept(future::complete);
            }
        }, 100, TimeUnit.MILLISECONDS);

        return future;
    }
}
