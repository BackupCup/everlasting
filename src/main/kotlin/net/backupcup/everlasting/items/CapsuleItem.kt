package net.backupcup.everlasting.items

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

class CapsuleItem(settings: Settings?) : Item(settings) {
    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        tooltip?.add(Text.translatable("tooltip.everlasting.everlasting_capsule.line_1").formatted(Formatting.GRAY))
    }

    override fun isEnchantable(stack: ItemStack?): Boolean {
        return false
    }

    override fun canRepair(stack: ItemStack?, ingredient: ItemStack?): Boolean {
        if (ingredient != null) {
            return ingredient.isOf(Items.NETHERITE_SCRAP)
        }
        return false
    }
}