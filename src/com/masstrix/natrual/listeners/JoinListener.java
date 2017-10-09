package com.masstrix.natrual.listeners;

import com.masstrix.natrual.user.User;
import com.masstrix.natrual.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserManager.add(User.load(player.getUniqueId()));
    }
}
