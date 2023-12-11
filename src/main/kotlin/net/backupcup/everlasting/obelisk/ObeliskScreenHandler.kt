package net.backupcup.everlasting.obelisk

import net.backupcup.everlasting.assign.AssignBlocks
import net.backupcup.everlasting.assign.AssignScreenHandlers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.*
import net.minecraft.screen.slot.Slot

class ObeliskScreenHandler(
    syncId: Int,
    private val obeliskBlockEntity: ObeliskBlockEntity?,
    playerInventory: PlayerInventory,
    private val inventory: ObeliskBlockEntity.ObeliskInventory,
    private val context: ScreenHandlerContext
): ScreenHandler(AssignScreenHandlers.OBELISK_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory): this(
        syncId,
        null,
        playerInventory,
        ObeliskBlockEntity.ObeliskInventory(1, null),
        ScreenHandlerContext.EMPTY
    )

    fun getInventory() : Inventory {
        return inventory
    }

    init {
        inventory.onOpen(playerInventory.player)

        addSlot(object : Slot(getInventory(), 0, 80, 49) {})

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

        return newStack
    }


    override fun canUse(player: PlayerEntity?): Boolean {
        return canUse(this.context, player, AssignBlocks.EVERLASTING_OBELISK)
    }

    //fun isWorking() : Boolean {
    //    return getPropertyDelegate(0) > 0
    //}


    //fun getChargeProgress(): Int {
    //    val progress: Int = this.getPropertyDelegate(0)
    //    val maxProgress: Int = this.getPropertyDelegate(1)
    //    val progressBarSize = 12
//
    //    if (maxProgress != 0 && progress != 0) {
    //        return progress * progressBarSize / maxProgress
    //    }
    //    return 0
    //}
}