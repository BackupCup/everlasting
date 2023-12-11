package net.backupcup.everlasting.obelisk

import net.backupcup.everlasting.assign.AssignBlocks
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.Nameable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class ObeliskBlock(
    settings: Settings?
) : BlockWithEntity(
    settings
), BlockEntityProvider {
    private val SHAPE: VoxelShape = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPE
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return ObeliskBlockEntity(pos, state)
    }

    override fun onStateReplaced(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        newState: BlockState?,
        moved: Boolean
    ) {
        if (newState != null && state != null && world != null) {
            val blockEntity : BlockEntity? = world.getBlockEntity(pos)
            if (blockEntity is Inventory && state.block != newState.block && world.getBlockEntity(pos) is ObeliskBlockEntity) {
                ItemScatterer.spawn(world, pos, blockEntity)
                world.updateComparators(pos, this)
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (world != null) {
            if (world.isClient) {
                return ActionResult.SUCCESS
            }
        }
        if (player != null && state != null) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        }
        return ActionResult.CONSUME
    }

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return checkType(type, AssignBlocks.EVERLASTING_OBELISK_BLOCK_ENTITY, BlockEntityTicker { world, pos, state, blockEntity ->
            if (blockEntity is ObeliskBlockEntity) {
                ObeliskBlockEntity.tick(world, pos, state, blockEntity)
            }
        })
    }
}