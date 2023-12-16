package net.backupcup.everlasting.assign

import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.obelisk.ObeliskScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier


object RegisterScreenHandlers {

    val OBELISK_SCREEN_HANDLER: ScreenHandlerType<ObeliskScreenHandler> = ScreenHandlerRegistry.registerSimple(
        Identifier(Everlasting.MOD_ID, "obelisk_screen_handler"), ::ObeliskScreenHandler)
}