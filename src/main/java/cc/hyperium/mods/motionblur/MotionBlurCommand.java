package cc.hyperium.mods.motionblur;

import cc.hyperium.Hyperium;
import cc.hyperium.commands.BaseCommand;
import cc.hyperium.config.Settings;
import cc.hyperium.mixinsimp.entity.HyperiumEntityRenderer;
import org.apache.commons.lang3.math.NumberUtils;

public class MotionBlurCommand implements BaseCommand {
    public void onExecute(String[] args) {
        if (args.length != 1) {
            Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage("Usage: /motionblur <0 - 7>.");
        } else {
            if (MotionBlurMod.isFastRenderEnabled()) {
                Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage("Motion blur breaks if Fast Render is on, please disable it in Options>Video Settings>Performance.");
                return;
            }

            int amount = NumberUtils.toInt(args[0], -1);

            if (amount < 0 || amount > 7) {
                Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage("Invalid motion blur amount.");
                return;
            }

            if (amount != 0) {
                Settings.MOTION_BLUR_ENABLED = true;
                Settings.MOTION_BLUR_AMOUNT = (float) amount;

                try {
                    MotionBlurMod.applyShader();
                } catch (Throwable var5) {
                    Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage("Failed to enable motion blur.");
                    var5.printStackTrace();
                }
            } else {
                Settings.MOTION_BLUR_ENABLED = false;
                HyperiumEntityRenderer.INSTANCE.disableBlurShader();
            }
        }
    }

    public String getName() {
        return "motionblur";
    }

    public String getUsage() {
        return "/motionblur <0 - 7>";
    }
}