package me.moderator_man.fo;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityHandler extends EntityListener
{
	private FakeOnline fo;
	
	public EntityHandler(FakeOnline fo)
	{
		this.fo = fo;
	}
	
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (!fo.um.isAuthenticated(player.getName()) || fo.um.isFrozen(player.getName()))
			{
				event.setCancelled(true);
			}
		}
	}
}
