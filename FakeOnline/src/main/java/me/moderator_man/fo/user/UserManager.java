package me.moderator_man.fo.user;

import java.io.File;
import java.util.ArrayList;

import me.moderator_man.meridian.serial.FormatReader;
import me.moderator_man.meridian.serial.FormatWriter;

public class UserManager
{
	//private static final FakeOnline fo = FakeOnline.instance;
	
	private ArrayList<String> frozenUsers;
	private ArrayList<String> authenticatedUsers;
	private ArrayList<String> usersWithSession;
	
	public UserManager()
	{
		frozenUsers = new ArrayList<String>();
		authenticatedUsers = new ArrayList<String>();
		usersWithSession = new ArrayList<String>();
	}
	
	public boolean userHasSession(String username)
	{
		for (String user : usersWithSession)
			return user.equalsIgnoreCase(username);
		return false;
	}
	
	public void put_userHasSession(String username)
	{
		usersWithSession.add(username);
	}
	
	public boolean playerDataExists(String username)
	{
		String[] files = new File(getDataFolder()).list();
		int count = 0;
		
		for (String file : files)
			if (file.equalsIgnoreCase(username + ".dat"))
				count++;
		if (count > 1)
			for (String file : files)
				new File(file).delete();
		else
			for (String file : files)
				if (file.equalsIgnoreCase(username + ".dat"))
					return true;
		
		return false;
	}
	
	public boolean isApproved(String username)
	{
		UserMetadata meta = getUserMetadata(username);
		if (meta != null)
			return meta.isApproved();
		return false;
	}
	
	public void approve(String username)
	{
		UserMetadata meta = getUserMetadata(username);
		if (meta != null)
		{
			meta.setApproved(true);
			updateUserMetadata(username, meta);
		} else {
			System.out.println(String.format("FAILED TO UPDATE USER METADATA FOR: '%s'", username));
		}
	}
	
	public boolean isAuthenticated(String username)
	{
		return authenticatedUsers.contains(username);
	}
	
	public boolean isRegistered(String username)
	{
		if (playerDataExists(username))
			return true;
		return false;
	}
	
	public File getPlayerDataFileIgnoreCase(String username)
	{
		for (String file : new File(getDataFolder()).list())
		{
			if (file.equalsIgnoreCase(username + ".dat"))
			{
				String val = getDataFolder() + file;
				System.out.println("DEBUG " + val);
				return new File(val);
			}
		}
		
		return null;
	}
	
	public String getPlayerDataIgnoreCase(String username)
	{
		String[] files = new File(getDataFolder()).list();
		int count = 0;
		
		for (String file : files)
			if (file.equalsIgnoreCase(username + ".dat"))
				count++;
		if (count > 1)
			for (String file : files)
				new File(file).delete();
		for (String file : files)
			if (file.equalsIgnoreCase(username + ".dat"))
				return getDataFolder() + file;
		
		return getDataFolder() + username + ".dat";
	}
	
	public void deleteUser(String username)
	{
		File file = getPlayerDataFileIgnoreCase(username);
		if (file.exists())
			file.delete();
	}
	
	public void registerUser(String username, String password, boolean approved)
	{
		UserMetadata metadata = new UserMetadata(password, approved);
		FormatWriter<UserMetadata> writer = new FormatWriter<UserMetadata>();
		writer.write(metadata, getPlayerDataIgnoreCase(username));
	}
	
	public UserMetadata getUserMetadata(String username)
	{
		FormatReader<UserMetadata> reader = new FormatReader<UserMetadata>();
		return reader.read(getPlayerDataIgnoreCase(username));
	}
	
	public void updateUserMetadata(String username, UserMetadata metadata)
	{
		File file = new File(getPlayerDataIgnoreCase(username));
		if (file.exists())
			file.delete();
		FormatWriter<UserMetadata> writer = new FormatWriter<UserMetadata>();
		writer.write(metadata, getPlayerDataIgnoreCase(username));
	}
	
	public void freezePlayer(String username)
	{
		frozenUsers.add(username);
	}
	
	public void unfreezePlayer(String username)
	{
		frozenUsers.remove(username);
	}
	
	public boolean isFrozen(String username)
	{
		return frozenUsers.contains(username);
	}
	
	public void authenticateUser(String username)
	{
		authenticatedUsers.add(username);
	}
	
	public void deauthenticateUser(String username)
	{
		authenticatedUsers.remove(username);
	}
	
	public String getDataFolder()
	{
		return "fo-data/";
	}
	
	public String getDataPath(String username)
	{
		return String.format("fo-data/%s.dat", username);
	}
}
