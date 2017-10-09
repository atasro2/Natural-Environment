package com.masstrix.natrual;

import com.masstrix.natrual.commands.HydrateCommand;
import com.masstrix.natrual.commands.NaturalEnvironmentCommand;
import com.masstrix.natrual.config.ConfigHandler;
import com.masstrix.natrual.listeners.*;
import com.masstrix.natrual.recipes.CraftRecipes;
import com.masstrix.natrual.recipes.EnchantmentsManager;
import com.masstrix.natrual.recipes.SmeltRecipes;
import com.masstrix.natrual.world.WorldDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/*
* TODO Magic mushroom (turns normal cow into magic)
* TODO Tree capitator system (Quick Fall)
*
* TODO
* */

public final class NaturalEnvironment extends JavaPlugin implements Listener {

    private static NaturalEnvironment instance;
    private static ConfigHandler configHandler;
    private WorldDataHandler worldDataHandler;

    private boolean debug = true;

    public boolean loaded = false;

    public static final String PREFIX = "\u00A77[\u00A7aNatural\u00A77] ";

    public boolean toggleDebugMode() {
        return debug = !debug;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public static NaturalEnvironment getInstance() {
        return instance;
    }

    public WorldDataHandler getChunkData() {
        return worldDataHandler;
    }

    public static ConfigHandler getConfigs() {
        return configHandler;
    }

    public final boolean isLoaded() {
        return loaded;
    }

    void setLoaded(boolean b) {
        this.loaded = b;
    }

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        new VersionChecker(43290);
        configHandler = new ConfigHandler();
        configHandler.reload();

        worldDataHandler = new WorldDataHandler();

        registerListener(this, new WalkListener(), new RespawnListener(), new QuitListener(),
                new JoinListener(), new DeathListener(), new InventoryListener(),
                new ConsumeListener(), new EnchantListener(), new InteractListener(this),
                new DamageListener(), new ChunkListener());

        getCommand("NaturalEnvironment").setExecutor(new NaturalEnvironmentCommand());
        getCommand("Hydrate").setExecutor(new HydrateCommand());

        new EnchantmentsManager();
        new NaturalSystem(this);
        new CraftRecipes().register();
        new SmeltRecipes();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().setHeldItemSlot(5);
        }

        try {
            File f = new File(getDataFolder(), "config.yml");
            if (!f.exists()) saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onDisable() {

    }

    private void registerListener(Listener... listener) {
        PluginManager manager = Bukkit.getPluginManager();
        for (Listener l : listener)
            manager.registerEvents(l, this);
    }
}
