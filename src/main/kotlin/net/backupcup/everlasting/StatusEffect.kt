package net.backupcup.everlasting

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class StatusEffect(statusEffectCategory: StatusEffectCategory, i: Int): StatusEffect(statusEffectCategory, i) {
    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return true
    }

    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
    }
}
