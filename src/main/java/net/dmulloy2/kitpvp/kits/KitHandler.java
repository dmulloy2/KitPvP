/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.kits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class KitHandler implements Reloadable {
	private static final String FILE_NAME = "kits.yml";

	private final List<Kit> kits;
	private final KitPvP plugin;

	public KitHandler(KitPvP plugin) {
		this.plugin = plugin;
		this.kits = new ArrayList<>();
		this.reload();
	}

	public List<Kit> getKits() {
		return kits;
	}

	public Kit getKit(String name) {
		name = name.toLowerCase();

		for (Kit kit : kits) {
			if (kit.getName().equals(name)) {
				return kit;
			}
		}

		return null;
	}

	public Kit getKit(ItemStack item, Player player) {
		if (item == null) {
			return null;
		}

		for (Kit kit : kits) {
			ItemStack icon = kit.getIcon(player);
			if (icon != null) {
				if (item.getType() == icon.getType()
						&& item.getAmount() == icon.getAmount()
						&& item.getDurability() == icon.getDurability()) {
					return kit;
				}
			}
		}

		return null;
	}

	public void addKit(Kit kit) {
		kits.add(kit);
	}

	@Override
	public void reload() {
		try {
			kits.clear();

			File file = new File(plugin.getDataFolder(), FILE_NAME);
			if (! file.exists()) {
				plugin.saveResource(FILE_NAME, false);
			}

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			Map<String, Object> values = config.getConfigurationSection("kits").getValues(false);
			for (Entry<String, Object> entry : values.entrySet()) {
				String name = entry.getKey();

				try {
					MemorySection section = (MemorySection) entry.getValue();
					Kit kit = new Kit(plugin, section);
					kits.add(kit);
				} catch (Throwable ex) {
					plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading kit {0}", name));
				}
			}
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading kits"));
		}
	}

	public void save() {
		try {
			File file = new File(plugin.getDataFolder(), FILE_NAME);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			YamlConfiguration config = new YamlConfiguration();

			for (Kit kit : kits) {
				config.createSection("kits." + kit.getName(), kit.serialize());
			}

			config.save(file);
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "saving kits"));
		}
	}
}
