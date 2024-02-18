package net.backupcup.everlasting

import net.backupcup.everlasting.assign.RegisterBlocks
import net.backupcup.everlasting.assign.RegisterScreenHandlers
import net.backupcup.everlasting.assign.RegisterScreens
import net.backupcup.everlasting.config.Config
import net.backupcup.everlasting.obelisk.ObeliskBlockEntity
import net.backupcup.everlasting.packets.ObeliskNetworkingConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.render.RenderLayer
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos

@Environment(EnvType.CLIENT)
object EverlastingClient : ClientModInitializer{
    override fun onInitializeClient() {
        RegisterScreenHandlers.OBELISK_SCREEN_HANDLER
        RegisterScreens.OBELISK_SCREEN
        BlockRenderLayerMap.INSTANCE.putBlock(RegisterBlocks.EVERLASTING_OBELISK, RenderLayer.getCutout())

        ClientPlayNetworking.registerGlobalReceiver(
            ObeliskNetworkingConstants.OBELISK_PACKET_ID,
            EverlastingClient::handleHighlightPacket
        )

        ClientPlayNetworking.registerGlobalReceiver(
            Everlasting.SYNC_CONFIG_PACKET
        ) { _: MinecraftClient?, _: ClientPlayNetworkHandler?, buf: PacketByteBuf?, _: PacketSender? ->
            if (buf != null) {
                Config.readFromServer(buf)?.let {
                    Everlasting.setConfig(
                        it
                    )
                }
            }
        }
    }

    private fun handleHighlightPacket(client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender) {
        val target = buf.readBlockPos()
        val charge = buf.readInt()
        val playerAmount = buf.readInt()

        client.execute {
            updateClientBlockEntity(target, charge, playerAmount)
        }
    }

    private fun updateClientBlockEntity(pos: BlockPos, charge: Int?, playerAmount: Int?) {
        val blockEntity = MinecraftClient.getInstance().world?.getBlockEntity(pos)

        if(blockEntity is ObeliskBlockEntity) {
            blockEntity.setCharge(charge)
            blockEntity.setPlayerAmount(playerAmount)
        }
    }
}