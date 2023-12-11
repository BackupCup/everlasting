package net.backupcup.everlasting.networking

import net.backupcup.everlasting.obelisk.ObeliskBlockEntity
import net.backupcup.everlasting.obelisk.ObeliskScreenHandler
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

object ChargeSyncS2CPacket {
    fun receiveSyncInfo(
        minecraftClient: MinecraftClient, handler: ClientPlayNetworkHandler?,
        buf: PacketByteBuf, responseSender: PacketSender?
    ) {
        val chargeLevel = buf.readInt()
        val position = buf.readBlockPos()
        val obeliskBlockEntity : ObeliskBlockEntity = minecraftClient.world!!.getBlockEntity(position) as ObeliskBlockEntity
        val screenHandler : ObeliskScreenHandler = minecraftClient.player!!.currentScreenHandler as ObeliskScreenHandler
        if (minecraftClient.world!!.getBlockEntity(position) is ObeliskBlockEntity) {
            obeliskBlockEntity.setChargeLevel(chargeLevel)
            if (minecraftClient.player!!.currentScreenHandler is ObeliskScreenHandler && obeliskBlockEntity.getPos() == position) {
                obeliskBlockEntity.setChargeLevel(chargeLevel)
                screenHandler.getChargeProgress()
            }
        }
    }
}
