package me.semx11.autotip.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageMatcher {
    private final Matcher matcher;

    MessageMatcher(Pattern pattern, String input) {
        this.matcher = pattern.matcher(input);
    }

    public boolean matches() {
        return matcher.matches();
    }

    int getInt(String group) {
        try {
            return Integer.parseInt(this.getString(group));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Group " + group + " is not of type int.");
        }
    }

    String getString(String group) {
        return matcher.group(group);
    }
}
