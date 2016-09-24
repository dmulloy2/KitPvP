/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.listeners;

import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.gui.KitSelectionGUI;
import net.dmulloy2.types.LazyLocation;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class PlayerListener implements Listener {
	private final KitPvP plugin;

	public PlayerListener(KitPvP plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.getPlayerDataCache().getData(player);

		String world = player.getWorld().getName().toLowerCase();
		if (world.equals(Config.world)) {
			plugin.getScoreboardHandler().update(player);
			giveItems(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		String world = player.getWorld().getName().toLowerCase();
		if (world.equals(Config.world)) {
			InventoryUtil.clear(player.getInventory());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		String newWorld = player.getWorld().getName().toLowerCase();
		String oldWorld = event.getFrom().getName().toLowerCase();

		if (oldWorld.equals(Config.world)) {
			InventoryUtil.clear(player.getInventory());
			plugin.getScoreboardHandler().unregister(player);
			return;
		}

		if (newWorld.equals(Config.world)) {
			plugin.getScoreboardHandler().update(player);
			giveItems(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String world = player.getWorld().getName().toLowerCase();
		if (! world.equals(Config.world)) {
			return;
		}

		giveItems(player);

		LazyLocation lobby = plugin.getDataHandler().getLobby();
		if (lobby != null) {
			player.teleport(lobby.getLocation());
		} else {
			player.teleport(player.getWorld().getSpawnLocation());
		}
	}

	private void giveItems(Player player) {
		InventoryUtil.clear(player.getInventory());
		InventoryUtil.giveItem(player, Config.kitSelector);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item != null) {
			Player player = event.getPlayer();
			if (equalsNoDurability(item, Config.kitSelector)) {
				KitSelectionGUI ksGUI = new KitSelectionGUI(plugin, player);
				plugin.getGuiHandler().open(ksGUI);
			}
		}
	}

	private boolean equalsNoDurability(ItemStack inHand, ItemStack other) {
		if (inHand == null || other == null) {
			return false;
		}

		return inHand.getType() == other.getType() && inHand.getAmount() == other.getAmount()
				&& inHand.getItemMeta().equals(other.getItemMeta());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();

		String world = from.getWorld().getName().toLowerCase();
		if (! world.equals(Config.world)) {
			return;
		}

		if (Util.coordsEqual(from, to)) {
			return;
		}

		plugin.getWorldGuardHandler().handlePlayerMove(event);
	}
}
