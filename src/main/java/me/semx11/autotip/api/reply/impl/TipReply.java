package me.semx11.autotip.api.reply.impl;

import java.util.Collections;
import java.util.List;
import me.semx11.autotip.api.reply.Reply;

public class TipReply extends Reply {
    private List<Tip> tips;

    private TipReply(List<Tip> tips) {
        this.tips = tips;
    }

    public static TipReply getDefault() {
        return new TipReply(Collections.singletonList(new Tip("all", null)));
    }

    public List<Tip> getTips() {
        return tips;
    }

    public static class Tip {
        private String gamemode;
        private String username;


        private Tip(String gamemode, String username) {
            this.gamemode = gamemode;
            this.username = username;
        }

        public String getAsCommand() {
            return "/tip " + this.toString();
        }

        @Override
        public String toString() {
            return (username != null && !username.isEmpty() ? username + " " : "") + gamemode;
        }
    }
}
