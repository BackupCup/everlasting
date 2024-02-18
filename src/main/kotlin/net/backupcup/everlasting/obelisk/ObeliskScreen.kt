package net.backupcup.everlasting.obelisk

import com.mojang.blaze3d.systems.RenderSystem
import net.backupcup.everlasting.Everlasting
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.ceil

@Environment(value = EnvType.CLIENT)
class ObeliskScreen(
    handler: ObeliskScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<ObeliskScreenHandler>(handler, playerInventory, title) {
    private var TEXTURE = Identifier(Everlasting.MOD_ID, "textures/gui/obelisk.png")
    val impactUI = Everlasting.getConfig()?.ImpactfulUI()

    val maxCharge = Everlasting.getConfig()?.ObeliskChargeMax()?.toDouble()
    val sculkPerPlayer = Everlasting.getConfig()?.ObeliskChargeUsedPerPlayer()
    val chargePerSculk = Everlasting.getConfig()?.ObeliskChargePerSculk()
    val effectRange = (Everlasting.getConfig()?.ObeliskRadius())?.toDouble()

    override fun init() {
        super.init()
        titleY = 1000
        playerInventoryTitleY = 1000
    }

    override fun drawBackground(context: DrawContext?, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        if((impactUI?: true) && handler.getCharge() > (maxCharge?: 120.0)) {
            TEXTURE = Identifier(Everlasting.MOD_ID, "textures/gui/obelisk_overcharged.png")
        }
        RenderSystem.setShaderTexture(0, TEXTURE)

        x = (width - backgroundWidth) / 2
        y = (height - backgroundHeight) / 2

        context?.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight)
        renderCharge(context, x, y)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        renderTooltip(context, mouseX, mouseY)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    private fun renderCharge(context: DrawContext?, x: Int, y: Int){
        if (handler.getCharge() > 0)
            context?.drawTexture(TEXTURE, x + 52, y + 10, 1, 166, (ceil(((handler.getCharge() / (maxCharge?: 120.0)) * 12)).toInt() * 6), 18)
        if (handler.getCharge() > (maxCharge?: 120.0)) {
            if(impactUI != false) {
                context?.drawTexture(
                    TEXTURE,
                    x + 123,
                    y + 5,
                    1,
                    183,
                    (ceil((((handler.getCharge() - (maxCharge?: 120.0)) / (maxCharge?: 120.0)) * 12)).toInt() * 6),
                    25
                )
            } else {
                context?.drawTexture(
                    TEXTURE,
                    x + 51,
                    y + 5,
                    1,
                    183,
                    (ceil((((handler.getCharge() - (maxCharge?: 120.0)) / (maxCharge?: 120.0)) * 12)).toInt() * 6),
                    25
                )
            }
        }

    }

    private fun renderTooltip(context: DrawContext?, mouseX: Int, mouseY: Int) {
        x = (width - backgroundWidth) / 2
        y = (height - backgroundHeight) / 2

        var tooltipTextList: List<Text> = listOf(
            Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.charge").append(
            Text.literal(": ${handler.getCharge()} / ${(maxCharge?: 120.0).toInt()}")).formatted(Formatting.DARK_AQUA),
            Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.sculkPerPlayer").append(Text.literal(": $sculkPerPlayer")).formatted(Formatting.DARK_GRAY),
            Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.chargePerSculk").append(Text.literal(": $chargePerSculk")).formatted(Formatting.DARK_GRAY),
            Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.effectRange").append(Text.literal(": $effectRange")).formatted(Formatting.DARK_GRAY)
        )

        if(handler.getCharge() > (maxCharge?: 120.0)) {
            tooltipTextList = listOf(
                Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.charge").append(
                Text.literal(": ${handler.getCharge()} / ${(maxCharge?: 120.0).toInt() * 2}")).formatted(Formatting.YELLOW),
                Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.overcharged").formatted(Formatting.YELLOW).formatted(Formatting.BOLD),
                Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.sculkPerPlayer").append(Text.literal(": ${(sculkPerPlayer?: 1) * 2}")).formatted(Formatting.DARK_GRAY).formatted(Formatting.BOLD),
                Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.chargePerSculk").append(Text.literal(": $chargePerSculk")).formatted(Formatting.DARK_GRAY),
                Text.translatable("tooltip.everlasting.everlasting_obelisk.ui.effectRange").append(Text.literal(": ${(effectRange?: 16.0) * 2}")).formatted(Formatting.DARK_GRAY).formatted(Formatting.BOLD)
            )
        }

        if(impactUI == false) {
            if (mouseX in (x + 51..x + 124) && mouseY in (y + 10..y + 27))
                context?.drawTooltip(
                    this.textRenderer,
                    tooltipTextList,
                    mouseX, mouseY
                )
        } else {
            if (mouseX in (x + 51..x + 124 + (ceil((((handler.getCharge() - (maxCharge?: 120.0)) / (maxCharge?: 120.0)) * 12)).toInt() * 6)) && mouseY in (y + 10..y + 27))
                context?.drawTooltip(
                    this.textRenderer,
                    tooltipTextList,
                    mouseX, mouseY
                )
        }
    }
}