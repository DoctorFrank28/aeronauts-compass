package com.mapcompass.menu;

import com.mapcompass.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NavigatorTableMenu extends AbstractContainerMenu {

    public final BlockPos blockPos;
    private final Container container;

    // Client-side
    public NavigatorTableMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, new SimpleContainer(1), buf.readBlockPos());
    }

    // Server-side
    public NavigatorTableMenu(int id, Inventory playerInventory, Container container, BlockPos blockPos) {
        super(ModMenuTypes.NAVIGATOR_TABLE.get(), id);
        this.container = container;
        this.blockPos = blockPos;

        // Compass slot (item pos x=8, y=26 → bg at 7,25)
        addSlot(new Slot(container, 0, 8, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.COMPASS);
            }
        });

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }
}
