package it.wintry.customblocks.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class Utils {

    public String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
