package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketBuffer;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketHandler;

public class PacketSetProtocol extends Packet {

  private int protocolVersion;

  public PacketSetProtocol(int protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public int getProtocolVersion() {
    return protocolVersion;
  }

  @Override
  public void read(PacketBuffer buf) {
    protocolVersion = buf.readInt();
  }

  @Override
  public void write(PacketBuffer buf) {
    buf.writeInt(protocolVersion);
  }

  @Override
  public void handle(PacketHandler packetHandler) {
  }
}
