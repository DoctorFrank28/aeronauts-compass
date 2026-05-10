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
        this(id, playerInventory, new SimpleContainer(2), buf.readBlockPos());
    }

    // Server-side
    public NavigatorTableMenu(int id, Inventory playerInventory, Container container, BlockPos blockPos) {
        super(ModMenuTypes.NAVIGATOR_TABLE.get(), id);
        this.container = container;
        this.blockPos = blockPos;

        // Compass slot at (8,26); map slot at (28,26)
        addSlot(new Slot(container, 0, 8, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.COMPASS);
            }
        });
        addSlot(new Slot(container, 1, 28, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.FILLED_MAP);
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
        if (index < 2) {
            // table slot → player inventory
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // player inventory → appropriate table slot
            if (stack.is(Items.COMPASS)) {
                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else if (stack.is(Items.FILLED_MAP)) {
                if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }
}
