package com.mapcompass.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// Soft-depends on Create: Aeronautics. @Pseudo causes this mixin to be silently skipped
// if the target class is absent (i.e. CA not installed).
@Pseudo
@Mixin(targets = "dev.simulated_team.simulated.content.navigation_targets.CompassNavigationTarget", remap = false)
public class CompassNavigationTargetMixin {

    // getTarget(NavTableBlockEntity, ItemStack) -> Vec3
    // NavTableBlockEntity is a CA type unavailable at compile time; @Coerce lets us use Object instead.
    @Inject(
        method = "getTarget(Ldev/simulated_team/simulated/content/blocks/nav_table/NavTableBlockEntity;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/phys/Vec3;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void mapcompass_interceptGetTarget(
        @Coerce Object blockEntity,
        ItemStack stack,
        CallbackInfoReturnable<Vec3> cir
    ) {
        LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
        if (tracker == null) return;
        Optional<GlobalPos> target = tracker.target();
        if (target.isEmpty()) return;
        BlockPos pos = target.get().pos();
        cir.setReturnValue(Vec3.atCenterOf(pos));
    }
}
