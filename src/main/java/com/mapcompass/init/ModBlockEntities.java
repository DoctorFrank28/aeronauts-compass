package com.mapcompass.init;

import com.mapcompass.block.NavigatorTableBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "mapcompass");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NavigatorTableBlockEntity>> NAVIGATOR_TABLE =
        BLOCK_ENTITY_TYPES.register("navigator_table", () ->
            BlockEntityType.Builder.of(NavigatorTableBlockEntity::new, ModBlocks.NAVIGATOR_TABLE.get()).build(null));
}
