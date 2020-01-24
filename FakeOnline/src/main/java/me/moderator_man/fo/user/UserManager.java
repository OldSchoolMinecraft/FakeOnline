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
	
	public UserManager()
	{
		frozenUsers = new ArrayList<String>();
		authenticatedUsers = new ArrayList<String>();
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
		if (new File(getDataPath(username)).exists())
			return true;
		return false;
	}
	
	public void deleteUser(String username)
	{
		File file = new File(getDataPath(username));
		if (file.exists())
			file.delete();
	}
	
	public void registerUser(String username, String password, boolean approved)
	{
		UserMetadata metadata = new UserMetadata(password, approved);
		FormatWriter<UserMetadata> writer = new FormatWriter<UserMetadata>();
		writer.write(metadata, getDataPath(username));
	}
	
	public UserMetadata getUserMetadata(String username)
	{
		FormatReader<UserMetadata> reader = new FormatReader<UserMetadata>();
		return reader.read(getDataPath(username));
	}
	
	public void updateUserMetadata(String username, UserMetadata metadata)
	{
		File file = new File(getDataPath(username));
		if (file.exists())
			file.delete();
		FormatWriter<UserMetadata> writer = new FormatWriter<UserMetadata>();
		writer.write(metadata, getDataPath(username));
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
	
	public String getDataPath(String username)
	{
		return String.format("fo-data/%s.dat", username);
	}
}
