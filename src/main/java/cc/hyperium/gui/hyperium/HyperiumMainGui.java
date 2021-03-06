package cc.hyperium.gui.hyperium;

import cc.hyperium.Hyperium;
import cc.hyperium.gui.HyperiumGui;
import cc.hyperium.gui.hyperium.components.AbstractTab;
import cc.hyperium.gui.hyperium.components.SettingsTab;
import cc.hyperium.mixinsimp.client.GlStateModifier;
import cc.hyperium.mods.sk1ercommon.ResolutionUtil;
import cc.hyperium.utils.HyperiumFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The Hyperium settings GUI.
 */
public class HyperiumMainGui extends HyperiumGui {
    public static HyperiumMainGui INSTANCE = new HyperiumMainGui();
    private static int tabIndex = 0; // save tab position
    public boolean show = false;
    private int initialGuiScale;
    private final HashMap<Field, Supplier<String[]>> customStates = new HashMap<>();
    private final HashMap<Field, List<Consumer<Object>>> callbacks = new HashMap<>();
    private final List<Object> settingsObjects = Hyperium.CONFIG.getConfigObjects();
    private final HyperiumFontRenderer font;
    private final HyperiumFontRenderer title;
    private final List<AbstractTab> tabs;
    private AbstractTab currentTab;
    private final List<RGBFieldSet> rgbFields = new ArrayList<>();

    private HyperiumMainGui() {
        font = new HyperiumFontRenderer("OpenSans", 16.0F, 0, 1.0F);
        title = new HyperiumFontRenderer("OpenSans", 30.0F, 0, 1.0F);

        tabs = Collections.singletonList(new SettingsTab(this));
        scollMultiplier = 2;
        setTab(tabIndex);
    }

    public HashMap<Field, Supplier<String[]>> getCustomStates() {
        return customStates;
    }

    public HashMap<Field, List<Consumer<Object>>> getCallbacks() {
        return callbacks;
    }

    public List<RGBFieldSet> getRgbFields() {
        return rgbFields;
    }

    public List<Object> getSettingsObjects() {
        return settingsObjects;
    }

    @Override
    protected void pack() {
        show = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int yg = (height / 10); // Y grid
        int xg = (width / 11);  // X grid

        if (Minecraft.getMinecraft().theWorld == null) renderHyperiumBackground(ResolutionUtil.current());

        GlStateModifier.INSTANCE.reset();

        if (Minecraft.getMinecraft().theWorld == null) {
            this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
            this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        }
        drawRect(xg, yg, xg * 10, yg * 9, new Color(0, 0, 0, 225 / 2).getRGB());
        GlStateModifier.INSTANCE.reset();

        this.title.drawCenteredString("Settings", this.width / 2, yg + (yg / 2 - 8), 0xFFFFFF);

        currentTab.render(xg, yg * 2, xg * 9, yg * 7);

        GlStateManager.pushMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void show() {
        // Set user's GUI scale to normal whilst the GUI is open.
        initialGuiScale = Minecraft.getMinecraft().gameSettings.guiScale;
        Minecraft.getMinecraft().gameSettings.guiScale = 2;
        super.show();
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
        worldrenderer.pos(0.0D, sr.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(sr.getScaledWidth(), sr.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(sr.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public HyperiumFontRenderer getFont() {
        return font;
    }

    public void setTab(int i) {
        tabIndex = i;
        currentTab = tabs.get(i);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Hyperium.CONFIG.save();
        Minecraft.getMinecraft().gameSettings.guiScale = initialGuiScale;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        currentTab.handleMouseInput();
    }
}
