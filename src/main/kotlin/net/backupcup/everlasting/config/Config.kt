package net.backupcup.everlasting.config

import blue.endless.jankson.Comment
import blue.endless.jankson.Jankson
import blue.endless.jankson.api.DeserializationException
import blue.endless.jankson.api.SyntaxError
import net.backupcup.everlasting.Everlasting
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path


class Config {
    companion object {
        var lastError: Text? = null

        private fun getConfigFile(): File? {
            return Path.of(
                FabricLoader.getInstance().getConfigDir().toString(),
                Everlasting.MOD_ID,
                "config.json"
            ).toFile()
        }

        private val JANKSON: Jankson = Jankson.builder()
            .registerSerializer(Identifier::class.java) { id, marshaller -> marshaller.serialize(id.toString()) }
            .registerDeserializer(String::class.java, Identifier::class.java) { str, marshaller ->
                Identifier.tryParse(
                    str
                )
            }
            .build()

        fun load(): Config? {
            val defaults = Config()
            try {
                if (getConfigFile()!!.exists()) {
                    lastError = null
                    val json = JANKSON.load(getConfigFile())
                    return JANKSON.fromJsonCarefully(json, Config::class.java)
                }
                defaults.save()
                lastError = null
                return defaults
            } catch (e: SyntaxError) {
                Everlasting.LOGGER?.error("Config syntax error. {}.", e.lineMessage)
                Everlasting.LOGGER?.error(e.message)
                Everlasting.LOGGER?.warn("Using default configuration.")
                lastError = Text.translatable("message.overvoltage.error.config.general")
            } catch (e: DeserializationException) {
                Everlasting.LOGGER?.error("Overvoltage's config deserialization error.")
                Everlasting.LOGGER?.error("{}", e.message)
                if (e.cause != null) {
                    Everlasting.LOGGER?.error("Cause: {}", e.cause!!.message)
                }
                Everlasting.LOGGER?.warn("Using default configuration.")
                lastError = Text.translatable("message.overvoltage.error.config.general")
            } catch (e: IOException) {
                Everlasting.LOGGER?.error("IO exception occurred while reading config. Using defaults.")
                Everlasting.LOGGER?.error(e.message)
                Everlasting.LOGGER?.warn("Using default configuration.")
                lastError = Text.translatable("message.overvoltage.error.config.general")
            }
            return defaults
        }

        fun readFromServer(buf: PacketByteBuf): Config? {
            try {
                return JANKSON.fromJsonCarefully(buf.readString(), Config::class.java)
            } catch (e: SyntaxError) {
                Everlasting.LOGGER?.error("Error while retrieving config from server: {}", e)
            }
            return null
        }

        const val skip: String = "\n\n"
    }

    @Throws(FileNotFoundException::class)
    fun save() {
        getConfigFile()!!.parentFile.mkdirs()
        try {
            FileOutputStream(getConfigFile()).use { outStream ->
                outStream.write(
                    JANKSON.toJson(this).toJson(true, true).toByteArray()
                )
            }
        } catch (e: IOException) {
            Everlasting.LOGGER?.error("IO exception while saving config: {}", e.message)
        }
    }



    fun writeToClient(buf: PacketByteBuf) {
        buf.writeString(JANKSON.toJson(this).toJson())
    }

    enum class ListType {
        ALLOW,
        DENY
    }

    @Comment(
        "${Companion.skip}Duration of a normal Everlasting potion (in seconds)"
    ) private var PotionDuration: Int = 90

    @Comment(
        "${Companion.skip}Duration of an extended Everlasting potion (in seconds)"
    ) private var PotionBigDuration: Int = 180

    @Comment(
        "${Companion.skip}Starting durability of the Capsule"
    ) private var CapsuleDurability: Int = 250

    @Comment(
        "${Companion.skip}Radius in which the Obelisk will give the Everlasting Effect\n" +
        "The Obelisk itself doesn't count towards this setting"
    ) private var ObeliskRadius: Int = 16

    @Comment(
        "${Companion.skip}How much charge does the Obelisk get per 1 Sculk"
    ) private var ObeliskChargePerSculk: Int = 10

    @Comment(
        "${Companion.skip}How much charge is used per player"
    ) private var ObeliskChargeUsedPerPlayer: Int = 1

    @Comment(
        "${Companion.skip}The maximum amount of charge that an Obelisk can have\n" +
        "Overcharge is x*2 where x - this value"
    ) private var ObeliskChargeMax: Int = 120

    @Comment(
        "${Companion.skip}Modifies the Item Entity behaviour to not despawn items\n" +
        "which's rarity isn't that of ItemSaveRarity."
    ) private var EnableSaveRarity: Boolean = false

    @Comment(
        "${Companion.skip}Which rarity of items doesn't despawn\n" +
        "This setting will make items that aren't of this rarity be despawnable.\n" +
        "Note: The undespawnable items can still be destroyed by lava, cactus, anvils etc.\n" +
        "Rarities: COMMON, UNCOMMON, RARE, EPIC"
    ) private var ItemSaveRarity: Rarity = Rarity.COMMON

    @Comment(
        "${Companion.skip}Whether the UI of the Obelisk should be \"Impactful\".\n" +
        "If enabled, the Ui will \"Explode\" whenever the Obelisk is Overcharged"
    ) private var ImpactfulUI: Boolean = true

    @Comment(
        "${Companion.skip}An item blacklist. Every item in this List will be ignored by:\n" +
        "the Capsule, the Obelisk, the Enchantment and the Potion effect."
    ) private var ItemBlacklist: Array<String> = arrayOf(
        "aquamirae:sweet_lance",
        "meadow:watering_can",
        "amethyst_imbuement:furious_scepter",
        "amethyst_imbuement:witty_scepter",
        "amethyst_imbuement:graceful_scepter",
        "amethyst_imbuement:blazing_scepter",
        "amethyst_imbuement:sparking_scepter",
        "amethyst_imbuement:frosted_scepter",
        "amethyst_imbuement:scepter_of_blades",
        "amethyst_imbuement:scepter_of_recall",
        "amethyst_imbuement:clerics_scepter",
        "amethyst_imbuement:bardic_scepter",
        "amethyst_imbuement:scepter_of_summoning",
        "amethyst_imbuement:builders_scepter",
        "amethyst_imbuement:scepter_of_the_vanguard",
        "amethyst_imbuement:scepter_of_the_paladin",
        "amethyst_imbuement:scepter_of_the_pacifist",
        "amethyst_imbuement:scepter_of_harvests",
        "amethyst_imbuement:corrupted_scepter",
        "amethyst_imbuement:scepter_of_agonies",
        "amethyst_imbuement:dangerous_scepter",
        "amethyst_imbuement:skillful_scepter",
        "amethyst_imbuement:enduring_scepter",
        "amethyst_imbuement:scepter_of_insight",
        "amethyst_imbuement:persuasive_scepter",
        "amethyst_imbuement:travelers_scepter",
        "amethyst_imbuement:lethality",
        "amethyst_imbuement:resonance",
        "amethyst_imbuement:redemption",
        "amethyst_imbuement:equinox",
        "amethyst_imbuement:sojourn",
        "amethyst_imbuement:aegis",
        "amethyst_imbuement:judgment",
        "amethyst_imbuement:a_scepter_so_fowl"

    )

    fun PotionDuration(): Int {
        return PotionDuration
    }

    fun PotionBigDuration(): Int {
        return PotionBigDuration
    }

    fun CapsuleDurability(): Int {
        return CapsuleDurability
    }

    fun ObeliskRadius(): Int {
        return ObeliskRadius
    }

    fun ObeliskChargePerSculk(): Int {
        return ObeliskChargePerSculk
    }

    fun ObeliskChargeUsedPerPlayer(): Int {
        return ObeliskChargeUsedPerPlayer
    }

    fun ObeliskChargeMax(): Int {
        return ObeliskChargeMax
    }

    fun EnableSaveRarity(): Boolean {
        return EnableSaveRarity
    }

    fun ItemSaveRarity(): Rarity {
        return ItemSaveRarity
    }

    fun ImpactfulUI(): Boolean {
        return ImpactfulUI
    }

    fun inBlacklist(string: String): Boolean {
        return ItemBlacklist.contains(string)
    }
}