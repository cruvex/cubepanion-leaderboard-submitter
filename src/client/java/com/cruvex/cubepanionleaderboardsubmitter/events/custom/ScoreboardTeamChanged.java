package com.cruvex.cubepanionleaderboardsubmitter.events.custom;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.scores.PlayerTeam;

public interface ScoreboardTeamChanged {
    Event<ScoreboardTeamChanged> SCOREBOARD_TEAM_CHANGE = EventFactory.createArrayBacked(
            ScoreboardTeamChanged.class,
            callbacks -> (team) -> {
                for (ScoreboardTeamChanged callback : callbacks) {
                    callback.onTeamChanged(team);
                }
            });

    void onTeamChanged(PlayerTeam team);
  }