package com.cruvex.cubepanionleaderboardsubmitter.cubesocket;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.events.CubeSocketEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class CubeSocketNotifications {

  private final Minecraft minecraft;

  public CubeSocketNotifications() {
    this.minecraft = Minecraft.getInstance();

    registerListeners();
  }

  private void registerListeners() {
    CubeSocketEvents.SOCKET_CONNECT.register(this::onCubeSocketConnect);
    CubeSocketEvents.SOCKET_DISCONNECT.register(this::onCubeSocketDisconnect);
    CubeSocketEvents.SOCKET_RELOAD_REQUEST.register(this::onReload);
  }

  private void onReload() {
    minecraft.execute(() -> {
      minecraft.getToastManager().addToast(
              SystemToast.multiline(
                      this.minecraft,
                      SystemToast.SystemToastId.NARRATOR_TOGGLE,
                      Component.literal("CubeSocket"),
                      Component.literal("Successfully reloaded")
              )
      );
    });
  }

  private void onCubeSocketConnect() {
    minecraft.execute(() -> {
      minecraft.getToastManager().addToast(
              SystemToast.multiline(
                      this.minecraft,
                      SystemToast.SystemToastId.NARRATOR_TOGGLE,
                      Component.literal("CubeSocket"),
                      Component.literal("Successfully connected")
              )
      );
    });
  }

  // Doesn't seem to get rendered on server disconnect for some reason
  private void onCubeSocketDisconnect(String reason) {
    minecraft.execute(() -> {
      minecraft.getToastManager().addToast(
              SystemToast.multiline(
                      this.minecraft,
                      SystemToast.SystemToastId.NARRATOR_TOGGLE,
                      Component.literal("CubeSocket"),
                      Component.literal(reason)
              )
      );
    });
  }
}
