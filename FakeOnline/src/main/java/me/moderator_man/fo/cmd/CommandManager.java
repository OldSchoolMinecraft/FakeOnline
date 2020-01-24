package me.moderator_man.fo.cmd;

import me.moderator_man.fo.AliasMap;
import me.moderator_man.fo.cmd.commands.ChangePassword;
import me.moderator_man.fo.cmd.commands.DeleteAccount;
import me.moderator_man.fo.cmd.commands.Login;
import me.moderator_man.fo.cmd.commands.PlayerInfo;
import me.moderator_man.fo.cmd.commands.Register;

public class CommandManager
{
	private AliasMap<String, Command> commands;
	
	public CommandManager()
	{
		commands = new AliasMap<String, Command>();
	}
	
	public void onEnable()
	{
		// register commands
		register("register", new Register());
		register("login", new Login());
		register("changepassword", new ChangePassword(), "cp", "passwd");
		
		// admin only
		register("deleteaccount", new DeleteAccount(), "delacc");
		register("playerinfo", new PlayerInfo(), "pinfo", "pi");
	}
	
	public void register(String realKey, Command command, String...aliases)
	{
		commands.put(realKey, command);
		for (String alias : aliases)
			commands.alias(realKey, alias);
	}
	
	public Command getCommand(String call)
	{
		return commands.get(call);
	}
}
