package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.session;


import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.CubeSocket;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.events.CubeSocketEvents;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketHandler;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets.*;
import com.cruvex.cubepanionleaderboardsubmitter.external.CubepanionAPI;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CubeSocketSession extends PacketHandler {

  private final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

  private static final Gson gson = new Gson();
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private final CubeSocket socket;

  private int keepAlivesSent = 0;
  private int keepAlivesReceived = 0;
  private long lastReload = -1;

  public CubeSocketSession(CubeSocket socket) {
    this.socket = socket;
  }

  public int getKeepAlivesReceived() {
    return keepAlivesReceived;
  }

  public int getKeepAlivesSent() {
    return keepAlivesSent;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    if (socket.getState() != CubeSocketState.OFFLINE) {
      socket.updateState(CubeSocketState.OFFLINE);
//      socket.fireEventSync(new CubeSocketDisconnectEvent(
//          "Server forced a disconnect"));
      CubeSocketEvents.SOCKET_DISCONNECT.invoker().onDisconnected(
          "Server forced a disconnect");
    }
  }

//  @Override
//  public void handle(PacketPerkUpdate packet) {
//    if (Minecraft.getInstance().player == null) {
//      return;
//    }
////    if (this.codecLink == null) {
////      return;
////    }
//
//    // Ignore updates from ourselves, the LoadPerkEvent has already fired
//    UUID uuid = Minecraft.getInstance().player.getUUID();
//    if (packet.getSender().equals(uuid)) {
//      return;
//    }
//
//    List<ItemStack> perks = new ArrayList<>(packet.getPerks().length);
//    for (String perk : packet.getPerks()) {
//      JsonObject json = gson.fromJson(perk, JsonObject.class);
//      Optional<ItemStack> stack = codecLink.decode(json);
//      stack.ifPresent(perks::add);
//    }
//
//    this.socket.fireEventSync(new PerkLoadEvent(packet.getPerkCategory(), perks, true));
//  }

  @Override
  public void handle(PacketHelloPong packet) {
    this.socket.updateState(CubeSocketState.LOGIN);
    UUID uuid;
    if (Minecraft.getInstance().player != null) {
      uuid = Minecraft.getInstance().player.getUUID();
    } else {
      uuid = UUID.randomUUID();
    }

    this.socket.sendPacket(new PacketLogin(uuid));
  }

  @Override
  public void handle(PacketPong packet) {
    this.keepAlivesReceived++;
    this.socket.keepAlive();

    this.executorService.schedule(() -> {
      this.socket.sendPacket(new PacketPing());
      this.keepAlivesSent++;
    }, 5L, TimeUnit.SECONDS);
  }

  @Override
  public void handle(PacketLoginComplete packet) {
    this.socket.updateState(CubeSocketState.CONNECTED);

//    socket.fireEventSync(new CubeSocketConnectEvent());
    CubeSocketEvents.SOCKET_CONNECT.invoker().onConnected();
    this.socket.sendPacket(new PacketPing());

    this.executorService.schedule(() -> {
      int protocolVersion = SharedConstants.getProtocolVersion();
      this.socket.sendPacket(new PacketSetProtocol(protocolVersion));
    }, 1L, TimeUnit.SECONDS);

    this.executorService.schedule(() -> {
//      var fakeEvent = new GameJoinEvent("main_lobby", "main_lobby", false);
      this.socket.sendPacket(PacketLocationUpdate.fakeEvent());
    }, 2L, TimeUnit.SECONDS);
  }

  @Override
  public void handle(PacketDisconnect packet) {
    this.socket.updateState(CubeSocketState.OFFLINE);
//    this.socket.fireEventSync(new CubeSocketDisconnectEvent(packet.getReason()));
    CubeSocketEvents.SOCKET_DISCONNECT.invoker().onDisconnected(packet.getReason());
  }

  @Override
  public void handle(PacketReload packet) {
    long now = System.currentTimeMillis();
    if (now - this.lastReload < 5000L) {
      this.lastReload = now;
      LOGGER.warn("CubeSocket tried reloading data less than 5s apart, ignoring");
      return;
    }


//    this.socket.fireEventSync(new CubeSocketReloadRequest());
    CubeSocketEvents.SOCKET_RELOAD_REQUEST.invoker().onReloadRequested();
    CubepanionAPI.I().loadInitialData();
    this.lastReload = now;
  }
}
