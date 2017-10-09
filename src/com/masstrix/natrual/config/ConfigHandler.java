package com.masstrix.natrual.config;

import com.masstrix.natrual.Logger;
import com.masstrix.natrual.NaturalEnvironment;
import com.masstrix.natrual.user.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigHandler {

    private boolean hasLoaded = false;

    private boolean worldOverrideDefault = false, worldAutoGenerate = false, savePlayerData = false, h2oEnabled = false, tempEnabled = false;
    private String H2O_BUBBLE_EMPTY = "", H2O_BUBBLE_HALF = "", H2O_BUBBLE_FULL = "";

    private File dataFolder = NaturalEnvironment.getInstance().getDataFolder();
    private File defaultWorldFile = null;

    public String getH2O_BUBBLE_EMPTY() {
        return H2O_BUBBLE_EMPTY;
    }

    public String getH2O_BUBBLE_HALF() {
        return H2O_BUBBLE_HALF;
    }

    public String getH2O_BUBBLE_FULL() {
        return H2O_BUBBLE_FULL;
    }

    public void reload() {
        Logger.info("Reloading config files...");
        NaturalEnvironment.getInstance().saveDefaultConfig();
        NaturalEnvironment.getInstance().reloadConfig();

        FileConfiguration config = NaturalEnvironment.getInstance().getConfig();

        Logger.info(config);

        tempEnabled = config.getBoolean("temperature.enabled");
        savePlayerData = config.getBoolean("save-player-data");
        worldOverrideDefault = config.getBoolean("world.override-default");
        worldAutoGenerate = config.getBoolean("world.auto-generate");

        h2oEnabled = config.getBoolean("h2o.enabled");
        H2O_BUBBLE_FULL = config.getString("h2o.full");
        H2O_BUBBLE_HALF = config.getString("h2o.half");
        H2O_BUBBLE_EMPTY = config.getString("h2o.empty");

        Logger.info(H2O_BUBBLE_HALF);
    }
}
