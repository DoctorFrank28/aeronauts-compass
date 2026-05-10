package com.mapcompass.client.screen;

import com.mapcompass.menu.NavigatorTableMenu;
import com.mapcompass.network.ApplyCompassPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class NavigatorTableScreen extends AbstractContainerScreen<NavigatorTableMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("mapcompass", "textures/gui/navigator_table.png");

    private EditBox xField;
    private EditBox zField;
    private EditBox nameField;

    public NavigatorTableScreen(NavigatorTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void init() {
        super.init();

        xField = new EditBox(font, leftPos + 44, topPos + 16, 52, 12, Component.literal("X"));
        xField.setMaxLength(8);
        xField.setFilter(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        xField.setValue("0");
        addRenderableWidget(xField);

        zField = new EditBox(font, leftPos + 44, topPos + 32, 52, 12, Component.literal("Z"));
        zField.setMaxLength(8);
        zField.setFilter(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        zField.setValue("0");
        addRenderableWidget(zField);

        nameField = new EditBox(font, leftPos + 72, topPos + 47, 88, 12, Component.literal("Name"));
        nameField.setMaxLength(32);
        addRenderableWidget(nameField);

        addRenderableWidget(
            Button.builder(Component.translatable("mapcompass.action.apply"), btn -> applyCompass())
                .bounds(leftPos + 120, topPos + 60, 44, 14)
                .build()
        );
    }

    private void applyCompass() {
        int x = parseIntOrZero(xField.getValue());
        int z = parseIntOrZero(zField.getValue());
        PacketDistributor.sendToServer(new ApplyCompassPacket(menu.blockPos, x, z, nameField.getValue()));
    }

    private static int parseIntOrZero(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x4A2E0A, false);
        graphics.drawString(font, "X:",    36, 19, 0x4A2E0A, false);
        graphics.drawString(font, "Z:",    36, 35, 0x4A2E0A, false);
        graphics.drawString(font, Component.translatable("mapcompass.label.name"), 36, 50, 0x4A2E0A, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x4A2E0A, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (xField.isFocused() || zField.isFocused() || nameField.isFocused()) {
            if (keyCode == 256) { onClose(); return true; }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
