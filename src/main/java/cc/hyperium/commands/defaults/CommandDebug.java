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

package cc.hyperium.commands.defaults;
import cc.hyperium.Hyperium;
import cc.hyperium.commands.BaseCommand;
import cc.hyperium.handlers.handlers.HypixelDetector;
import cc.hyperium.handlers.handlers.chat.GeneralChatHandler;
import cc.hyperium.mods.chromahud.ChromaHUDApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommandDebug implements BaseCommand {
    private static final Gson printer = new GsonBuilder().setPrettyPrinting().create();

    private static void tryChromaHUD(StringBuilder builder) {
        try {
            builder.append("ChromaHUD: ").append(printer.toJson(ChromaHUDApi.getInstance().getConfig().getObject()));
        } catch (Exception e) {
            builder.append("ChromaHUD: Error");
        }
    }

    private static void tryConfig(StringBuilder builder) {
        try {
            Hyperium.CONFIG.save();
            builder.append("Config: ").append(printer.toJson(Hyperium.CONFIG.getConfig()));
        } catch (Exception e) {
            builder.append("Config: Error");
        }
    }

    private static void tryKeybinds(StringBuilder builder) {
        try {
            builder.append("Keybinds: ").append(printer.toJson(Hyperium.INSTANCE.getHandlers().getKeybindHandler().getKeyBindConfig().getKeyBindJson().getData()));
        } catch (Exception e) {
            builder.append("Keybinds: Error");
        }
    }

    private static void tryLevelhead(StringBuilder builder) {
        try {
            builder.append("Local Stats: ").append(HypixelDetector.getInstance().isHypixel()).append("\n");
            builder.append("Header State: ").append(Hyperium.INSTANCE.getModIntegration().getLevelhead().getHeaderConfig()).append("\n");
            builder.append("Footer State: ").append(Hyperium.INSTANCE.getModIntegration().getLevelhead().getFooterConfig()).append("\n");
        } catch (Exception e) {
            builder.append("Levelhead: Error");
        }
    }

    public static String get() {
        StringBuilder builder = new StringBuilder();

        HypixelDetector instance = HypixelDetector.getInstance();
        if (instance != null) builder.append("Hypixel: ").append(instance.isHypixel());
        builder.append("\n\n");
        tryConfig(builder);
        builder.append("\n");
        tryChromaHUD(builder);
        builder.append("\n");
        tryKeybinds(builder);
        builder.append("\n");
        tryLevelhead(builder);
        builder.append("\n");

        return builder.toString();
    }

    @Override
    public String getName() {
        return "hyperium_debug";
    }

    @Override
    public String getUsage() {
        return "Usage: /hyperium_debug";
    }

    @Override
    public void onExecute(String[] args) {
        GeneralChatHandler.instance().sendMessage("Please instead use /logs");
    }
}
