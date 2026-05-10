package com.mapcompass.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SetCompassPacket(int x, int z, ResourceKey<Level> dimension, String name) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOCATION = ResourceLocation.fromNamespaceAndPath("mapcompass", "set_compass");
    public static final Type<SetCompassPacket> TYPE = new Type<>(ID_LOCATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, SetCompassPacket> STREAM_CODEC = StreamCodec.of(
        SetCompassPacket::encode,
        SetCompassPacket::decode
    );

    private static void encode(RegistryFriendlyByteBuf buf, SetCompassPacket packet) {
        buf.writeInt(packet.x());
        buf.writeInt(packet.z());
        buf.writeResourceKey(packet.dimension());
        buf.writeUtf(packet.name());
    }

    private static SetCompassPacket decode(RegistryFriendlyByteBuf buf) {
        int x = buf.readInt();
        int z = buf.readInt();
        ResourceKey<Level> dimension = buf.readResourceKey(Registries.DIMENSION);
        String name = buf.readUtf();
        return new SetCompassPacket(x, z, dimension, name);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(SetCompassPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            InteractionHand hand = getCompassHand(player);
            if (hand == null) {
                player.sendSystemMessage(Component.translatable("mapcompass.message.no_compass"));
                return;
            }

            GlobalPos globalPos = GlobalPos.of(packet.dimension(), new BlockPos(packet.x(), 64, packet.z()));
            LodestoneTracker tracker = new LodestoneTracker(Optional.of(globalPos), false);

            ItemStack newCompass = new ItemStack(Items.COMPASS);
            newCompass.set(DataComponents.LODESTONE_TRACKER, tracker);

            Component displayName = packet.name().isEmpty()
                ? Component.translatable("mapcompass.item.compass_name", packet.x(), packet.z())
                : Component.literal(packet.name());
            newCompass.set(DataComponents.CUSTOM_NAME, displayName);

            player.setItemInHand(hand, newCompass);
            player.sendSystemMessage(Component.translatable("mapcompass.message.compass_set", packet.x(), packet.z()));
        });
    }

    private static InteractionHand getCompassHand(ServerPlayer player) {
        if (player.getMainHandItem().is(Items.COMPASS)) return InteractionHand.MAIN_HAND;
        if (player.getOffhandItem().is(Items.COMPASS)) return InteractionHand.OFF_HAND;
        return null;
    }
}
