package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.Packet;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketBuffer;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketHandler;
import com.cruvex.cubepanionleaderboardsubmitter.managers.CubeCraftManager;

public class PacketLocationUpdate extends Packet {

  private String origin;

  private String destination;

  private boolean preLobby;

  public PacketLocationUpdate() {
  }

  public PacketLocationUpdate(String origin, String destination, boolean preLobby) {
    this.origin = origin;
    this.destination = destination;
    this.preLobby = preLobby;
  }

  public static PacketLocationUpdate fakeEvent() {
    String serverID = CubeCraftManager.getInstance().getServerID();
    String lastServerID = CubeCraftManager.getInstance().getLastServerID();

    return new PacketLocationUpdate(
            "main_lobby-lobby" + lastServerID,
            "main_lobby-lobby" + serverID,
            false
    );
  }

  @Override
  public void read(PacketBuffer buf) {
    this.origin = buf.readString();
    this.destination = buf.readString();
    this.preLobby = buf.readBoolean();
  }

  @Override
  public void write(PacketBuffer buf) {
    buf.writeString(this.origin);
    buf.writeString(this.destination);
    buf.writeBoolean(this.preLobby);
  }

  @Override
  public void handle(PacketHandler packetHandler) {
  }

  public String getOrigin() {
    return this.origin;
  }

  public String getDestination() {
    return this.destination;
  }

  public boolean isPreLobby() {
    return this.preLobby;
  }

}
