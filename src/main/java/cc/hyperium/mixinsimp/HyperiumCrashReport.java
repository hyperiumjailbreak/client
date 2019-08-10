package cc.hyperium.mixinsimp;

import cc.hyperium.Hyperium;
import cc.hyperium.commands.defaults.CommandDebug;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HyperiumCrashReport {
    private CrashReport parent;

    public HyperiumCrashReport(CrashReport crashReport) {
        this.parent = crashReport;
    }

    public void add(CallbackInfo info) {
        CrashReportCategory category = parent.makeCategoryDepth("Affected level", 1);
        category.addCrashSection("Hyperium Version", Hyperium.modid);
        category.addCrashSection("Everything else", CommandDebug.get());
    }
}
