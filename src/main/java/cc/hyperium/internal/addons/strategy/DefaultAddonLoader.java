package cc.hyperium.internal.addons.strategy;

import cc.hyperium.internal.addons.AddonBootstrap;
import cc.hyperium.internal.addons.AddonManifest;
import cc.hyperium.internal.addons.misc.AddonManifestParser;
import cc.hyperium.mods.sk1ercommon.Multithreading;
import net.minecraft.launchwrapper.Launch;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Loads addons in a production environment, and deletes addons that we know conflict with this version of the client.
 */
public final class DefaultAddonLoader extends AddonLoaderStrategy {
    @Override
    public AddonManifest load(File file) throws Exception {
        if (file == null) {
            throw new IOException("Could not load file; parameter issued was null.");
        } else {
            JarFile jar = new JarFile(file);
            if (jar.getJarEntry("pack.mcmeta") != null) {
                AddonBootstrap.INSTANCE.getAddonResourcePacks().add(file);
            }

            final AddonManifest manifest = (new AddonManifestParser(jar)).getAddonManifest();
            final List<String> array = Arrays.asList(
                    "AutoFriend", "Custom Crosshair Addon", "Tab Toggle", "SidebarAddon", "BossbarAddon", "FortniteCompassMod", "Item Physic"
            );

            if (!array.contains(manifest.getName()) && AddonBootstrap.INSTANCE.getPendingManifests().stream().noneMatch(manifest1 -> array.contains(manifest1.getName()))) {
                Launch.classLoader.addURL(file.toURI().toURL());
                return manifest;
            } else {
                Multithreading.runAsync(file::delete);
                return null;
            }
        }
    }
}
