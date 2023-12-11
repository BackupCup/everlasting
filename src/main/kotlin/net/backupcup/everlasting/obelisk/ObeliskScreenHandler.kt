package net.backupcup.everlasting.obelisk

import net.backupcup.everlasting.assign.AssignScreenHandlers
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class ObeliskScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory?,
    private var entity: ObeliskBlockEntity?,
    private var inventory: Inventory,
    private var propertyDelegate: PropertyDelegate = ArrayPropertyDelegate(2)
) : ScreenHandler(AssignScreenHandlers.OBELISK_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory): this(
        syncId,
        playerInventory,
        entity, ObeliskBlockEntity.ObeliskInventory(1, null)
        propertyDelegate
    )

    init {

        if (playerInventory != null) {
            inventory.onOpen(playerInventory.player)
        }

        addSlot(object : Slot(inventory, 0, 80, 49) {})

        for (row in 0 until 3) {
            for (col in 0 until 9) {
                addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }

        for (col in 0 until 9) {
            addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
    }

    override fun quickMove(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var newStack : ItemStack = ItemStack.EMPTY;
        val slot : Slot = this.slots[invSlot];

        if (slot.hasStack()) {
            val originalStack : ItemStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }


    override fun canUse(player: PlayerEntity?): Boolean {
        return inventory.canPlayerUse(player)
    }

    fun getPropertyDelegate(index: Int): Int {
        return propertyDelegate[index]
    }
}