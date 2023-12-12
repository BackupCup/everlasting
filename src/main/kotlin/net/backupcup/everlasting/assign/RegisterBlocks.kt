package net.backupcup.everlasting.assign

import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.obelisk.ObeliskBlock
import net.backupcup.everlasting.obelisk.ObeliskBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.MapColor
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object RegisterBlocks {

    val EVERLASTING_OBELISK = ObeliskBlock(
        FabricBlockSettings.create()
        .strength(3.0f, 1200.0f)
        .requiresTool()
        .luminance(7)
        .sounds(BlockSoundGroup.NETHERITE).mapColor(MapColor.CYAN)
        .nonOpaque()
    )

    val EVERLASTING_OBELISK_BLOCK_ENTITY : BlockEntityType<ObeliskBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier(Everlasting.MOD_ID, "everlasting_obelisk_be"),
        FabricBlockEntityTypeBuilder.create({ pos : BlockPos, state : BlockState -> ObeliskBlockEntity(pos, state) }, EVERLASTING_OBELISK).build())
}