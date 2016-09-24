/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.kitpvp.integration;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.integration.DependencyProvider;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * @author dmulloy2
 */
public class WorldEditHandler extends DependencyProvider<WorldEditPlugin> {

	public WorldEditHandler(SwornPlugin handler) {
		super(handler, "WorldEdit");
	}
}