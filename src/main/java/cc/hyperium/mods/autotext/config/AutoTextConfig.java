package cc.hyperium.mods.autotext.config;

import cc.hyperium.Hyperium;
import cc.hyperium.config.ConfigOpt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoTextConfig {
    public static final AutoTextConfig INSTANCE = new AutoTextConfig();

    @ConfigOpt
    private final Map<String, String> keybinds = new ConcurrentHashMap<>();

    public Map<String, String> getKeybinds() {
        return keybinds;
    }

    public void addKeybind(String keyCode, String description) {
        keybinds.put(keyCode, description);
        Hyperium.CONFIG.save();
    }
}
