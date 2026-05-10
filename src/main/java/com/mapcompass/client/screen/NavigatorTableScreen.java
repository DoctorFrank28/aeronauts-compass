package com.mapcompass.client.screen;

import com.mapcompass.menu.NavigatorTableMenu;
import com.mapcompass.network.ApplyCompassPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.MapDecorations;
import net.neoforged.neoforge.network.PacketDistributor;

public class NavigatorTableScreen extends AbstractContainerScreen<NavigatorTableMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("mapcompass", "textures/gui/navigator_table.png");

    private EditBox xField;
    private EditBox zField;
    private EditBox nameField;

    private ItemStack lastCompassStack = ItemStack.EMPTY;
    private ItemStack lastMapStack = ItemStack.EMPTY;

    public NavigatorTableScreen(NavigatorTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void init() {
        super.init();

        xField = new EditBox(font, leftPos + 58, topPos + 16, 52, 12, Component.literal("X"));
        xField.setMaxLength(8);
        xField.setFilter(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        xField.setValue("0");
        addRenderableWidget(xField);

        zField = new EditBox(font, leftPos + 58, topPos + 30, 52, 12, Component.literal("Z"));
        zField.setMaxLength(8);
        zField.setFilter(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        zField.setValue("0");
        addRenderableWidget(zField);

        nameField = new EditBox(font, leftPos + 86, topPos + 46, 82, 12, Component.literal("Name"));
        nameField.setMaxLength(32);
        addRenderableWidget(nameField);

        addRenderableWidget(
            Button.builder(Component.translatable("mapcompass.action.apply"), btn -> applyCompass())
                .bounds(leftPos + 116, topPos + 58, 52, 12)
                .build()
        );
    }

    private void checkMapSlot() {
        ItemStack current = menu.getSlot(1).getItem();
        if (ItemStack.matches(current, lastMapStack)) return;
        lastMapStack = current.copy();
        if (current.isEmpty()) {
            xField.setEditable(true);
            zField.setEditable(true);
            return;
        }
        MapDecorations decorations = current.get(DataComponents.MAP_DECORATIONS);
        if (decorations == null) return;
        MapDecorations.Entry entry = decorations.decorations().get("+");
        if (entry == null) return;
        xField.setValue(String.valueOf((int) entry.x()));
        zField.setValue(String.valueOf((int) entry.z()));
        xField.setEditable(false);
        zField.setEditable(false);
        // suggerisci nome dalla mappa solo se il campo è vuoto
        if (nameField.getValue().isBlank()) {
            Component mapName = current.get(DataComponents.CUSTOM_NAME);
            if (mapName != null) nameField.setValue(mapName.getString());
        }
    }

    private void checkCompassSlot() {
        ItemStack current = menu.getSlot(0).getItem();
        if (!ItemStack.matches(current, lastCompassStack)) {
            lastCompassStack = current.copy();
            populateFromCompass(current);
        }
    }

    private void populateFromCompass(ItemStack stack) {
        LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
        if (tracker != null && tracker.target().isPresent()) {
            BlockPos pos = tracker.target().get().pos();
            xField.setValue(String.valueOf(pos.getX()));
            zField.setValue(String.valueOf(pos.getZ()));
        } else if (stack.isEmpty()) {
            xField.setValue("0");
            zField.setValue("0");
            nameField.setValue("");
        }
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            nameField.setValue(customName.getString());
        }
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
        // Ghost hint: icon + parchment overlay (~60% opacity) to simulate transparency
        // renderFakeItem ignores alpha, so we overlay the bg colour instead
        if (menu.getSlot(0).getItem().isEmpty()) {
            graphics.renderFakeItem(new ItemStack(Items.COMPASS), leftPos + 8, topPos + 26);
            graphics.fill(leftPos + 8, topPos + 26, leftPos + 24, topPos + 42, 0x99E8D5A3);
        }
        if (menu.getSlot(1).getItem().isEmpty()) {
            graphics.renderFakeItem(new ItemStack(Items.FILLED_MAP), leftPos + 28, topPos + 26);
            graphics.fill(leftPos + 28, topPos + 26, leftPos + 44, topPos + 42, 0x99E8D5A3);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x4A2E0A, false);
        graphics.drawString(font, "X:", 50, 19, 0x4A2E0A, false);
        graphics.drawString(font, "Z:", 50, 33, 0x4A2E0A, false);
        graphics.drawString(font, Component.translatable("mapcompass.label.name"), 50, 49, 0x4A2E0A, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x4A2E0A, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (xField.isFocused() || zField.isFocused() || nameField.isFocused()) {
            if (keyCode == 256) { onClose(); return true; } // ESC chiude sempre
            // Passa il tasto al widget focalizzato, poi consuma l'evento per bloccare
            // il check dell'inventory key (es. E) in AbstractContainerScreen
            if (getFocused() != null) getFocused().keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        checkCompassSlot();
        checkMapSlot();
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
