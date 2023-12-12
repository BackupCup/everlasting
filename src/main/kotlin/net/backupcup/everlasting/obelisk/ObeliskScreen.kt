package net.backupcup.everlasting.obelisk

import com.mojang.blaze3d.systems.RenderSystem
import net.backupcup.everlasting.Everlasting
import net.backupcup.everlasting.config.configHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.ceil

@Environment(value = EnvType.CLIENT)
class ObeliskScreen(
    handler: ObeliskScreenHandler,
    playerInventory: PlayerInventory,
    title: Text
) : HandledScreen<ObeliskScreenHandler>(handler, playerInventory, title) {
        val TEXTURE = Identifier(Everlasting.MOD_ID, "textures/gui/obelisk.png")
        val chargePerSculk = configHandler.getConfigValue("ObeliskChargePerSculk").toFloat()

    override fun init() {
        super.init()
        titleY = 1000
        playerInventoryTitleY = 1000
    }

    override fun drawBackground(context: DrawContext?, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        x = (width - backgroundWidth) / 2
        y = (height - backgroundHeight) / 2

        context?.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight)
        renderCharge(context, x, y)
        //Everlasting.logger.info("handler: " + handler.getPropertyDelegate(0).toString()) //debug
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    private fun renderCharge(context: DrawContext?, x: Int, y: Int){
        if (handler.getCharge() > 0)
            context?.drawTexture(TEXTURE, x + 52, y + 10, 1, 166, (ceil((handler.getCharge() / chargePerSculk)).toInt() * 6), 18) //replace x in (x / chargePerSculk) with
    }
}