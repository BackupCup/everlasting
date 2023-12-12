package net.backupcup.everlasting.assign

import net.backupcup.everlasting.obelisk.ObeliskScreenHandler
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType


object RegisterScreenHandlers {
    val OBELISK_SCREEN_HANDLER: ScreenHandlerType<ObeliskScreenHandler> =
        ScreenHandlerType ({ syncID, playerInventory ->
            ObeliskScreenHandler(syncID, playerInventory)
        }, FeatureFlags.VANILLA_FEATURES)
}