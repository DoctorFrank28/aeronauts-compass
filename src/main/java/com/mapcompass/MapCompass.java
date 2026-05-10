package com.mapcompass;

import com.mapcompass.init.ModBlockEntities;
import com.mapcompass.init.ModBlocks;
import com.mapcompass.init.ModMenuTypes;
import com.mapcompass.network.ApplyCompassPacket;
import com.mapcompass.network.SetCompassPacket;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("mapcompass")
public class MapCompass {

    public MapCompass(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerPackets);
        modEventBus.addListener(this::addCreativeTab);
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModBlocks.NAVIGATOR_TABLE_ITEM.get());
        }
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SetCompassPacket.TYPE, SetCompassPacket.STREAM_CODEC, SetCompassPacket::handleOnServer);
        registrar.playToServer(ApplyCompassPacket.TYPE, ApplyCompassPacket.STREAM_CODEC, ApplyCompassPacket::handleOnServer);
    }
}
