package net.backupcup.everlasting.obelisk

import net.backupcup.everlasting.assign.RegisterScreenHandlers
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos


class ObeliskScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    inventory: Inventory,
    propertyDelegate: PropertyDelegate
): ScreenHandler(RegisterScreenHandlers.OBELISK_SCREEN_HANDLER, syncId) {
    private var inventory: Inventory
    private var propertyDelegate: PropertyDelegate

    constructor(syncId: Int, playerInventory: PlayerInventory) :
            this(syncId, playerInventory, SimpleInventory(1), ArrayPropertyDelegate(1))

    init {
        checkSize(inventory, 1)

        this.inventory = inventory
        this.propertyDelegate = propertyDelegate

        inventory.onOpen(playerInventory.player)

        this.addSlot(Slot(inventory, 0, 80, 49))

        for (m in 0 until 3) {
            for (l in 0 until 9) {
                this.addSlot(Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18))
            }
        }
        for (m in 0 until 9) {
            this.addSlot(Slot(playerInventory, m, 8 + m * 18, 142))
        }

        this.addProperties(propertyDelegate)
    }

    override fun quickMove(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var newStack: ItemStack = ItemStack.EMPTY
        val slot: Slot = this.slots[invSlot]

        if (slot.hasStack()) {
            val originalStack: ItemStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY
            }

            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }

        return newStack
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return inventory.canPlayerUse(player)
    }

    fun getCharge(): Int {
        return propertyDelegate.get(0)
    }
}