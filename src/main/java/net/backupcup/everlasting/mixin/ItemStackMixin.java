package net.backupcup.everlasting.mixin;

import net.backupcup.everlasting.config.configHandler;
import net.backupcup.everlasting.enchantment.RestoringEnchantment;
import net.backupcup.everlasting.assign.RegisterEffects;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @WrapWithCondition(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z",
                       at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
    private boolean shouldPreventDamage(ItemStack stack, int amount, int damage, Random random, ServerPlayerEntity player) {
        if(!configHandler.INSTANCE.containsItemID(stack.getItem().getTranslationKey().trim())) {
            if (player.hasStatusEffect(RegisterEffects.INSTANCE.getEVERLASTING())) {
                return false;
            }

            if (RestoringEnchantment.Companion.shouldPreventDamage(stack, damage, player)) {
                player.addExperience(-damage);
                return false;
            }
        }

        return true;
    }


}