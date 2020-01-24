package me.moderator_man.fo.cmd.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.moderator_man.fo.cmd.Command;

public class PlayerInfo extends Command
{
	public PlayerInfo()
	{
		super("PlayerInfo", "Gets auth information about a logged-in player.", true, false);
	}

	@Override
	public boolean run(CommandSender sender, String[] args)
	{
		String username = args[0];
		
		for (Player p : fo.getServer().getOnlinePlayers())
		{
			if (!fo.pcomp(p, username))
				continue;
			
			return true;
		}
		
		return false;
	}
}
