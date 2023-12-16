package net.backupcup.everlasting.assign

import net.backupcup.everlasting.StatusEffect
import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.config.configHandler
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.potion.Potion
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object RegisterEffects {
    val EVERLASTING : StatusEffect = Registry.register(Registries.STATUS_EFFECT, Identifier(Everlasting.MOD_ID, "everlasting"),
        StatusEffect(StatusEffectCategory.BENEFICIAL, 0x06E5B5))

    val PotionEverlastingDuration = configHandler.getConfigValue("PotionEverlastingDuration").toInt()
    var EVERLASTING_POTION : Potion

    val PotionEverlastingLargeDuration = configHandler.getConfigValue("PotionEverlastingLargeDuration").toInt()
    var EVERLASTING_POTION_LARGE : Potion

    private const val ticksInSecond = 20

    init {
        EVERLASTING_POTION = Registry.register(
        Registries.POTION, Identifier(Everlasting.MOD_ID, "everlasting_potion"),
        Potion(StatusEffectInstance(EVERLASTING, PotionEverlastingDuration.times(ticksInSecond), 0)))

        EVERLASTING_POTION_LARGE = Registry.register(
            Registries.POTION, Identifier(Everlasting.MOD_ID, "everlasting_potion_2"),
            Potion(StatusEffectInstance(EVERLASTING, PotionEverlastingLargeDuration.times(ticksInSecond), 0)))
    }
}