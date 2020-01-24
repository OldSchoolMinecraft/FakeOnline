package me.moderator_man.fo.cmd.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.moderator_man.fo.cmd.Command;

public class DeleteAccount extends Command
{
	public DeleteAccount()
	{
		super("DeleteAccount", "Delete an account", true, false);
	}
	
	public boolean run(CommandSender sender, String[] args)
	{
		Player ply = (Player) sender;
		
		if (!ply.isOp() && !ply.hasPermission("fo.delete"))
		{
			fo.sendError(sender, "You don't have permission to delete accounts!");
			return true;
		}
		
		if (args.length < 1)
			return false;
		
		String username = args[0];
		
		if (!fo.um.isRegistered(username))
		{
			fo.sendError(sender, "That player doesn't have an account!");
			return true;
		}
		
		fo.um.deleteUser(username);
		for (Player on : fo.getServer().getOnlinePlayers())
			if (on.getName().equalsIgnoreCase(username))
				fo.um.deauthenticateUser(username);
		fo.sendSuccess(sender, String.format("Successfully deleted account of '%s'!", username));
		
		return true;
	}
}
