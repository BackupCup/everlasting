package net.backupcup.everlasting.assign

import net.backupcup.everlasting.assign.AssignScreenHandlers
import net.backupcup.everlasting.obelisk.ObeliskScreen
import net.backupcup.everlasting.obelisk.ObeliskScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

object AssignScreens {
    val OBELISK_SCREEN = HandledScreens.register(AssignScreenHandlers.OBELISK_SCREEN_HANDLER) {
            handler: ObeliskScreenHandler, playerInventory: PlayerInventory, title: Text ->
        ObeliskScreen(
            handler,
            playerInventory,
            title
        )
    }
}