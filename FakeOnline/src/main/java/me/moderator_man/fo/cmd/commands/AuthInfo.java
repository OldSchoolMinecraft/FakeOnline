package me.moderator_man.fo.cmd.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.moderator_man.fo.cmd.Command;

public class AuthInfo extends Command
{
	public AuthInfo()
	{
		super("AuthInfo", "Get authentication information about a player", true, false);
	}

	@Override
	public boolean run(CommandSender sender, String[] args)
	{
		if (!sender.isOp() && !sender.hasPermission("fo.authinfo"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return true;
		}
		
		if (args.length < 1)
			return false;
		
		if (args[0].equalsIgnoreCase("all"))
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("Online players: ");
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				boolean authed = fo.um.userHasSession(player.getName());
				sb.append(String.format("%s%s, ", authed ? ChatColor.GREEN : ChatColor.RED, player.getName()));
			}
			String f1 = sb.toString().trim();
			String f2 = f1.substring(f1.length() - 1);
			sender.sendMessage(f2);
		} else {
			String username = args[0];
			Player player = Bukkit.getServer().getPlayer(username);
			
			if (player != null)
			{
				boolean authed = fo.um.userHasSession(username);
				ChatColor color = authed ? ChatColor.GREEN : ChatColor.RED;
				sender.sendMessage(String.format("%s%s is %s%s", ChatColor.WHITE, username, color, authed ? "authenticated" : "not authenticated"));
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid player!");
			}
		}
		
		return true;
	}
}
