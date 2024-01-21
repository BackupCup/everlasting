package net.backupcup.everlasting.packets

import net.backupcup.everlasting.Everlasting
import net.minecraft.util.Identifier

class ObeliskNetworkingConstants {
    companion object {
        val OBELISK_PACKET_ID: Identifier = Identifier(Everlasting.MOD_ID, "obelisk_data")
    }
}