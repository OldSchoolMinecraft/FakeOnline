package me.moderator_man.fo.cmd;

import org.bukkit.command.CommandSender;

import me.moderator_man.fo.FakeOnline;

public abstract class Command
{
	protected static FakeOnline fo = FakeOnline.instance;
	
	private String name;
	private String desc;
	private boolean adminOnly;
	private boolean playerOnly;
	
	public Command(String name, String desc)
	{
		this.name = name;
		this.desc = desc;
		this.adminOnly = false;
		this.playerOnly = true;
	}
	
	public Command(String name, String desc, boolean adminOnly)
	{
		this.name = name;
		this.desc = desc;
		this.adminOnly = adminOnly;
		this.playerOnly = true;
	}
	
	public Command(String name, String desc, boolean adminOnly, boolean playerOnly)
	{
		this.name = name;
		this.desc = desc;
		this.adminOnly = adminOnly;
		this.playerOnly = playerOnly;
	}
	
	public abstract boolean run(CommandSender sender, String[] args);
	
	public String getName()
	{
		return name;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
	public boolean isAdminOnly()
	{
		return adminOnly;
	}
	
	public boolean isPlayerOnly()
	{
		return playerOnly;
	}
}
