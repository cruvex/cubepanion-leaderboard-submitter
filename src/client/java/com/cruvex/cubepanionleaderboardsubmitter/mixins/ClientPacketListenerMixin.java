package com.cruvex.cubepanionleaderboardsubmitter.mixins;

import com.cruvex.cubepanionleaderboardsubmitter.events.custom.ScoreboardTeamChanged;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleSetPlayerTeamPacket", at = @At("TAIL"))
    private void onHandleSetPlayerTeam(ClientboundSetPlayerTeamPacket packet, CallbackInfo ci) {
        // Skip REMOVE — team is already gone at TAIL, nothing useful to fire
        if (packet.getTeamAction() == ClientboundSetPlayerTeamPacket.Action.REMOVE) return;

        ClientPacketListener self = (ClientPacketListener) (Object) this;
        PlayerTeam team = self.getLevel().getScoreboard().getPlayerTeam(packet.getName());

        if (team != null) {
            ScoreboardTeamChanged.SCOREBOARD_TEAM_CHANGE.invoker().onTeamChanged(team);
        }
    }
}