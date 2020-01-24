package me.moderator_man.fo.cmd.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.moderator_man.fo.cmd.Command;
import me.moderator_man.fo.user.UserMetadata;

public class ChangePassword extends Command
{
	public ChangePassword()
	{
		super("ChangePassword", "Change your password");
	}
	
	public boolean run(CommandSender sender, String[] args)
	{
		Player ply = (Player) sender;
		
		if (!fo.um.isRegistered(ply.getName()))
		{
			fo.sendError(sender, "You must register first!");
			return true;
		}
		if (!fo.um.isAuthenticated(ply.getName()))
		{
			fo.sendError(sender, "You must login first!");
			return true;
		}
		if (args.length < 2)
			return false;
		UserMetadata metadata = fo.um.getUserMetadata(ply.getName());
		String oldPassword = metadata.getPassword();
		String newPassword = args[1];
		if (fo.hash(args[0]).equals(oldPassword))
		{
			// set the password
			metadata.setPassword(fo.hash(newPassword));
			// make sure they have to re-enter their password on OSM next time too
			metadata.setApproved(false);
			// update the metadata
			fo.um.updateUserMetadata(ply.getName(), metadata);
			fo.sendSuccess(sender, "Successfully changed password! You will need to login again.");
			// deauthenticate them so they need to login
			fo.um.deauthenticateUser(ply.getName());
		} else {
			fo.sendError(sender, "Old password was incorrect!");
			return true;
		}
		return true;
	}
}
