package net.backupcup.everlasting.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {
    @Accessor("tridentStack")
    abstract ItemStack getTridentStack();
    @Accessor("dealtDamage")
    abstract void setDealtDamage(boolean dealtDamage);

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void loyaltyVoid(CallbackInfo ci) {
        if (((TridentEntity)(Object)this).getY() <= -64 && EnchantmentHelper.getLevel(Enchantments.LOYALTY, getTridentStack()) > 0)
            setDealtDamage(true);
    }
}
