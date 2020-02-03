package me.moderator_man.fo.user;

import java.io.Serializable;
import java.util.ArrayList;

public class IPMetadata implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private int count;
	private ArrayList<String> usernames;
	
	public IPMetadata(String ip, int count)
	{
		this.ip = ip;
		this.count = count;
		this.usernames = new ArrayList<String>();
	}
	
	public boolean canRegisterOnIP()
	{
		return count < 2;
	}
	
	public void incrementCount()
	{
		count++;
	}
	
	public String getIP()
	{
		return ip;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public ArrayList<String> getUsernames()
	{
		return usernames;
	}
}
