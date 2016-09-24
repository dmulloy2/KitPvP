/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.data;

import java.io.File;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.types.LazyLocation;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author dmulloy2
 */

@Getter
public class DataHandler {
	private static final String FILE_NAME = "data.yml";

	private LazyLocation lobby;
	private final KitPvP plugin;

	public DataHandler(KitPvP plugin) {
		this.plugin = plugin;
		this.load();
	}

	public void setLobby(Location location) {
		this.lobby = new LazyLocation(location);
	}

	private void load() {
		try {
			File file = new File(plugin.getDataFolder(), FILE_NAME);
			if (! file.exists()) {
				return;
			}

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			if (config.isSet("lobby")) {
				this.lobby = (LazyLocation) config.get("lobby");
			}
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading data.yml"));
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

			if (lobby != null) {
				config.set("lobby", lobby);
			}

			config.save(file);
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "saving data.yml"));
		}
	}
}
