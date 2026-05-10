package com.mapcompass.init;

import com.mapcompass.menu.NavigatorTableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(BuiltInRegistries.MENU, "mapcompass");

    public static final DeferredHolder<MenuType<?>, MenuType<NavigatorTableMenu>> NAVIGATOR_TABLE =
        MENU_TYPES.register("navigator_table", () ->
            IMenuTypeExtension.create(NavigatorTableMenu::new));
}
