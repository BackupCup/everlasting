package net.backupcup.everlasting

import net.backupcup.everlasting.assign.RegisterBlocks
import net.backupcup.everlasting.assign.RegisterEffects
import net.backupcup.everlasting.assign.RegisterItems
import net.backupcup.everlasting.assign.RegisterScreenHandlers
import net.backupcup.everlasting.config.Config
import net.backupcup.everlasting.enchantment.RestoringEnchantment
import net.backupcup.everlasting.mixin.BrewingRecipeRegistryMixin
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SyncDataPackContents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents.Modify
import net.fabricmc.fabric.api.loot.v2.LootTableSource
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
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
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object Everlasting : ModInitializer {
	const val MOD_ID = "everlasting"
	val LOGGER: Logger? = LoggerFactory.getLogger(MOD_ID)

	val SYNC_CONFIG_PACKET = Identifier.of(MOD_ID, "sync_config")
	private var config: Config? = null

	fun getConfig(): Config? {
		return config
	}

	fun setConfig(config: Config) {
		Everlasting.config = config
	}

	val restoringSlots = arrayOf(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
	val capsuleLootTables = arrayOf(LootTables.ANCIENT_CITY_CHEST, LootTables.BASTION_TREASURE_CHEST, LootTables.END_CITY_TREASURE_CHEST)

	override fun onInitialize() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
			.registerReloadListener(object : SimpleSynchronousResourceReloadListener {
				override fun getFabricId(): Identifier {
					return Identifier.of(MOD_ID, "config")!!
				}

				override fun reload(manager: ResourceManager) {
					config = Config.load()
				}
			})

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(SyncDataPackContents { player: ServerPlayerEntity, joined: Boolean ->
			val buf = PacketByteBufs.create()
			config!!.writeToClient(buf)
			ServerPlayNetworking.send(player, SYNC_CONFIG_PACKET, buf)
			if (Config.lastError != null) {
				player.sendMessage(
					Text.literal("[${MOD_ID.uppercase()}]: ")
						.append(Config.lastError).formatted(Formatting.RED)
				)
			}
		})

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

		LOGGER?.info("Everlasting Registered")
	}
}
