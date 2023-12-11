package net.backupcup.everlasting.assign

import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.networking.ChargeSyncS2CPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier

object AssignPackets {
    var CHARGE_SYNC = Identifier(Everlasting.MOD_ID, "charge_sync")
    fun assignPacket() = ClientPlayNetworking.registerGlobalReceiver(
        CHARGE_SYNC, ChargeSyncS2CPacket::receiveSyncInfo)
}