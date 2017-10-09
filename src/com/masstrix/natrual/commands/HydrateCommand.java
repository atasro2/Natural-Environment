package com.masstrix.natrual.commands;

import com.masstrix.natrual.user.User;
import com.masstrix.natrual.user.UserManager;
import com.masstrix.natrual.util.Sender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HydrateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                Sender.msg(sender, "&cNo player found matching " + args[0] + "!");
            } else {
                User user = UserManager.get(player.getUniqueId());
                user.hydrate(20);
                Sender.msg(sender, "&aSet " + player.getName() + "'s H²O to 100%");
            }
            return true;
        }
        return false;
    }
}
