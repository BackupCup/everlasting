package net.backupcup.everlasting.config

import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.Properties

object configHandler {

    private val configFile = File(FabricLoader.getInstance().configDir.toString(), "everlasting.properties")
    private val defaultConfig: Properties = getDefaultConfig()

    init {
        initConfig()
    }

    private fun getDefaultConfig(): Properties {
        val defaultConfig = Properties()

        // Add default config values here
        defaultConfig.setProperty("PotionEverlastingDuration", "90")
        defaultConfig.setProperty("PotionEverlastingLargeDuration", "180")

        defaultConfig.setProperty("ObeliskRadius", "16")
        defaultConfig.setProperty("ObeliskChargePerSculk", "10")
        defaultConfig.setProperty("ObeliskChargedUsedPerPlayer", "1")
        defaultConfig.setProperty("ObeliskChargeMax", "120")

        defaultConfig.setProperty("ListItemIDs", "item.aquamirae.sweet_lance, item.meadow.watering_can, item.amethyst_imbuement.furious_scepter, item.amethyst_imbuement.witty_scepter, item.amethyst_imbuement.graceful_scepter, item.amethyst_imbuement.blazing_scepter, item.amethyst_imbuement.sparking_scepter, item.amethyst_imbuement.frosted_scepter, item.amethyst_imbuement.scepter_of_blades, item.amethyst_imbuement.scepter_of_recall, item.amethyst_imbuement.clerics_scepter, item.amethyst_imbuement.bardic_scepter, item.amethyst_imbuement.scepter_of_summoning, item.amethyst_imbuement.builders_scepter, item.amethyst_imbuement.scepter_of_the_vanguard, item.amethyst_imbuement.scepter_of_the_paladin, item.amethyst_imbuement.scepter_of_the_pacifist, item.amethyst_imbuement.scepter_of_harvests, item.amethyst_imbuement.corrupted_scepter, item.amethyst_imbuement.scepter_of_agonies, item.amethyst_imbuement.dangerous_scepter, item.amethyst_imbuement.skillful_scepter, item.amethyst_imbuement.enduring_scepter, item.amethyst_imbuement.scepter_of_insight, item.amethyst_imbuement.persuasive_scepter, item.amethyst_imbuement.travelers_scepter, item.amethyst_imbuement.lethality, item.amethyst_imbuement.resonance, item.amethyst_imbuement.redemption, item.amethyst_imbuement.equinox, item.amethyst_imbuement.sojourn, item.amethyst_imbuement.aegis, item.amethyst_imbuement.judgment, item.amethyst_imbuement.a_scepter_so_fowl")
        return defaultConfig
    }

    private fun initConfig() {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            try {
                configFile.createNewFile()
                saveConfig()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            loadConfig()
            updateConfig()
            saveConfig()
        }
    }

    private fun loadConfig() {
        try {
            FileReader(configFile).use { reader ->
                defaultConfig.load(reader)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updateConfig() {
        val defaultKeys = defaultConfig.keys()
        while (defaultKeys.hasMoreElements()) {
            val key = defaultKeys.nextElement() as String
            if (!defaultConfig.containsKey(key)) {
                defaultConfig.setProperty(key, "defaultValue")
            }
        }
    }

    private fun saveConfig() {
        try {
            FileWriter(configFile).use { writer ->
                defaultConfig.store(writer, "Everlasting Configuration || THIS IS SERVER CONFIG, THIS *HAS* TO MATCH WITH THE SERVER CONFIG")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getConfigValue(propertyName: String): String {
        return defaultConfig.getProperty(propertyName, "defaultValue")
    }

    fun containsItemID(itemID: String): Boolean {
        val itemIDsString = getConfigValue("ListItemIDs")
        val itemIDs = itemIDsString.split(',').map { it.trim() }
        return itemIDs.contains(itemID)
    }
}
