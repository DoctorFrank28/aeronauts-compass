package com.mapcompass.init;

import com.mapcompass.recipe.ResetCompassRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, "mapcompass");

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<ResetCompassRecipe>> RESET_COMPASS =
        RECIPE_SERIALIZERS.register("reset_compass", () -> new SimpleCraftingRecipeSerializer<>(ResetCompassRecipe::new));
}
