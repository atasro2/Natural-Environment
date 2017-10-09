package com.masstrix.natrual.commands;

import com.masstrix.natrual.NaturalEnvironment;
import com.masstrix.natrual.user.UserManager;
import com.masstrix.natrual.util.Sender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NaturalEnvironmentCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Sender.msg(sender, "", false);
            Sender.msg(sender, "&a&l  Natural Environment", false);
            Sender.msg(sender, "&a/ne help &7- Print this help message.", false);
            Sender.msg(sender, "&a/ne stats &7- View plugin run stats.", false);
            Sender.msg(sender, "&a/ne debug &7- Toggle the debug console log.", false);
            Sender.msg(sender, "&a/hydrate <player> &7-&o Re-hydrate a player to full.", false);
            Sender.msg(sender, "", false);
            return true;
        } else {
            if (args[0].equalsIgnoreCase("stats")) {
                Sender.msg(sender, "", false);
                Sender.msg(sender, "&7 [Stats]&a&l  Natural Environment", false);
                Sender.msg(sender, String.format("Players Cached: &a%d",
                        UserManager.getAll().size()), false);
                Sender.msg(sender, String.format("Worlds Cached: &a%d",
                        NaturalEnvironment.getInstance().getChunkData().size()), false);
                Sender.msg(sender, String.format("Chunks Cached: &a%d",
                        NaturalEnvironment.getInstance().getChunkData().chunkSize()), false);
                Sender.msg(sender, "", false);
                return true;
            }
            else if (args[0].equalsIgnoreCase("debug")) {
                Sender.msg(sender, "Toggled debug mode to " + NaturalEnvironment.getInstance().toggleDebugMode());
            }
        }
        return false;
    }
}
