package com.cruvex.cubepanionleaderboardsubmitter.mixins;

import com.cruvex.cubepanionleaderboardsubmitter.model.CCCompoundTag;
import com.cruvex.cubepanionleaderboardsubmitter.model.CCItemStack;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Interface.Remap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
@Implements({@Interface(
    iface = CCItemStack.class,
    prefix = "itemStack$",
    remap = Remap.NONE
)})
public abstract class ItemStackMixin implements CCItemStack {

    @Shadow
    public abstract List<net.minecraft.network.chat.Component> getTooltipLines(
            Item.TooltipContext $$0, @Nullable Player $$1, TooltipFlag $$2);

    @Shadow
    public abstract DataComponentPatch getComponentsPatch();

    @Override
    public List<String> getToolTips() {
        List<String> lines = new ArrayList<>();

        var toolTips = this.getTooltipLines(
                Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL);

        for (var mcc : toolTips) {
            lines.add(mcc.getString());
        }

        return lines;
    }

    @Override
    public CCCompoundTag getCustomDataTag() {
        // Implementation for custom data tag if needed
        return null;
    }

    @Override
    public String texture() {
        var profile = this.getComponentsPatch().get(net.minecraft.core.component.DataComponents.PROFILE);
        if (profile == null || profile.isEmpty()) {
            return null;
        }

        return profile.get().partialProfile().properties()
                .get("textures")
                .stream()
                .findFirst()
                .map(Property::value)
                .orElse(null);
    }

    @Override
    public ItemStack asVanilla() {
        return (ItemStack) (Object) this;
    }
}
