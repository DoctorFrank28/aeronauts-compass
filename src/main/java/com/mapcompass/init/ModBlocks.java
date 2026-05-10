package com.mapcompass.init;

import com.mapcompass.block.NavigatorTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("mapcompass");
    public static final DeferredRegister.Items ITEMS   = DeferredRegister.createItems("mapcompass");

    public static final DeferredBlock<NavigatorTableBlock> NAVIGATOR_TABLE =
        BLOCKS.registerBlock("navigator_table", NavigatorTableBlock::new,
            BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD));

    public static final DeferredItem<BlockItem> NAVIGATOR_TABLE_ITEM =
        ITEMS.registerSimpleBlockItem(NAVIGATOR_TABLE);
}
