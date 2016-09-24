/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.kitpvp.KitPvP;

/**
 * @author dmulloy2
 */

public abstract class KitPvPCommand extends Command {
	protected final KitPvP plugin;

	public KitPvPCommand(KitPvP plugin) {
		super(plugin);
		this.plugin = plugin;
		this.usesPrefix = true;
	}
}
