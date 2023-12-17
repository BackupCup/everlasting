package net.backupcup.everlasting.obelisk

import net.backupcup.everlasting.assign.RegisterBlocks
import net.backupcup.everlasting.assign.RegisterEffects
import net.backupcup.everlasting.config.configHandler
import net.backupcup.everlasting.inventory.ImplementedInventory
import net.fabricmc.fabric.api.client.particle.v1.ParticleRenderEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.LightningRodBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.report.ReporterEnvironment.Server
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import kotlin.random.Random


class ObeliskBlockEntity(
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(
    RegisterBlocks.EVERLASTING_OBELISK_BLOCK_ENTITY,
    pos,
    state
), NamedScreenHandlerFactory, ImplementedInventory {
    private val itemSlot: DefaultedList<ItemStack?> = DefaultedList.ofSize(1, ItemStack.EMPTY)

    //Config Values
    val maxCharge = configHandler.getConfigValue("ObeliskChargeMax").toInt()
    val chargePerPlayer = configHandler.getConfigValue("ObeliskChargedUsedPerPlayer").toInt()
    val chargePerSculk = configHandler.getConfigValue("ObeliskChargePerSculk").toInt()
    val effectRadius = configHandler.getConfigValue("ObeliskRadius").toDouble()

    private var charge: Int = 0
    private var playerAmount: Int = 0
    private var soundPlayed: Boolean = true


    private val propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return charge
        }

        override fun set(index: Int, value: Int) {
            charge = value
        }

        override fun size(): Int {
            return 1
        }
    }

    override fun markDirty() {
        world?.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_LISTENERS)
    }

    override fun getItems(): DefaultedList<ItemStack?> {
        return itemSlot
    }

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, this.itemSlot);
        charge = nbt?.getInt("everlastingObelisk.charge")!!
        playerAmount = nbt.getInt("everlastingObelisk.playerAmount")
        soundPlayed = nbt.getBoolean("everlastingObelisk.soundPlayed")
    }

    override fun writeNbt(nbt: NbtCompound?) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, this.itemSlot);
        nbt?.putInt("everlastingObelisk.charge", charge)
        nbt?.putInt("everlastingObelisk.playerAmount", playerAmount)
        nbt?.putBoolean("everlastingObelisk.soundPlayed", soundPlayed)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        return ObeliskScreenHandler(syncId, playerInventory, this, this.propertyDelegate)
    }

    override fun getDisplayName(): Text? {
        return Text.translatable(cachedState.block.translationKey)
    }

    companion object {
        fun clientTick(world: World?, pos: BlockPos?, state: BlockState?, blockEntity: ObeliskBlockEntity?) {
            markDirty(world, pos, state)
            if(world != null && pos != null && blockEntity != null &&
                world.getBlockState(pos.up(1)).block == Blocks.LIGHTNING_ROD) {
                world.addParticle(
                    ParticleTypes.ELECTRIC_SPARK,
                    blockEntity.pos.x.toDouble() + Random.nextDouble(.25, .75),
                    blockEntity.pos.y.toDouble() + Random.nextDouble(1.0, 2.0),
                    blockEntity.pos.z.toDouble()  + Random.nextDouble(.25, .75),
                    Random.nextDouble(-.025, .025), Random.nextDouble(-.025, .025), Random.nextDouble(-.025, .025))
            }
        }

        fun tick(world: World?, pos: BlockPos?, state: BlockState?, blockEntity: ObeliskBlockEntity?) {
            if (world != null && pos != null && blockEntity != null) {
                if(world.isClient) return
                if(blockEntity.playerAmount > 0) {
                    if(blockEntity.charge <= (blockEntity.maxCharge - blockEntity.chargePerSculk)
                        && blockEntity.itemSlot[0].isOf(Items.SCULK)) {
                            blockEntity.consumeItem()
                            blockEntity.addCharge(blockEntity.chargePerSculk)
                            markDirty(world, pos, state)
                    }
                }

                if(world.getBlockState(pos.up(1)).block == Blocks.LIGHTNING_ROD)
                    if(world.getBlockState(pos.up()).get(LightningRodBlock.POWERED)) {
                        blockEntity.addOverCharge(blockEntity.chargePerSculk)
                    }

                if(world.time % 100L == 0L) {
                    if(world.getBlockState(pos.up(1)).block == Blocks.LIGHTNING_ROD) {
                        var box = Box(pos).expand(blockEntity.effectRadius).stretch(0.0, world.height.toDouble(), 0.0)

                        if(blockEntity.charge > blockEntity.maxCharge)
                            box = Box(pos).expand(blockEntity.effectRadius * 2).stretch(0.0, world.height.toDouble(), 0.0)

                        val list = world.getNonSpectatingEntities(
                            PlayerEntity::class.java, box
                        )
                        blockEntity.playerAmount = list.size

                        if (list.isNotEmpty()) {
                            if(!blockEntity.isChargeZero()) {
                                blockEntity.playActivationSound()

                                for (playerEntity in list) {
                                    playerEntity.addStatusEffect(StatusEffectInstance(RegisterEffects.EVERLASTING, 101, 0, true, true))
                                    blockEntity.decreaseCharge()
                                    if(blockEntity.charge > blockEntity.maxCharge) blockEntity.decreaseCharge()
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
                markDirty(world, pos, state)
            }
        }
    }

    private fun isChargeZero(): Boolean {
        return this.charge <= 0
    }

    private fun consumeItem() {
        if(this.itemSlot[0].isOf(Items.SCULK)) this.itemSlot[0].decrement(chargePerPlayer)
    }

    private fun addOverCharge(charge: Int) {
        this.charge += charge
        if (this.charge >= this.maxCharge * 2) this.charge = this.maxCharge * 2
    }

    private fun addCharge(charge: Int) {
        this.charge += charge
        if (this.charge >= this.maxCharge) this.charge = this.maxCharge
    }

    private fun decreaseCharge() {
        this.charge -= this.chargePerPlayer
    }


    private fun playActivationSound() {
        if (this.soundPlayed) {
            world?.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS)
            this.soundPlayed = false
        }
    }

    private fun playDeactivationSound() {
        if (!this.soundPlayed) {
            world?.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS)
            this.soundPlayed = true
        }
    }
}