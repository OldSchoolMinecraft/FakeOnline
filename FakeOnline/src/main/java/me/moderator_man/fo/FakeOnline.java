package me.moderator_man.fo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

import me.moderator_man.fo.cmd.CommandManager;
import me.moderator_man.fo.config.ConfigurationManager;
import me.moderator_man.fo.user.UserManager;

public class FakeOnline extends JavaPlugin
{
	public static FakeOnline instance;
	
	public UserManager um;
	public ConfigurationManager cm;
	public PlayerHandler handler;
	public World world;
	public CommandManager cmdm;
	
	public void onEnable()
	{
		instance = this;
		
		world = getServer().getWorlds().get(0);
		
		if (world == null)
			System.out.println("WORLD IS NULL FOR SOME DUMB FUCKING REASON");
		
		um = new UserManager();
		cm = new ConfigurationManager();
		cmdm = new CommandManager();
		
		cmdm.onEnable();
		cm.load();
		
		handler = new PlayerHandler(this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_LOGIN, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_MOVE, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_DROP_ITEM, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_PICKUP_ITEM, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_BED_ENTER, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_BUCKET_EMPTY, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_BUCKET_FILL, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_COMMAND_PREPROCESS, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT_ENTITY, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_PORTAL, handler, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGE, handler, Priority.Normal, this);
		
		File dir1 = new File("fo-data");
		if (!dir1.exists())
			dir1.mkdir();
		else if (!dir1.isDirectory()) {
			dir1.delete();
			dir1.mkdir();
		}
		
		System.out.println("FakeOnline enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		String command = cmd.getName().toLowerCase();
		me.moderator_man.fo.cmd.Command cmnd = cmdm.getCommand(command);
		
		if (cmnd != null)
		{
			if (cmnd.isPlayerOnly() && !(sender instanceof Player))
			{
				sendError(sender, "Only players can use this command!");
				return false;
			}
			
			return cmnd.run(sender, args);
		}
		
		return false;
	}
	
	public static final String prefix = "%s[%sFakeOnline%s]%s ";
	public static final String error_prefix = String.format(prefix, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY, ChatColor.RED);
	public static final String success_prefix = String.format(prefix, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY, ChatColor.GREEN);
	public static final String adminlog_prefix = String.format(prefix, ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY, ChatColor.AQUA);
	
	public String get(String url)
	{
		try
		{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
			return response.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	public void sendError(CommandSender sender, String msg)
	{
		if (sender != null)
			sender.sendMessage(error_prefix + msg);
	}
	
	public void sendSuccess(CommandSender sender, String msg)
	{
		if (sender != null)
			sender.sendMessage(success_prefix + msg);
	}
	
	public void sendAdminLog(boolean error, String msg)
	{
		System.out.println(msg);
		
		if (!cm.getBoolean("adminlog", false))
			return;
		
		for (Player player : getServer().getOnlinePlayers())
			if ((player.isOp() || player.hasPermission("fo.admin")) && um.isAuthenticated(player.getName()))
				player.sendMessage(error ? error_prefix : adminlog_prefix + msg);
	}
	
	public void sendAdminLog(boolean error, String msg, boolean bypass)
	{
		System.out.println(msg);
		
		if (!cm.getBoolean("adminlog", false) && !bypass)
			return;
		
		for (Player player : getServer().getOnlinePlayers())
			if ((player.isOp() || player.hasPermission("fo.admin")) && um.isAuthenticated(player.getName()))
				player.sendMessage(error ? error_prefix : adminlog_prefix + msg);
	}
	
	public void sendAdminLog(CommandSender sender, boolean error, String[] lines)
	{
		for (String line : lines)
		{
			String msg = String.format("%s%s", error ? ChatColor.RED : ChatColor.AQUA, line);
			sender.sendMessage(msg);
		}
	}

	public String hash(String input)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(input.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < hash.length; i++)
	        {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	public boolean pcomp(Player ply, String username)
	{
		return pcomp(ply, username, false);
	}
	
	public boolean pcomp(Player ply, String username, boolean sens)
	{
		if (sens)
		{
			if (ply.getName().equals(username) || ply.getDisplayName().equals(username))
				return true;
			return false;
		} else {
			if (ply.getName().equalsIgnoreCase(username) || ply.getDisplayName().equalsIgnoreCase(username))
				return true;
			return false;
		}
	}
	
	public void onDisable()
	{
		cm.save();
		
		System.out.println("FakeOnline disabled.");
	}
	
	public UserManager getUserManager()
	{
		return um;
	}
	
	public ConfigurationManager getConfigurationManager()
	{
		return cm;
	}
	
	public World getWorld()
	{
		return world;
	}
}
