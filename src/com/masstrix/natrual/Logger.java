package com.masstrix.natrual;

public class Logger {

    public static void info(Object msg) {
        NaturalEnvironment.getInstance().getLogger().info(String.valueOf(msg));
    }

    public static void warn(Object msg) {
        NaturalEnvironment.getInstance().getLogger().warning(String.valueOf(msg));
    }
}
