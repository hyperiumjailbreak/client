package com.hyperiumjailbreak;

import cc.hyperium.event.network.chat.ChatEvent;
import cc.hyperium.event.InvokeEvent;
import cc.hyperium.handlers.handlers.HypixelDetector;
import net.minecraft.client.Minecraft;

public class CommonChatResponder {
    private final String listenFor;
    private final String say;
    private final boolean onlyOnHypixel;

    public CommonChatResponder(String listenFor, String say, boolean onlyOnHypixel) {
        this.listenFor = listenFor;
        this.say = say;
        this.onlyOnHypixel = onlyOnHypixel;
    }

    @InvokeEvent
    public void onChat(ChatEvent e) {
        if (e.getChat().getUnformattedText().contains(this.listenFor) && !this.onlyOnHypixel || HypixelDetector.getInstance().isHypixel()) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/achat" + this.say);
        }
    }
}
