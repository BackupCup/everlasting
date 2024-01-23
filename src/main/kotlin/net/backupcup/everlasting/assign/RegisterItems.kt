package net.backupcup.everlasting.assign

import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.items.CapsuleItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object RegisterItems {
    val capsuleDurability = Everlasting.getConfig()?.CapsuleDurability()

    val CAPSULE: CapsuleItem = Registry.register(
        Registries.ITEM,
        Identifier(Everlasting.MOD_ID, "everlasting_capsule"),
        CapsuleItem(FabricItemSettings()
            .maxCount(1)
            .maxDamage(capsuleDurability?: 250)
            .rarity(Rarity.RARE)
        )
    )
}