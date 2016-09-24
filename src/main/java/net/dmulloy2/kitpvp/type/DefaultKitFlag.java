/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.type;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

/**
 * @author dmulloy2
 */

public class DefaultKitFlag extends Flag<String> {

	public DefaultKitFlag(String name) {
		super(name);
	}

	@Override
	public String parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat {
		return input;
	}

	@Override
	public String unmarshal(Object o) {
		return o.toString();
	}

	@Override
	public Object marshal(String o) {
		return o;
	}
}
