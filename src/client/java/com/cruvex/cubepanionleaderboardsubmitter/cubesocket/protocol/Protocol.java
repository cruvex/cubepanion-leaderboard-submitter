package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets.*;

import java.util.HashMap;
import java.util.Map;

public class Protocol {

  private final Map<Integer, Class<? extends Packet>> packets = new HashMap<>();

  public Protocol() {
    this.register(0, PacketPing.class);
    this.register(1, PacketPong.class);
    this.register(2, PacketHelloPing.class);
    this.register(3, PacketHelloPong.class);
    this.register(4, PacketLocationUpdate.class);
//    this.register(5, PacketPerkUpdate.class);
    this.register(6, PacketDisconnect.class);
    this.register(7, PacketLogin.class);
    this.register(8, PacketLoginComplete.class);
    this.register(9, PacketSetProtocol.class);
//    this.register(10, PacketGameStatUpdate.class);
    this.register(11, PacketReload.class);
  }

  private void register(int id, Class<? extends Packet> clazz) {
    try {
      this.packets.put(id, clazz);
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
  }

  public Packet getPacket(int id) throws Exception {
    if (!this.packets.containsKey(id)) {
      throw new RuntimeException("Packet with id " + id + " is not registered.");
    } else {
      return this.packets.get(id).getConstructor().newInstance();
    }
  }

  public int getPacketId(Packet packet) {
    for (Map.Entry<Integer, Class<? extends Packet>> entry : this.packets.entrySet()) {
      if (packet.getClass().getSimpleName().equals(entry.getValue().getSimpleName())) {
        return entry.getKey();
      }
    }
    throw new RuntimeException("Packet " + packet + " is not registered.");
  }

  public Map<Integer, Class<? extends Packet>> getPackets() {
    return this.packets;
  }

}
