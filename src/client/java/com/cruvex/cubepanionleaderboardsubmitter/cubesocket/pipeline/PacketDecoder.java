package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.pipeline;

import com.cruvex.cubepanionleaderboardsubmitter.CubepanionLeaderboardSubmitterClient;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.CubeSocket;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

  private static final Logger LOGGER = CubepanionLeaderboardSubmitterClient.LOGGER;

  private final CubeSocket cubeSocket;

  public PacketDecoder(CubeSocket cubeSocket) {
    this.cubeSocket = cubeSocket;
  }


  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> objects) throws Exception {
    if (byteBuf.readableBytes() < 1) {
      return;
    }

    PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
    int id = packetBuffer.readVarIntFromBuffer();
    Packet packet = this.cubeSocket.getProtocol().getPacket(id);
    if (id != 0 && id != 1) {
        LOGGER.debug("[CUBESOCKET] [IN] {} {}", id, packet.getClass().getSimpleName());
    }

    packet.read(packetBuffer);
    if (byteBuf.readableBytes() > 0) {
      String packetName = packet.getClass().getSimpleName();
      throw new IOException(
          "Packet " + packetName + " (" + id + ") was larger than I expected, found "
              + byteBuf.readableBytes() + " bytes extra whilst reading packet " + packet);
    }

    objects.add(packet);
  }
}
