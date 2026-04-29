//package com.cruvex.cubepanionleaderboardsubmitter.cubesocket.session;
//
//import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.CubeSocket;
//import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.packets.PacketLocationUpdate;
//import art.ameliah.laby.addons.cubepanion.core.events.GameJoinEvent;
//import net.labymod.api.event.Subscribe;
//
//public class CubeSocketGameTracker {
//
//  private final CubeSocket socket;
//
//  public CubeSocketGameTracker(CubeSocket socket) {
//    this.socket = socket;
//  }
//
//  @Subscribe
//  public void onGameUpdate(GameJoinEvent e) {
//    if (socket.getState() != CubeSocketState.CONNECTED) {
//      return;
//    }
//    socket.sendPacket(new PacketLocationUpdate(e));
//  }
//
//
//}
