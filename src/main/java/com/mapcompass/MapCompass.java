package com.mapcompass;

import com.mapcompass.network.SetCompassPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("mapcompass")
public class MapCompass {

    public MapCompass(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPackets);
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
            SetCompassPacket.TYPE,
            SetCompassPacket.STREAM_CODEC,
            SetCompassPacket::handleOnServer
        );
    }
}
