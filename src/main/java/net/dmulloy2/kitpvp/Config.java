/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.config.ConfigParser;
import net.dmulloy2.config.Key;
import net.dmulloy2.config.ValueOptions;
import net.dmulloy2.config.ValueOptions.ValueOption;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class Config {

	private Config() {
	}

	public static void load(SwornPlugin plugin) {
		ConfigParser.parse(plugin, Config.class);
	}

	@Key("world")
	@ValueOptions(ValueOption.LOWER_CASE)
	public static String world = "kitpvp";

	@Key("spawnRegion")
	@ValueOptions(ValueOption.LOWER_CASE)
	public static String spawnRegion = "spawn";

	@Key("defaultKit")
	@ValueOptions(ValueOption.LOWER_CASE)
	public static String defaultKit = "leather";

	@Key("kitSelector")
	@ValueOptions(ValueOption.PARSE_ITEM)
	public static ItemStack kitSelector = ItemUtil.readItem("COMPASS, 1, name:&9&l>_&4&lKit_Selector_&9&l<");

	@Key("gui.kitSelection.title")
	public static String kitSelectionTitle = "&b&lKit Selection";

	@Key("gui.kitSelection.size")
	public static int kitSelectionSize = -1;

	@Key("gui.kitConfirm.title")
	public static String kitConfirmTitle = "&b&lPreview Kit: &9&l%k";

	@Key("forceRespawn")
	public static boolean forceRespawn = true;

	@Key("messages.prefix")
	@ValueOptions(ValueOption.FORMAT)
	public static String prefix = FormatUtil.format("&8&l[&b&lKitPvP&8&l] &8\u00BB &e ");

	@Key("permissionPrefix")
	public static String permissionPrefix = "kitpvp.kit.";
}