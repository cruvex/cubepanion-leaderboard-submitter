package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.pipeline;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.CubeSocket;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

  private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

  private final CubeSocket cubeSocket;

  public PacketEncoder(CubeSocket cubeSocket) {
    this.cubeSocket = cubeSocket;
  }

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf)
      throws Exception {
    int id = this.cubeSocket.getProtocol().getPacketId(packet);
    if (id != 0 && id != 1) {
        LOGGER.debug("[CUBESOCKET] [OUT] {} {}", id, packet.getClass().getSimpleName());
    }

    PacketBuffer buffer = new PacketBuffer(byteBuf);
    buffer.writeVarIntToBuffer(id);
    packet.write(buffer);
  }
}
