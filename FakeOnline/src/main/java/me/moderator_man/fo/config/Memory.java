package me.moderator_man.fo.config;

import java.util.HashMap;
import java.util.Map;

public class Memory
{
	private Map<String, Object> data;
	
	public Memory()
	{
		data = new HashMap<String, Object>();
	}
	
	public void set(String key, Object value)
	{
		for (String k : data.keySet())
		{
			if (k.equalsIgnoreCase(key))
			{
				data.remove(key);
				data.put(key, value);
				return;
			}
		}
		data.put(key, value);
	}
	
	public void removeByKey(String key, Object value)
	{
		data.remove(key);
	}
	
	public void removeByPair(String key, Object value)
	{
		data.remove(key, value);
	}
	
	public Object get(String key)
	{
		return data.get(key);
	}
	
	public Object get(String key, Object defaultObj)
	{
		Object obj = data.get(key);
		if (obj != null)
			return obj;
		return defaultObj;
	}
	
	public Map<String, Object> getMap()
	{
		return data;
	}
}
