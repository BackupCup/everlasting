package net.backupcup.everlasting.assign

import net.backupcup.everlasting.obelisk.ObeliskScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens

object RegisterScreens {
    val OBELISK_SCREEN = HandledScreens.register(RegisterScreenHandlers.OBELISK_SCREEN_HANDLER, ::ObeliskScreen)
}