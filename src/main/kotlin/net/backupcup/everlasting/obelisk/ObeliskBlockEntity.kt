package net.backupcup.everlasting.obelisk

import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.assign.AssignBlocks
import net.backupcup.everlasting.assign.AssignEffects
import net.backupcup.everlasting.config.configHandler
import net.backupcup.everlasting.inventory.ImplementedInventory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import kotlin.properties.Delegates

class ObeliskBlockEntity(
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(
    AssignBlocks.EVERLASTING_OBELISK_BLOCK_ENTITY,
    pos,
    state
), NamedScreenHandlerFactory, ImplementedInventory {
    var inventory = DefaultedList.ofSize(1, ItemStack.EMPTY)
    val FUEL_SLOT = 0

    class ObeliskInventory(size: Int, blockEntity: ObeliskBlockEntity): SimpleInventory(size) {
        override fun readNbtList(nbtList: NbtList?) {
            if (nbtList != null) {
                for (i in 0 until nbtList.size) {
                    val compound = nbtList.getCompound(i)
                    val stack = ItemStack.fromNbt(compound)
                    if(stack.isEmpty) continue
                    val slot = compound.getInt("slot")
                    this.setStack(slot, stack)
                }
            }
        }

        override fun toNbtList(): NbtList {
            val list = NbtList()
            for (i in 0 until this.size()) {
                val compound = NbtCompound()
                compound.putInt("slot", i)
                val stack = this.getStack(i)
                stack.writeNbt(compound)
                list.add(compound)
            }
            return list
        }
    }

    //Config Values
    val maxCharge = configHandler.getConfigValue("ObeliskChargeMax").toInt()
    val chargePerPlayer = configHandler.getConfigValue("ObeliskChargedUsedPerPlayer").toInt()
    val chargePerSculk = configHandler.getConfigValue("ObeliskChargePerSculk").toInt()
    val effectRadius = configHandler.getConfigValue("ObeliskRadius").toDouble()


    private var charge: Int by Delegates.observable(0) { _, _, _ ->
        markDirty()
    }
    private var playerAmount: Int by Delegates.observable(0) { _, _, _ ->
        markDirty()
    }
    private var soundPlayed: Int by Delegates.observable(1) { _, _, _ ->
        markDirty()
    }

    private var propertyDelegate: PropertyDelegate = object : PropertyDelegate {

        override fun get(index: Int): Int {
            return when (index) {
                0 -> charge
                1 -> playerAmount
                2 -> soundPlayed
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> charge = value
                1 -> playerAmount = value
                2 -> soundPlayed = value
            }
        }

        override fun size(): Int {
            return 3
        }
    }

    override fun getDisplayName(): Text {
        return Text.translatable("block.everlasting.everlasting_obelisk")
    }

    override fun markDirty() {
        world?.updateListeners(pos, cachedState, cachedState, 3)
    }

    override fun getItems(): DefaultedList<ItemStack> {
        return inventory
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        return ObeliskScreenHandler(syncId, playerInventory, this, inventory as Inventory, this.propertyDelegate)
    }

    override fun writeNbt(nbt: NbtCompound?) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
        nbt?.putInt("everlastingObelisk.charge", charge)
        nbt?.putInt("everlastingObelisk.playerAmount", playerAmount)
        nbt?.putInt("everlastingObelisk.soundPlayed", soundPlayed)
    }

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, inventory)
        charge = nbt?.getInt("everlastingObelisk.charge")!!
        playerAmount = nbt.getInt("everlastingObelisk.playerAmount")
        soundPlayed = nbt.getInt("everlastingObelisk.soundPlayed")
    }

    companion object {
        fun tick(world: World?, pos: BlockPos?, state: BlockState?, blockEntity: ObeliskBlockEntity?) {
            if (world != null && pos != null && blockEntity != null) {
                if(world.isClient) return
                if(blockEntity.playerAmount > 0) {
                    if(blockEntity.charge <= (blockEntity.maxCharge - blockEntity.chargePerSculk) && blockEntity.inventory[blockEntity.FUEL_SLOT].isOf(Items.SCULK)) {
                        blockEntity.consumeItem()
                        blockEntity.addCharge()
                        markDirty(world, pos, state)
                    }
                }
                if(world.time % 100L == 0L) {

                    if(world.getBlockState(pos.up(1)).block == Blocks.LIGHTNING_ROD) {
                        val box = Box(pos).expand(blockEntity.effectRadius).stretch(0.0, world.height.toDouble(), 0.0)
                        val list = world.getNonSpectatingEntities(
                            PlayerEntity::class.java, box
                        )
                        if (list.isNotEmpty()) {
                            blockEntity.playerAmount = list.size
                            if(!blockEntity.isChargeZero()) {
                                blockEntity.playActivationSound()
                                Everlasting.logger.info(blockEntity.charge.toString()) //debug
                                for (playerEntity in list) {
                                    playerEntity.addStatusEffect(StatusEffectInstance(AssignEffects.EVERLASTING, 101, 0, true, true))
                                    blockEntity.decreaseCharge()
                                }
                                markDirty(world, pos, state)
                            } else {
                                blockEntity.playDeactivationSound()
                                markDirty(world, pos, state)
                            }
                        } else {
                            blockEntity.playDeactivationSound()
                            markDirty(world, pos, state)
                        }
                    } else {
                        blockEntity.playDeactivationSound()
                        markDirty(world, pos, state)
                    }
                }
            }
        }
    }

    private fun isChargeZero(): Boolean {
        return this.charge <= 0
    }

    private fun consumeItem() {
        if(this.inventory[FUEL_SLOT].isOf(Items.SCULK)) this.removeStack(FUEL_SLOT, 1)
    }

    private fun addCharge() {
        this.charge += this.chargePerSculk
        if (this.charge >= this.maxCharge) this.charge = this.maxCharge
    }

    private fun decreaseCharge() {
        this.charge -= this.chargePerPlayer
    }

    private fun playActivationSound() {
        if (this.soundPlayed == 1) {
            world?.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS)
            this.soundPlayed = 0
        }
    }

    private fun playDeactivationSound() {
        if (this.soundPlayed == 0) {
            world?.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS)
            this.soundPlayed = 1
        }
    }
}