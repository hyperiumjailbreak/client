package com.hyperiumjailbreak;

import cc.hyperium.mods.chromahud.ElementRenderer;
import cc.hyperium.mods.chromahud.api.DisplayItem;
import cc.hyperium.mods.sk1ercommon.Sk1erMod;
import cc.hyperium.utils.JsonHolder;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChromaHudDarkAuctionDisplay extends DisplayItem {
    static final Timer timer = new Timer();
    private static final Sk1erMod util = new Sk1erMod();

    JsonParser parser = new JsonParser();
    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    String time = "Unknown";

    private void updateTime() {
        time = parser.parse(util.rawWithAgent("http://backend.rdil.rocks/timers/dark-auction")).getAsJsonObject().get("minutes_integer").getAsString();
    }

    public ChromaHudDarkAuctionDisplay(JsonHolder data, int ordinal) {
        super(data, ordinal);
        updateTime();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 60000);
    }

    @Override
    public void draw(int x, double y, boolean config) {
        List<String> list = new ArrayList<>();
        if (time != null) {
            list.add("Dark Auction: " + (time.equals("0") ? "less than a minute" : time + " minutes"));
        }
        height = fr.FONT_HEIGHT * list.size();
        int maxWidth = 0;
        for (String line : list) {
            if (fr.getStringWidth(line) > maxWidth) maxWidth = fr.getStringWidth(line);
        }
        width = maxWidth;
        ElementRenderer.draw(x, y, list);
    }
}
