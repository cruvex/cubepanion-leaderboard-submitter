package com.cruvex.cubepanionleaderboardsubmitter.cubesocket;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.events.CubeSocketEvents;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Protocol;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets.PacketDisconnect;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets.PacketHelloPing;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.session.*;
import com.cruvex.cubepanionleaderboardsubmitter.events.custom.CubeEvents;
import com.cruvex.cubepanionleaderboardsubmitter.managers.CubeCraftManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.time.Instant;import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class CubeSocket {

  private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;
  private static final String host = "cubesocket.ameliah.art";
  private static final int port = 30527;

  private final Protocol protocol = new Protocol();
  private final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(0);
  private final ExecutorService executor = Executors.newScheduledThreadPool(2);

  private CubeSocketSession session = null;
  private CubeSocketHandler channelHandler = null;
  private Bootstrap bootstrap;
  private volatile CubeSocketState state;
  private long timeLastKeepAlive;
  private long timeNextConnect;
  private int connectTries;
  private String lastDisconnectReason;

  public CubeSocket() {
    this.state = CubeSocketState.OFFLINE;
    this.timeNextConnect = Instant.now().toEpochMilli();
    this.connectTries = 0;

    new CubeSocketNotifications();

    registerListeners();

    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
      try {
        if (!CubeCraftManager.getInstance().onCubeCraft()) {
          return;
        }

        long durationKeepAlive = Instant.now().toEpochMilli() - this.timeLastKeepAlive;
        long durationConnect = this.timeNextConnect - Instant.now().toEpochMilli();

        if (state != CubeSocketState.OFFLINE && durationKeepAlive > 25000L) {
          disconnect("Connection timed out");
        }

        if (state == CubeSocketState.OFFLINE && durationConnect < 0L) {
          connect();
        }
      } catch (Exception e) {
        LOGGER.error("Error in CubeSocket keep alive", e);
      }
    }, 0L, 5L, TimeUnit.SECONDS);
  }

  private void registerListeners() {
    CubeEvents.CUBE_JOIN.register(this::connect);

    ClientPlayConnectionEvents.DISCONNECT.register(this::onNetworkDisconnect);
  }

  private void connect() {
    if (this.connectTries > 4) {
      return;
    }

    executor.execute(() -> {
      synchronized (this) {
        if (state != CubeSocketState.OFFLINE) {
          return;
        }

        this.keepAlive();
        this.updateState(CubeSocketState.HELLO);
        this.connectTries++;

        this.session = new CubeSocketSession(this);
        this.channelHandler = new CubeSocketHandler(this, this.session);
        this.lastDisconnectReason = null;

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.nioEventLoopGroup);
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.handler(this.channelHandler);

        try {
          this.bootstrap.connect(host, port).syncUninterruptibly();
          this.sendPacket(new PacketHelloPing(Instant.now().toEpochMilli()));
        } catch (Exception e) {
          this.updateState(CubeSocketState.OFFLINE);
          LOGGER.warn("Failed to connect to CubeSocket", e);
        }
      }
    });
  }

  public void onNetworkDisconnect(ClientPacketListener handler, Minecraft client) {
    if (this.isConnected()) {
      this.disconnect("Logged off CubeCraft");
    }
    this.connectTries = 0;
  }

  private void disconnect(String reason) {
    long delay = (long) (1000.0 * Math.random() * 60.0);
    this.timeNextConnect = Instant.now().toEpochMilli() + 10000L + delay;
    this.lastDisconnectReason = reason;
    if (this.state == CubeSocketState.OFFLINE) {
      return;
    }

    CubeSocketEvents.SOCKET_DISCONNECT.invoker().onDisconnected(reason);
    this.updateState(CubeSocketState.OFFLINE);
    this.sendPacket(new PacketDisconnect("logout"), (ch) -> {
      if (ch.isOpen()) {
        ch.close();
      }
    });
    this.session = null;
  }


  public void updateState(CubeSocketState state) {
    synchronized (this) {
      this.state = state;
    }
    CubeSocketEvents.SOCKET_STATE_UPDATE.invoker().onStateUpdated(state);
  }

  public void keepAlive() {
    this.timeLastKeepAlive = Instant.now().toEpochMilli();
  }

  public void sendPacket(Packet packet) {
    this.sendPacket(packet, null);
  }

  public void sendPacket(Packet packet, Consumer<Channel> callback) {
    if (packet == null) {
      LOGGER.warn("Tried to send a null packet");
      return;
    }
    NioSocketChannel channel = this.getChannel();
    if (channel == null || !channel.isActive()) {
      return;
    }

    EventLoop loop = channel.eventLoop();

    if (loop.inEventLoop()) {
      channel
          .writeAndFlush(packet)
          .addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      if (callback != null) {
        callback.accept(channel);
      }

      return;
    }

    loop.execute(() -> {
      channel
          .writeAndFlush(packet)
          .addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      if (callback != null) {
        callback.accept(channel);
      }
    });
  }

  public NioSocketChannel getChannel() {
    return this.channelHandler == null ? null : this.channelHandler.getChannel();
  }

  public boolean isConnected() {
    return this.state == CubeSocketState.CONNECTED;
  }

  public CubeSocketState getState() {
    return this.state;
  }

  public @Nullable CubeSocketSession getSession() {
    return this.session;
  }

  public String getLastDisconnectReason() {
    return lastDisconnectReason;
  }

  public Protocol getProtocol() {
    return protocol;
  }

  public void setConnectTries(int connectTries) {
    this.connectTries = connectTries;
  }
}






















