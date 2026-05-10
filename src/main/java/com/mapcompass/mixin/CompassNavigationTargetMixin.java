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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.Optional;

// Soft-depends on Create: Aeronautics. @Pseudo causes this mixin to be silently skipped
// if the target class is absent (i.e. CA not installed).
@Pseudo
@Mixin(targets = "dev.simulated_team.simulated.content.navigation_targets.CompassNavigationTarget", remap = false)
public class CompassNavigationTargetMixin {

    @Inject(method = "getTarget", at = @At("HEAD"), cancellable = true, remap = false)
    private void mapcompass_interceptGetTarget(CallbackInfoReturnable<Vec3> cir) {
        // CA ignores vanilla LODESTONE_TRACKER and reads its own UUID component instead.
        // Walk all ItemStack fields on this instance and redirect if one carries a lodestone target.
        Class<?> clazz = this.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() != ItemStack.class) continue;
                try {
                    field.setAccessible(true);
                    ItemStack stack = (ItemStack) field.get(this);
                    if (stack == null) continue;
                    LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
                    if (tracker == null) continue;
                    Optional<GlobalPos> target = tracker.target();
                    if (target.isEmpty()) continue;
                    BlockPos pos = target.get().pos();
                    cir.setReturnValue(Vec3.atCenterOf(pos));
                    return;
                } catch (Exception ignored) {}
            }
            clazz = clazz.getSuperclass();
        }
    }
}
