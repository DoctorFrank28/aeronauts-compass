package com.mapcompass.block;

import com.mapcompass.init.ModBlockEntities;
import com.mapcompass.menu.NavigatorTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NavigatorTableBlockEntity extends BlockEntity implements MenuProvider {

    public final SimpleContainer inventory = new SimpleContainer(1);

    public NavigatorTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NAVIGATOR_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mapcompass.navigator_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new NavigatorTableMenu(id, playerInventory, this.inventory, this.worldPosition);
    }
}
