package com.masstrix.natrual;

import com.masstrix.natrual.listeners.DeathListener;
import com.masstrix.natrual.user.User;
import com.masstrix.natrual.user.UserManager;
import com.masstrix.natrual.util.StringUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

final class NaturalSystem {

    private Random random = new Random();

    NaturalSystem(NaturalEnvironment plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            Location location = player.getLocation();
            plugin.getChunkData().fetchChunk(world, location.getChunk());
        }

        startSystem(plugin);
    }

    private void startSystem(NaturalEnvironment plugin) {
        new BukkitRunnable() {
            int damageTick = 0;
            @Override
            public void run() {
                damageTick = damageTick > 20 ? 0 : damageTick++;
                for (User user : UserManager.getAll()) {
                    Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) continue;

                    if (damageTick == 0 && random.nextInt(70) + 1 < (user.getTemp() / 90 > 5 ? 5 : user.getTemp() / 90)){
                        user.hydrate(-1);
                    }

                    if ((user.getTemp() > 75 || user.getTemp() < -12) && random.nextInt(70) + 1 < 4) {
                        if (player.getHealth() - 0.5 >= 0) {
                            player.damage(0.5, null);
                        } else {
                            DeathListener.kill(player, "%s let the temperature get to them");
                            user.reset();
                        }
                    }

                    if (user.getThirst() < 2 && damageTick % (random.nextInt(2) + 3) == 0) {
                        if (player.getHealth() - 1.5 >= 0) {
                            player.damage(1.5, null);
                        } else {
                            DeathListener.kill(player, "%s died from dehydration");
                            user.reset();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);

        new BukkitRunnable() {
            boolean b = true;

            @Override
            public void run() {
                for (User user : UserManager.getAll()) {
                    //user.updateTemp();
                    user.updateTemperature();
                    Player player = Bukkit.getPlayer(user.getUniqueId());
                    if (player == null) continue;

                    StringBuilder bar = new StringBuilder();
                    for (double i = 0; i < 20; i++) {
                        bar.append(i == user.getThirst()
                                ? StringUtil.color(NaturalEnvironment.getConfigs().getH2O_BUBBLE_HALF())
                                : i < user.getThirst()
                                ? StringUtil.color(NaturalEnvironment.getConfigs().getH2O_BUBBLE_FULL())
                                : StringUtil.color(NaturalEnvironment.getConfigs().getH2O_BUBBLE_EMPTY()));
                    }

                    String h2o = (user.getThirst() < 5 ? b ? "\u00A7cH²O " : "\u00A7fH²O " : "\u00A7fH²O ") + bar;

                    double o = user.getTemp();

                    String tem = "\u00A7" + (o > 70 ? (b ? "c" : "f")
                            : o >= 30 ? "e"
                            : o < -8 ? (b ? "c" : "f")
                            : o < 10 ? "f" : o < 0 ? "b"
                            : "a") + String.format("%.1f°", o);

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(h2o + " \u00A77- " + tem));
                }
                b = !b;
            }
        }.runTaskTimer(plugin, 5, 5);
    }
}
