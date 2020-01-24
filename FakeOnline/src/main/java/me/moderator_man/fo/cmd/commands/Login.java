package me.moderator_man.fo.cmd.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.moderator_man.fo.cmd.Command;

public class Login extends Command
{
	public Login()
	{
		super("Login", "Login to your account");
	}
	
	public boolean run(CommandSender sender, String[] args)
	{
		Player ply = (Player) sender;
		
		if (!fo.um.isRegistered(ply.getName()))
		{
			fo.sendError(sender, "You must register first!");
			return true;
		}
		
		if (fo.um.isAuthenticated(ply.getName()) && !fo.um.isFrozen(ply.getName()))
		{
			fo.sendError(sender, "You are already logged in!");
			return true;
		}
		String inputPasswd = fo.hash(args[0]);
		if (inputPasswd.equals(fo.um.getUserMetadata(ply.getName()).getPassword()))
		{
			// user is authenticated with OSM but hasn't been approved for OSM logins
			if (fo.um.isAuthenticated(ply.getName()) && fo.um.isFrozen(ply.getName()))
			{
				if (fo.um.isFrozen(ply.getName()))
					fo.um.unfreezePlayer(ply.getName());
				fo.um.approve(ply.getName());
			} else if (!fo.um.isAuthenticated(ply.getName())) {
				fo.um.authenticateUser(ply.getName());
			}
			
			fo.sendSuccess(sender, "Successfully logged in!");
			
			if (fo.cm.getBoolean("adminlog", false))
				fo.sendAdminLog(false, String.format("Player '%s' logged in.", ply.getName()));
		} else {
			fo.sendError(sender, "Invalid password!");
		}
		return true;
	}
}
