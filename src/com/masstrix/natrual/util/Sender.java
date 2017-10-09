package com.masstrix.natrual.util;

import com.masstrix.natrual.NaturalEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sender {

    public static void msg(CommandSender sender, String msg) {
        msg(sender, msg, true);
    }

    public static void msg(CommandSender sender, String msg, boolean b) {
        if (sender instanceof Player) {
            ((Player) sender).sendRawMessage(ChatColor.translateAlternateColorCodes(
                    '&', (b ? NaturalEnvironment.PREFIX : "") + msg));
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(
                    ChatColor.translateAlternateColorCodes('&', msg));
        }
    }
}
