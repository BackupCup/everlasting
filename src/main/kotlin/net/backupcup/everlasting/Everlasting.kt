package net.backupcup.everlasting

import net.backupcup.everlasting.assign.RegisterBlocks
import net.backupcup.everlasting.assign.RegisterEffects
import net.backupcup.everlasting.assign.RegisterItems
import net.backupcup.everlasting.assign.RegisterScreenHandlers
import net.backupcup.everlasting.enchantment.RestoringEnchantment
import net.backupcup.everlasting.mixin.BrewingRecipeRegistryMixin
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents.Modify
import net.fabricmc.fabric.api.loot.v2.LootTableSource
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.potion.Potions
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory


object Everlasting : ModInitializer {
	val logger = LoggerFactory.getLogger("everlasting")
	const val MOD_ID = "everlasting"
	val restoringSlots = arrayOf(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
	val capsuleLootTables = arrayOf(LootTables.ANCIENT_CITY_CHEST, LootTables.BASTION_TREASURE_CHEST, LootTables.END_CITY_TREASURE_CHEST)

	override fun onInitialize() {
		RegisterEffects.EVERLASTING

		RegisterEffects.EVERLASTING_POTION
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, Items.NETHERITE_SCRAP, RegisterEffects.EVERLASTING_POTION)

		RegisterEffects.EVERLASTING_POTION_LARGE
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(RegisterEffects.EVERLASTING_POTION, Items.REDSTONE, RegisterEffects.EVERLASTING_POTION_LARGE)

		RegisterItems.CAPSULE
		LootTableEvents.MODIFY.register(Modify {
			resourceManager: ResourceManager?,
			lootManager: LootManager?,
			id: Identifier?, tableBuilder:
			LootTable.Builder,
			source: LootTableSource ->
			if (id != null) {
				if (source.isBuiltin && capsuleLootTables.contains(id)) {
					val poolBuilder = LootPool.builder()
						.rolls(UniformLootNumberProvider.create(0.25f, 1f))
						.with(ItemEntry.builder(RegisterItems.CAPSULE).weight(1))
					tableBuilder.pool(poolBuilder)
				}
			}
		})
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
			.register(ModifyEntries { content: FabricItemGroupEntries ->
				content.add(
					RegisterItems.CAPSULE
				)
			})

		RegisterScreenHandlers.OBELISK_SCREEN_HANDLER
		Registry.register(Registries.BLOCK, Identifier(MOD_ID, "everlasting_obelisk"), RegisterBlocks.EVERLASTING_OBELISK)
		Registry.register(Registries.ITEM, Identifier(MOD_ID, "everlasting_obelisk"), BlockItem(RegisterBlocks.EVERLASTING_OBELISK, FabricItemSettings()))
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
			.register(ModifyEntries { content: FabricItemGroupEntries ->
				content.add(
					RegisterBlocks.EVERLASTING_OBELISK
				)
			})

		Registry.register(Registries.ENCHANTMENT, Identifier(MOD_ID, "restoring"), RestoringEnchantment.INSTANCE)

		logger.info("Everlasting Registered")
	}
}
