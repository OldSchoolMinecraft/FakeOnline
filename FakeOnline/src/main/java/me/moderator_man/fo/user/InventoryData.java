package me.moderator_man.fo.user;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Location location;
	private ItemStack[] inventory;
	private ItemStack[] armor;
	
	public InventoryData(Location location, ItemStack[] inventory, ItemStack[] armor)
	{
		this.location = location;
		this.inventory = inventory;
		this.armor = armor;
	}
	
	public InventoryData(Player player)
	{
		location = player.getLocation();
		inventory = player.getInventory().getContents();
		armor = player.getInventory().getArmorContents();
	}
	
	public InventoryData(Location location, PlayerInventory inventory)
	{
		this.location = location;
		this.inventory = inventory.getContents();
		this.armor = inventory.getArmorContents();
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public ItemStack[] getInventory()
	{
		return inventory;
	}
	
	public ItemStack[] getArmor()
	{
		return armor;
	}
}
