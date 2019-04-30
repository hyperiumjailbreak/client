/*
 *     Copyright (C) 2018  Hyperium <https://hyperium.cc/>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cc.hyperium.gui;
import cc.hyperium.C;
import cc.hyperium.Hyperium;
import cc.hyperium.event.EventBus;
import cc.hyperium.mods.sk1ercommon.ResolutionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class HyperiumGui extends GuiScreen {
    public static ResourceLocation background = new ResourceLocation("textures/material/backgrounds/1.png");
    public static ResourceLocation bgDynamicTexture = null;
    private static BufferedImage bgBr = null;
    private final Map<GuiButton, Consumer<GuiButton>> clicks = new HashMap<>();
    private final Map<GuiButton, Consumer<GuiButton>> updates = new HashMap<>();
    private final Map<String, GuiButton> nameMap = new HashMap<>();
    public int offset = 0;
    protected HashMap<GuiBlock, Runnable> actions = new HashMap<>();
    protected double scollMultiplier = 1;
    protected int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
    private int idIteration;
    private boolean drawAlpha = true;
    private int alpha = 100;
    private ScaledResolution lastResolution;

    public HyperiumGui() {
        lastResolution = ResolutionUtil.current();
    }

    public static float clamp(float number, float min, float max) {
        return number < min ? min : number > max ? max : number;
    }

    public static float easeOut(float current, float goal, float jump, float speed) {
        if (Math.floor(Math.abs(goal - current) / jump) > 0) {
            return current + (goal - current) / speed;
        } else {
            return goal;
        }
    }

    public static String trimString(String str, int width, FontRenderer font, boolean appendEllipsis) {
        if (width <= 0) {
            throw new IllegalArgumentException("String width cannot be less than or equal to 0.");
        } else if (str == null || str.length() <= 0) {
            throw new IllegalArgumentException("String cannot be null and cannot have a length less than or equal to 0.");
        } else {
            if (font == null) {
                if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().fontRendererObj != null) {
                    font = Minecraft.getMinecraft().fontRendererObj;
                } else {
                    throw new IllegalStateException("Param \"font\" is null and default font renderer could not be used!");
                }
            }

            final StringBuilder strBuilder = new StringBuilder();
            final String suffix = (appendEllipsis ? "..." : "");
            int currentWidth = font.getStringWidth(suffix);
            // If suffix is already too long
            if (width < currentWidth) return suffix;

            // Loop through each character until too long
            for (char theChar : str.toCharArray()) {
                final int charWidth = font.getCharWidth(theChar);
                // If strWidth + next charWidth is too big then return this string, otherwise continue
                if (width < currentWidth + charWidth) {
                    return strBuilder.append(suffix).toString();
                } else {
                    strBuilder.append(theChar);
                    currentWidth += charWidth;
                }
            }
            return strBuilder.toString();
        }
    }

    public static void drawChromaBox(int left, int top, int right, int bottom, float alpha) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        int startColor = Color.HSBtoRGB(System.currentTimeMillis() % 5000L / 5000.0f, 0.8f, 0.8f);
        int endColor = Color.HSBtoRGB((System.currentTimeMillis() + 500) % 5000L / 5000.0f, 0.8f, 0.8f);
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) right, (double) top, (double) 0).color(f1, f2, f3, alpha).endVertex();
        worldrenderer.pos((double) left, (double) top, (double) 0).color(f1, f2, f3, alpha).endVertex();
        worldrenderer.pos((double) left, (double) bottom, (double) 0).color(f5, f6, f7, alpha).endVertex();
        worldrenderer.pos((double) right, (double) bottom, (double) 0).color(f5, f6, f7, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        EventBus.INSTANCE.unregister(this);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        Consumer<GuiButton> guiButtonConsumer = clicks.get(button);
        if (guiButtonConsumer != null) guiButtonConsumer.accept(button);
    }

    @Override
    public void updateScreen() {
        ScaledResolution current = ResolutionUtil.current();
        if (current == null) return;
        if (lastResolution.getScaledWidth() != current.getScaledWidth() || lastResolution.getScaledHeight() != current.getScaledHeight() || lastResolution.getScaleFactor() != current.getScaleFactor()) {
            rePack();
        }

        this.lastResolution = current;
        super.updateScreen();
        for (GuiButton guiButton : buttonList) {
            Consumer<GuiButton> guiButtonConsumer = updates.get(guiButton);
            if (guiButtonConsumer != null) guiButtonConsumer.accept(guiButton);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        rePack();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mouseX = (int) (mouseX * ((float) Minecraft.getMinecraft().gameSettings.guiScale) / (float) guiScale);
        mouseY = (int) (mouseY * ((float) Minecraft.getMinecraft().gameSettings.guiScale) / (float) guiScale);

        for (GuiBlock block : actions.keySet()) {
            if (block.isMouseOver(mouseX, mouseY)) {
                actions.get(block).run();
                return;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        actions.clear();

        if (drawAlpha) Gui.drawRect(0, 0, ResolutionUtil.current().getScaledWidth() * ResolutionUtil.current().getScaleFactor(), ResolutionUtil.current().getScaledHeight() * ResolutionUtil.current().getScaleFactor(), new Color(0, 0, 0, alpha).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if (i < 0) {
            offset += 11 * scollMultiplier;
        } else if (i > 0) {
            offset -= 11 * scollMultiplier;
        }
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int nextId() {
        return (++idIteration);
    }

    public void rePack() {
        buttonList.clear();
        pack();
    }

    public void show() {
        Hyperium.INSTANCE.getHandlers().getGuiDisplayHandler().setDisplayNextTick(this);
    }

    protected void reg(String name, GuiButton button, Consumer<GuiButton> consumer, Consumer<GuiButton> tick) {
        this.buttonList.add(button);
        this.clicks.put(button, consumer);
        this.updates.put(button, tick);
        this.nameMap.put(name, button);
    }

    protected abstract void pack();

        protected void drawBackground() {
        if (this.mc != null && this.mc.theWorld == null) renderHyperiumBackground(ResolutionUtil.current());

        if (drawAlpha) Gui.drawRect(0, 0, ResolutionUtil.current().getScaledWidth() * ResolutionUtil.current().getScaleFactor(), ResolutionUtil.current().getScaledHeight() * ResolutionUtil.current().getScaleFactor(), new Color(0, 0, 0, alpha).getRGB());
    }

    private void renderHyperiumBackground(ScaledResolution sr) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();

        Minecraft.getMinecraft().getTextureManager().bindTexture(background);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, (double) sr.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos((double) sr.getScaledWidth(), (double) sr.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos((double) sr.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    protected GuiButton getButtonByName(String name) {
        return nameMap.get(name);
    }

    public void setDrawAlpha(boolean drawAlpha) {
        this.drawAlpha = drawAlpha;
    }

    public void drawScaledText(String text, int trueX, int trueY, double scaleFac, int color, boolean shadow, boolean centered) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleFac, scaleFac, scaleFac);
        fontRendererObj.drawString(text, (float) (((double) trueX) / scaleFac) - (centered ? fontRendererObj.getStringWidth(text) / 2f : 0), (float) (((double) trueY) / scaleFac), color, shadow);
        GlStateManager.scale(1 / scaleFac, 1 / scaleFac, 1 / scaleFac);
        GlStateManager.popMatrix();
    }

    public String boldText(String first, String second) {
        return C.BOLD + first + C.GRAY + second;
    }
}
