package com.mapcompass.journeymap;

import com.mapcompass.network.SetCompassPacket;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.event.PopupMenuEvent;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.FullscreenEventRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class MapCompassPlugin implements IClientPlugin {

    @SuppressWarnings("unused")
    private IClientAPI clientAPI;

    @Override
    public String getModId() {
        return "mapcompass";
    }

    @Override
    public void initialize(IClientAPI api) {
        this.clientAPI = api;
        FullscreenEventRegistry.FULLSCREEN_POPUP_MENU_EVENT.subscribe("mapcompass", this::onFullscreenPopupMenu);
    }

    private void onFullscreenPopupMenu(PopupMenuEvent.FullscreenPopupMenuEvent event) {
        ResourceKey<Level> dimension = event.getFullscreen().getUiState().dimension;
        event.getPopupMenu().addMenuItem("Imposta Bussola", blockPos ->
            PacketDistributor.sendToServer(new SetCompassPacket(blockPos.getX(), blockPos.getZ(), dimension))
        );
    }
}
