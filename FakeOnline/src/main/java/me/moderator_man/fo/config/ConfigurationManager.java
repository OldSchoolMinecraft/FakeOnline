package me.moderator_man.fo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigurationManager
{
	private Memory memory;
	
	public ConfigurationManager()
	{
		memory = new Memory();
	}
	
	public void load()
	{
		try
		{
			File cfgfile = new File("fo-data/fakeonline.cfg");
			if (!cfgfile.exists())
			{
				cfgfile.createNewFile();
				setString("reject-policy", "default"); // possible values: default (supplement), supplement, relaxed, strict
				setString("reject-message", "Not authenticated with oldschoolminecraft.com");
				setBoolean("log-successful-auths", true);
				setBoolean("log-failed-auths", true);
				setBoolean("adminlog", true);
				setBoolean("debug-mode", false);
				save();
				System.out.println("Generated new configuration!");
				return;
			}
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(cfgfile));
			
			for (Object key : properties.keySet())
			{
				if (!(key instanceof String))
					continue;
				String skey = (String) key;
				memory.set(skey, properties.get(skey));
			}
			
			System.out.println("Loaded configuration!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void save()
	{
		try
		{
			Properties properties = new Properties();
			for (Object key : memory.getMap().keySet())
			{
				if (!(key instanceof String))
					continue;
				properties.put(key, (String)memory.get((String)key));
			}
			properties.store(new FileOutputStream(new File("fo-data/fakeonline.cfg")), "EDIT AT YOUR OWN RISK!");
			
			System.out.println("Saved configuration!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getString(String key, String defaultValue)
	{
		return (String) memory.get(key, defaultValue);
	}
	
	public void setString(String key, String value)
	{
		memory.set(key, value);
	}
	
	public boolean getBoolean(String key, boolean defaultValue)
	{
		try
		{
			boolean flag = Boolean.parseBoolean(((String)memory.get(key)).trim());
			return flag;
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	public void setBoolean(String key, boolean value)
	{
		memory.set(key, value);
	}
	
	public Memory getMemory()
	{
		return memory;
	}
}
