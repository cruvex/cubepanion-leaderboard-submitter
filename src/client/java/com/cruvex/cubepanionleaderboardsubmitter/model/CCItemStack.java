package com.cruvex.cubepanionleaderboardsubmitter.model;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface CCItemStack {

  List<String> getToolTips();

  CCCompoundTag getCustomDataTag();

  String texture();

  ItemStack asVanilla();

}
