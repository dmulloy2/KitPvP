/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.listeners;

import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.data.PlayerData;
import net.dmulloy2.types.LazyLocation;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author dmulloy2
 */

public class EntityListener implements Listener {
	private final KitPvP plugin;

	public EntityListener(KitPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity killed = event.getEntity();
		if (killed instanceof Player) {
			Player player = (Player) killed;

			String world = player.getWorld().getName().toLowerCase();
			if (! world.equals(Config.world)) {
				return;
			}

			event.getDrops().clear();
			event.setDroppedExp(0);

			Player killer = player.getKiller();
			if (killer == null) {
				EntityDamageEvent damage = player.getLastDamageCause();
				if (damage instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent damageByEntity = (EntityDamageByEntityEvent) damage;
					Entity damager = damageByEntity.getDamager();
					if (damager instanceof Player) {
						killer = (Player) damager;
					} else if (damager instanceof Projectile) {
						Projectile proj = (Projectile) damager;
						if (proj.getShooter() instanceof Player) {
							killer = (Player) proj.getShooter();
						}
					}
				}
			}

			// Force them to respawn
			if (Config.forceRespawn && plugin.isProtocolLibEnabled()) {
				plugin.getProtocolLibHandler().forceRespawn(player);
			}

			if (killer != null) {
				LazyLocation lobby = plugin.getDataHandler().getLobby();
				if (lobby != null) {
					killer.teleport(lobby.getLocation());
				} else {
					killer.teleport(killer.getWorld().getSpawnLocation());
				}

				PlayerData data = plugin.getPlayerDataCache().getData(killer);
				data.setKills(data.getKills() + 1);
				data.setStreak(data.getStreak() + 1);

				data = plugin.getPlayerDataCache().getData(player);
				data.setDeaths(data.getDeaths() + 1);
				data.setStreak(0);

				plugin.getScoreboardHandler().update(killer);
				plugin.getScoreboardHandler().update(player);
			}
		}
	}
}
