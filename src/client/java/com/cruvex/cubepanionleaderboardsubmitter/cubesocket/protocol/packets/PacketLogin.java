package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketBuffer;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketHandler;
import java.util.UUID;

public class PacketLogin extends Packet {

  private UUID uuid;

  public PacketLogin() {
  }

  public PacketLogin(UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public void read(PacketBuffer buf) {

  }

  @Override
  public void write(PacketBuffer buf) {
    buf.writeUUID(this.uuid);
  }

  @Override
  public void handle(PacketHandler packetHandler) {

  }
}
