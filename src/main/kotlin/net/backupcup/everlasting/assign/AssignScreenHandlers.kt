package net.backupcup.everlasting.assign

import net.backupcup.everlasting.obelisk.ObeliskScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType

object AssignScreenHandlers {
    val OBELISK_SCREEN_HANDLER: ScreenHandlerType<ObeliskScreenHandler> =
        ScreenHandlerType ( { syncID: Int, playerInventory: PlayerInventory ->
            ObeliskScreenHandler(
                syncID,
                playerInventory
            )
        }, FeatureFlags.VANILLA_FEATURES)
}
