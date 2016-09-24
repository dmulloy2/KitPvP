/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.kitpvp.integration;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.integration.DependencyProvider;
import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.kits.Kit;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * @author dmulloy2
 */
public class WorldGuardHandler extends DependencyProvider<WorldGuardPlugin> {

	public WorldGuardHandler(SwornPlugin handler) {
		super(handler, "WorldGuard");
	}

	public void handlePlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo();
		Location from = event.getFrom();
		KitPvP plugin = (KitPvP) handler;

		WorldGuardPlugin worldGuard = getDependency();
		RegionManager rm = worldGuard.getRegionManager(from.getWorld());
		ApplicableRegionSet regions = rm.getApplicableRegions(from);

		Player player = event.getPlayer();

		for (ProtectedRegion region : regions) {
			if (region.getId().toLowerCase().equals(Config.spawnRegion)) {
				ApplicableRegionSet newRegions = rm.getApplicableRegions(to);
				for (ProtectedRegion newRegion : newRegions) {
					if (newRegion.getId().toLowerCase().equals(Config.spawnRegion)) {
						return;
					}
				}

				Kit kit = plugin.getKitHandler().getKit(Config.defaultKit);
				if (kit != null) {
					kit.giveItems(player);
					player.sendMessage(plugin.getPrefix() + FormatUtil.format("&eYou have been given the default kit!"));
				}

				return;
			}
		}
	}
}