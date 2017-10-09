package com.masstrix.natrual.listeners;

import com.masstrix.natrual.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WalkListener implements Listener {

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean walked = event.getTo().distance(event.getFrom()) > 0;
        if (walked) {
            if (!player.isInsideVehicle()) {
                UserManager.get(player.getUniqueId()).addDistance(event.getTo().distance(event.getFrom()));
            }

            
        }
    }
}
