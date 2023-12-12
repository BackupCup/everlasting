package net.backupcup.everlasting

import net.backupcup.everlasting.assign.RegisterBlocks
import net.backupcup.everlasting.assign.RegisterScreenHandlers
import net.backupcup.everlasting.assign.RegisterScreens
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
            RegisterScreenHandlers.OBELISK_SCREEN_HANDLER
            RegisterScreens.OBELISK_SCREEN
        }
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.EVERLASTING_OBELISK, RenderLayer.getCutout())
    }
}