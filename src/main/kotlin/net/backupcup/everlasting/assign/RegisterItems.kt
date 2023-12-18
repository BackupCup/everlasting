package net.backupcup.everlasting.assign

import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.config.configHandler
import net.backupcup.everlasting.items.CapsuleItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object RegisterItems {
    val capsuleDurability = configHandler.getConfigValue("CapsuleDurability").toInt()

    val CAPSULE: CapsuleItem = Registry.register(
        Registries.ITEM,
        Identifier(Everlasting.MOD_ID, "everlasting_capsule"),
        CapsuleItem(FabricItemSettings()
            .maxCount(1)
            .maxDamage(capsuleDurability)
            .rarity(Rarity.RARE)
        )
    )
}