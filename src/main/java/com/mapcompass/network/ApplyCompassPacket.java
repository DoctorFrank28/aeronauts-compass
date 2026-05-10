package com.mapcompass.network;

import com.mapcompass.block.NavigatorTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public record ApplyCompassPacket(BlockPos tablePos, int x, int z, String name) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath("mapcompass", "apply_compass");
    public static final Type<ApplyCompassPacket> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, ApplyCompassPacket> STREAM_CODEC = StreamCodec.of(
        ApplyCompassPacket::encode,
        ApplyCompassPacket::decode
    );

    private static void encode(RegistryFriendlyByteBuf buf, ApplyCompassPacket p) {
        buf.writeBlockPos(p.tablePos());
        buf.writeInt(p.x());
        buf.writeInt(p.z());
        buf.writeUtf(p.name());
    }

    private static ApplyCompassPacket decode(RegistryFriendlyByteBuf buf) {
        return new ApplyCompassPacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(ApplyCompassPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (!(player.level().getBlockEntity(packet.tablePos()) instanceof NavigatorTableBlockEntity be)) return;

            var inv = be.inventory;
            if (inv.isEmpty() || !inv.getItem(0).is(Items.COMPASS)) {
                player.sendSystemMessage(Component.translatable("mapcompass.message.no_compass"));
                return;
            }

            var stack = inv.getItem(0).copy();
            GlobalPos globalPos = GlobalPos.of(player.level().dimension(), new BlockPos(packet.x(), 64, packet.z()));
            stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(globalPos), false));

            Component displayName = packet.name().isBlank()
                ? Component.translatable("mapcompass.item.compass_name", packet.x(), packet.z())
                : Component.literal(packet.name());
            stack.set(DataComponents.CUSTOM_NAME, displayName);
            stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("X: " + packet.x() + "  Z: " + packet.z())
                    .withStyle(s -> s.withColor(0xAAAAAA).withItalic(false)),
                Component.translatable("mapcompass.lore.reset_hint")
                    .withStyle(s -> s.withColor(0x777777).withItalic(true))
            )));

            inv.setItem(0, stack);
            be.setChanged();
            player.sendSystemMessage(Component.translatable("mapcompass.message.compass_set", packet.x(), packet.z()));
        });
    }
}
