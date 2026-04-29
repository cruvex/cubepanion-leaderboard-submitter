package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketBuffer;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketHandler;

public class PacketReload extends Packet {

  public PacketReload() {}

  @Override
  public void read(PacketBuffer buf) {

  }

  @Override
  public void write(PacketBuffer buf) {

  }

  @Override
  public void handle(PacketHandler packetHandler) {
    packetHandler.handle(this);
  }
}
