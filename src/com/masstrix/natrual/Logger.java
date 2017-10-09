package com.masstrix.natrual;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    private static final String NAME = "[NaturalEnvironment] ";

    public static void info(Object msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(NAME + String.valueOf(msg));
    }

    public static void info(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(NAME +
                ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void warn(Object msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(NAME + "\u00A7c" + String.valueOf(msg));
    }
}
