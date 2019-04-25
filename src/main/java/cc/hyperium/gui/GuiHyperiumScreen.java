package cc.hyperium.gui;

import cc.hyperium.styles.GuiStyle;
import cc.hyperium.Hyperium;
import cc.hyperium.config.Settings;
import cc.hyperium.gui.playerrenderer.GuiPlayerRenderer;
import cc.hyperium.utils.HyperiumFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jb.Metadata;

public class GuiHyperiumScreen extends GuiScreen {
    public static ResourceLocation background = new ResourceLocation("textures/material/backgrounds/1.png");
    public static boolean customBackground = false;
    public static File customImage = new File(Minecraft.getMinecraft().mcDataDir, "customImage.png");
    public static ResourceLocation bgDynamicTexture = null;
    public static BufferedImage bgBr = null;
    public static HyperiumFontRenderer fr = new HyperiumFontRenderer("Arial", Font.PLAIN, 20);
    public static HyperiumFontRenderer sfr = new HyperiumFontRenderer("Arial", Font.PLAIN, 12);
    public static DynamicTexture viewportTexture;
    private static float swing;
    public GuiButton hypixelButton;

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_FASTEST);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double) x, (double) (y + height), 0.0D).tex((double) (u * f), (double) ((v + (float) vHeight) * f1)).endVertex();
        worldrenderer.pos((double) (x + width), (double) (y + height), 0.0D).tex((double) ((u + (float) uWidth) * f), (double) ((v + (float) vHeight) * f1)).endVertex();
        worldrenderer.pos((double) (x + width), (double) y, 0.0D).tex((double) ((u + (float) uWidth) * f), (double) (v * f1)).endVertex();
        worldrenderer.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
    }

    public static ResourceLocation getBackground() {
        return background;
    }

    public static void setBackground(ResourceLocation givenBackground) {
        background = givenBackground;
    }

    public void initGui() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        switch (getStyle()) {
            case DEFAULT:
                drawDefaultStyleScreen(mouseX, mouseY);
                break;
            case HYPERIUM:
                drawHyperiumStyleScreen(mouseX, mouseY, partialTicks);
                break;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public GuiStyle getStyle() {
        return GuiStyle.valueOf(Minecraft.getMinecraft().theWorld == null ? Settings.MENU_STYLE : Settings.PAUSE_STYLE);
    }

    public void renderHyperiumBackground(ScaledResolution p_180476_1_) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        Minecraft.getMinecraft().getTextureManager().bindTexture(background);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, (double) p_180476_1_.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos((double) p_180476_1_.getScaledWidth(), (double) p_180476_1_.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos((double) p_180476_1_.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawHyperiumStyleScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        swing++;

        if (mc.theWorld == null) {
            if (!Settings.BACKGROUND.equals("DEFAULT")) {
                GlStateManager.disableAlpha();
                ScaledResolution sr = new ScaledResolution(mc);
                this.renderHyperiumBackground(sr);
                GlStateManager.enableAlpha();
            }
        }

        /* Render shadowed bar at top of screen */
        if (mc.theWorld == null) {
            this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
            this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        } else {
            this.drawDefaultBackground();
        }

        drawRect(0, 4, width, 55, 0x33000000);
        drawRect(0, 5, width, 54, 0x33000000);

        /* Render Client Logo */
        GlStateManager.color(1, 1, 1, 1);
        ResourceLocation logo = new ResourceLocation("textures/hyperium-logo.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(logo);
        drawScaledCustomSizeModalRect(10, 5, 0, 0, 2160, 500, 200, 47, 2160, 500);

        /* Render profile container */
        drawRect(width - 155, 10, width - 10, 49, 0x33000000);
        drawRect(width - 156, 9, width - 9, 50, 0x33000000);

        float val = (float) (Math.sin(swing / 40) * 30);
        ScissorState.scissor(width - 153, 0, 145, 49, true);

        if (!Hyperium.INSTANCE.isDevEnv()) GuiPlayerRenderer.renderPlayerWithRotation(width - 118, -4, val);
        ScissorState.endScissor();

        fr.drawStringScaled("HyperiumJailbreak", width - 152, 39, 0xFFFFFF, .75);

        /* Display copyright disclaimers at bottom of screen */
        sfr.drawString(I18n.format("menu.left").toUpperCase(), 1, height - 7, 0x55FFFFFF);
        String s = I18n.format("menu.right").toUpperCase();
        sfr.drawString(s, width - sfr.getWidth(s) - 1, height - 7, 0x55FFFFFF);
        GlStateManager.popMatrix();
    }

    public void drawDefaultStyleScreen(int mouseX, int mouseY) {
        if (!Settings.BACKGROUND.equals("DEFAULT")) {
            GlStateManager.disableAlpha();
            this.renderHyperiumBackground(new ScaledResolution(mc));
        }

        ParticleOverlay.getOverlay().render(mouseX, mouseY, 0, 0, 0, 0);
        GlStateManager.enableAlpha();
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(4F, 4F, 1F);
        this.drawCenteredString(fontRendererObj, Metadata.getModid(), width / 8, 40 / 4, 0xFFFFFF);
        GlStateManager.popMatrix();
        String s = String.format("%s %s", Metadata.getModid(), Metadata.getVersion());
        this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);
        String s1 = I18n.format("menu.right");
        this.drawString(this.fontRendererObj, s1, this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, -1);
        String s3 = "Made by Hyperium devs";
        this.drawString(this.fontRendererObj, s3, this.width - this.fontRendererObj.getStringWidth(s3) - 2, this.height - 30, -1);
        String s4 = "and jumbo <3";
        this.drawString(this.fontRendererObj, s4, this.width - this.fontRendererObj.getStringWidth(s4) - 2, this.height - 20, -1);
        GuiButton hypixelButton = this.hypixelButton;
        if (hypixelButton != null) hypixelButton.displayString = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? I18n.format("button.ingame.fixhypixelsession") : I18n.format("button.ingame.joinhypixel");
    }

    public int getIntendedWidth(int value) {
        float intendedWidth = 1920F;
        return (int) ((Minecraft.getMinecraft().displayWidth / intendedWidth) * value);
    }

    public int getIntendedHeight(int value) {
        float intendedHeight = 1080F;
        return (int) ((Minecraft.getMinecraft().displayHeight / intendedHeight) * value);
    }

    @Override
    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    @Override
    public void drawWorldBackground(int tint) {
        if (this.mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }
}
