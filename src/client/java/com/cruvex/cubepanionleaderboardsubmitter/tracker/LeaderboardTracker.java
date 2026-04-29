package com.cruvex.cubepanionleaderboardsubmitter.tracker;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.external.CubepanionAPI;
import com.cruvex.cubepanionleaderboardsubmitter.model.*;
import com.cruvex.cubepanionleaderboardsubmitter.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaderboardTracker {

    private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

    private final Pattern pagePattern = Pattern.compile(".*\\((\\d+)/\\d+\\)");
    private final Set<Integer> pages = ConcurrentHashMap.newKeySet();
    private final Set<LeaderboardRow> rows = ConcurrentHashMap.newKeySet(200);

    public void processScreen() {
        this.loadMenuContext(
                        title -> title != null && title.contains("Leaderboard"),
                        items -> items != null && !items.isEmpty()
                )
                .thenApplyAsync(this::createLeaderboardContext)
                .thenApplyAsync(this::processContext)
                .thenComposeAsync(context -> {
                    if (context == null) return CompletableFuture.completedFuture(null);

                    var submittingFor = context.game();
                    var rowsList = rows.stream().toList();

                    String uuid = Minecraft.getInstance().player.getStringUUID();

                    return CubepanionAPI.I()
                            .submit(submittingFor, rowsList, uuid)
                            .thenAcceptAsync(ignored -> onSuccess(submittingFor));
                })
                .exceptionallyAsync(ex -> {
                    LOGGER.error("Failed to load or submit leaderboard", ex);
                    return null;
                });
    }

    public CompletableFuture<@Nullable MenuContext> loadMenuContext(Predicate<String> titlePredicate, Predicate<List<CCItemStack>> itemPredicate) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return CompletableFuture.completedFuture(null);
        }

        return Util.try10Times(1, () -> {
            Screen currentScreen = minecraft.screen;
            if (!(currentScreen instanceof ContainerScreen)) {
                return false;
            }

            AbstractContainerMenu menu = player.containerMenu;
            if ((!(menu instanceof ChestMenu))) {
                return false;
            }

            Component title = currentScreen.getTitle();
            var titleCheck = titlePredicate.test(title.getString());
            if (itemPredicate == null || !titleCheck) {
                return titleCheck;
            }

            List<CCItemStack> items = new ArrayList<>();
            for (var item : player.containerMenu.getItems()) {
                items.add((CCItemStack) (Object) item);
            }
            return itemPredicate.test(items);
        }, () -> {
            Screen currentScreen = minecraft.screen;
            if (!(currentScreen instanceof ContainerScreen)) {
                return null;
            }

            var title = currentScreen.getTitle().getString();

            List<CCItemStack> items = new ArrayList<>();
            for (var item : player.containerMenu.getItems()) {
                items.add((CCItemStack) (Object) item);
            }
            return new MenuContext(title, items);
        });
    }

    /** Converts a MenuContext into a LeaderboardContext */
    private @Nullable LeaderboardContext createLeaderboardContext(@Nullable MenuContext menuContext) {
        if (menuContext == null) return null;

        String title = menuContext.title();
        List<CCItemStack> items = menuContext.items();
        if (items == null || items.isEmpty()) return null;

        var cleaned = title.replaceAll("[^a-zA-Z0-9s(\\) /]", "");
        var gameString = cleaned.substring(0, cleaned.indexOf("Leaderboard")).trim();
        Game game = CubepanionAPI.I().tryGame(gameString);
        if (game == null) {
            LOGGER.debug("Failed to match {} to a game", gameString);
            return null;
        }

        Matcher matcher = pagePattern.matcher(cleaned.trim());
        if (!matcher.matches()) {
            LOGGER.warn("Failed to find pages in {}", cleaned);
            return null;
        }

        int curPage;
        try {
            curPage = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            LOGGER.error("Failed to parse page number {}", matcher.group(1));
            return null;
        }

        if (!pages.add(curPage)) return null;

        return new LeaderboardContext(curPage, game, items);
    }

    /** Process items for a page and add rows */
    private @Nullable LeaderboardContext processContext(@Nullable LeaderboardContext context) {
        if (context == null) return null;

        int count = 0;
        for (CCItemStack item : context.items()) {
            ItemStack stack = item.asVanilla();

            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.PLAYER_HEAD) continue;

            var row = parseLeaderboardRow(item);
            if (row == null) continue;

            rows.add(row);
            count++;
        }

        if (!checkSubmit()) return null;
        return context;
    }

    private LeaderboardRow parseLeaderboardRow(CCItemStack stack) {
        List<String> tooltip = stack.getToolTips();
        LOGGER.info("tooltip size: {}", tooltip.size());
        if (tooltip.size() < 3) return null;

        String name = tooltip.get(0).trim();
        String ps = tooltip.get(1).replaceAll("[^0-9]", "").trim();
        String ss = tooltip.get(2).replaceAll("[^0-9]", "").trim();

        if (ps.isEmpty() || ss.isEmpty()) return null;

        int position = Integer.parseInt(ps);
        int score = Integer.parseInt(ss);

        return new LeaderboardRow(0, name, position, score, stack.texture());
    }

    private void onSuccess(Game game) {
        Minecraft.getInstance().player.displayClientMessage(
            Component.literal(String.format(
                "Successfully submitted leaderboard places for %s.",
                game.displayName()
                )
            )
           .withStyle(ChatFormatting.GREEN),
            false
        );

        reset();
    }

    private boolean checkSubmit() {
        return pages.size() == 10 && rows.size() == 200;
    }

    public void reset() {
        pages.clear();
        rows.clear();
    }
}
