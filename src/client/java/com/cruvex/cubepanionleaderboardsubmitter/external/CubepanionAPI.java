package com.cruvex.cubepanionleaderboardsubmitter.external;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.model.Game;
import com.cruvex.cubepanionleaderboardsubmitter.model.LeaderboardRow;
import com.cruvex.cubepanionleaderboardsubmitter.model.Submission;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CubepanionAPI {

    private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

    private static final String baseUrlv2 = "https://cubepanion.ameliah.art/api/v2";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private static CubepanionAPI instance;
    private final Map<String, Game> games = new HashMap<>();

    public CubepanionAPI() {
        instance = this;
    }

    public static CubepanionAPI I() {
        if (instance == null) instance = new CubepanionAPI();
        return instance;
    }

    public void loadInitialData() {
        this.games.clear();

        LOGGER.info("Loading initial data from {}", baseUrlv2);
        this.getGames()
                .exceptionallyAsync(ex -> {
                    LOGGER.error("Failed to load games, some features may not work correctly", ex);
                    return null;
                })
                .thenAcceptAsync(games -> {
                    if (games == null) {
                        return;
                    }

                    for (var game : games) {
                        this.games.put(game.name(), game);
                        this.games.put(game.displayName(), game);
                        game.aliases().forEach(a -> this.games.put(a, game));
                    }

                    LOGGER.info("Loaded {} games", games.size());
                })
                .exceptionallyAsync(ex -> {
                    LOGGER.error("Failed to load games, some features may not work correctly", ex);
                    return null;
                });
    }

    public CompletableFuture<List<Game>> getGames() {
        return get(baseUrlv2 + "/Games", new TypeToken<List<Game>>() {});
    }

    @Nullable
    private Game getGame(String game) {
        return this.games.get(game);
    }

    @Nullable
    public Game tryGame(String game) {
        return this.getGame(game.replace(" ", "_").toLowerCase().trim());
    }

    public CompletableFuture<Void> submit(Game game, List<LeaderboardRow> entries, String playerUuid) {
        Submission submission = new Submission(playerUuid, game.id(), entries);
        String json = gson.toJson(submission);

        LOGGER.info("Submitting leaderboard for game: {}, player: {}", game.name(), playerUuid);

        LOGGER.info("Submission JSON: {}", json);


        // Commented for testing
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(baseUrlv2 + "/Leaderboard"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenCompose(response -> {
//                    if (response.statusCode() != 202) {
//                        return CompletableFuture.failedFuture(new Exception("Server returned status code " + response.statusCode()));
//                    }
//                    return CompletableFuture.completedFuture(null);
//                });
        return CompletableFuture.completedFuture(null);
    }

    private <T> CompletableFuture<T> get(String url, TypeToken<T> token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("CubepanionAPI returned status code " + response.statusCode());
                    }
                    return gson.fromJson(response.body(), token.getType());
                });
    }
}
