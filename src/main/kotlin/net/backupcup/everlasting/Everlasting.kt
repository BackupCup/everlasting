package net.backupcup.everlasting

import net.backupcup.everlasting.assign.AssignBlocks
import net.backupcup.everlasting.assign.AssignEffects
import net.backupcup.everlasting.assign.AssignScreenHandlers
import net.backupcup.everlasting.config.configHandler
import net.backupcup.everlasting.enchantment.RestoringEnchantment
import net.backupcup.everlasting.mixin.BrewingRecipeRegistryMixin
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.potion.Potions
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Everlasting : ModInitializer {
	val logger = LoggerFactory.getLogger("everlasting")
	const val MOD_ID = "everlasting"
	val restoringSlots = arrayOf(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)

	override fun onInitialize() {
		AssignEffects.EVERLASTING

		if (AssignEffects.PotionEverlastingEnable) {
			AssignEffects.EVERLASTING_POTION
			BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, Items.NETHERITE_SCRAP, AssignEffects.EVERLASTING_POTION)
		}

		if (AssignEffects.PotionEverlastingLargeEnable) {
			AssignEffects.EVERLASTING_POTION_LARGE
			BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(AssignEffects.EVERLASTING_POTION, Items.REDSTONE, AssignEffects.EVERLASTING_POTION_LARGE)
		}

		if(configHandler.getConfigValue("ObeliskEnable").toBoolean()) {
			AssignScreenHandlers.OBELISK_SCREEN_HANDLER
			Registry.register(Registries.BLOCK, Identifier(MOD_ID, "everlasting_obelisk"), AssignBlocks.EVERLASTING_OBELISK)
			Registry.register(Registries.ITEM, Identifier(MOD_ID, "everlasting_obelisk"), BlockItem(AssignBlocks.EVERLASTING_OBELISK, FabricItemSettings()))
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
				.register(ModifyEntries { content: FabricItemGroupEntries ->
					content.add(
						AssignBlocks.EVERLASTING_OBELISK
					)
				})
		}

		Registry.register(Registries.ENCHANTMENT, Identifier(MOD_ID, "restoring"), RestoringEnchantment.INSTANCE)

		logger.info("Everlasting Registered")

		//Last issue: the delegate doesn't get transferred from BlockEntity to ScreenHandler and to Screen by extension

		//idea: add lore like "Hold [SHIFT] for more Information" to the obelisk
		//add configurable charge vars to the obelisk
	}
}