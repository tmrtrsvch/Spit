package ch.tmrtrsv.spit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");

    public static String color(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            StringBuilder builder = new StringBuilder("&x");
            for (char c : hexCode.toCharArray())
                builder.append('&').append(c);
            text = text.replace("&#" + hexCode, builder.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}