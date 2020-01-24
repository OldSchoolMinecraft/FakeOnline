package me.moderator_man.fo.cmd.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.moderator_man.fo.cmd.Command;

public class Register extends Command
{
	public Register()
	{
		super("Register", "Register an account");
	}
	
	public boolean run(CommandSender sender, String[] args)
	{
		Player ply = (Player) sender;
		
		// validate length of arguments
		if (args.length < 2)
			return false;
		
		// check if user is already registered
		if (fo.um.isRegistered(ply.getName()))
		{
			fo.sendError(sender, "You are already registered!");
			return true;
		}
		
		// check if the password confirmation matches
		String password = args[0].trim();
		String confirm = args[1].trim();
		if (!confirm.equals(password))
		{
			fo.sendError(sender, "Passwords did not match!");
			return true;
		}
		
		if (fo.um.isAuthenticated(ply.getName()) && fo.um.isFrozen(ply.getName()))
		{
			fo.um.registerUser(ply.getName(), fo.hash(password), true);
			fo.um.unfreezePlayer(ply.getName());
		} else if (!fo.um.isAuthenticated(ply.getName())) {
			fo.um.registerUser(ply.getName(), fo.hash(password), false);
			fo.um.authenticateUser(ply.getName());
		}
				
		fo.sendSuccess(sender, "Successfully registered!");
		
		if (fo.cm.getBoolean("adminlog", false))
			fo.sendAdminLog(false, String.format("Player '%s' registered.", ply.getName()));
		
		return true;
	}
}
