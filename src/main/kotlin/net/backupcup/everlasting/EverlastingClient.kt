package net.backupcup.everlasting

import net.backupcup.everlasting.assign.AssignBlocks
import net.backupcup.everlasting.assign.AssignPackets
import net.backupcup.everlasting.assign.AssignScreenHandlers
import net.backupcup.everlasting.assign.AssignScreens
import net.backupcup.everlasting.config.configHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

@Environment(EnvType.CLIENT)
object EverlastingClient : ClientModInitializer{
    override fun onInitializeClient() {
        if(configHandler.getConfigValue("ObeliskEnable").toBoolean()) {
            AssignScreens.OBELISK_SCREEN
            AssignScreenHandlers.OBELISK_SCREEN_HANDLER
            AssignPackets.assignPacket()
        }
        BlockRenderLayerMap.INSTANCE.putBlock(AssignBlocks.EVERLASTING_OBELISK, RenderLayer.getCutout())
    }
}