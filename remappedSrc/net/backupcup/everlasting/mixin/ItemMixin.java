package net.backupcup.everlasting.mixin;

import net.backupcup.everlasting.Everlasting;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemEntity.class)
public abstract class ItemMixin {

    @Shadow public abstract int getItemAge();

    @Shadow private int itemAge;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void rarityDespawn(CallbackInfo ci) {
        if (Objects.requireNonNull(Everlasting.INSTANCE.getConfig()).EnableSaveRarity()) {
            if(((ItemEntity)(Object)this).getStack().getRarity() != Everlasting.INSTANCE.getConfig().ItemSaveRarity() && this.getItemAge() >= 5000) {
                this.itemAge = 0;
            }
        }
    }
}
