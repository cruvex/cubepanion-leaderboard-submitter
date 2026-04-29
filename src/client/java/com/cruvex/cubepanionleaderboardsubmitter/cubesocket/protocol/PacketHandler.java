package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets.*;
import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;

public abstract class PacketHandler extends SimpleChannelInboundHandler<Object> {

  private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

  protected void channelRead0(ChannelHandlerContext ctx, Object packet) {
    this.handlePacket((Packet) packet);
  }

  protected void handlePacket(Packet packet) {
    packet.handle(this);
  }

  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
    LOGGER.error("An exception occurred while handling a packet", cause);
  }


  public abstract void handle(PacketPong packet);

  public abstract void handle(PacketHelloPong packet);

  public abstract void handle(PacketLoginComplete packet);

  public abstract void handle(PacketDisconnect packet);

  public abstract void handle(PacketReload packet);

}
