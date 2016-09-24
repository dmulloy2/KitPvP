/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.kitpvp.scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.data.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Asynchronous, cached leaderboard
 * @author dmulloy2
 */
public class Leaderboard {
	private static final String CACHE_NAME = "cache.yml";

	private List<String> leaderboard;
	private List<String> loserboard;

	private boolean expired = true;
	private boolean updating = false;

	private final KitPvP plugin;

	public Leaderboard(KitPvP plugin) {
		this.plugin = plugin;
		this.leaderboard = new ArrayList<>();
		this.loserboard = new ArrayList<>();
		this.loadCache();
	}

	private void loadCache() {
		File cache = new File(plugin.getDataFolder(), CACHE_NAME);
		if (!cache.exists()) {
			return;
		}

		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(cache);
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading leaderboard cache"));
			return;
		}

		this.leaderboard = config.getStringList("leaderboard");
		this.loserboard = config.getStringList("loserboard");

		long delay = 20 * 60 * 5; // 5 minutes
		new InvalidateTask().runTaskTimer(plugin, delay, delay);
	}

	public void saveCache() {
		File cache = new File(plugin.getDataFolder(), CACHE_NAME);
		if (!cache.exists()) {
			try {
				cache.createNewFile();
			} catch (IOException ex) {
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "creating leaderboard cache"));
				return;
			}
		}

		YamlConfiguration config = new YamlConfiguration();
		config.set("leaderboard", leaderboard);
		config.set("loserboard", loserboard);

		try {
			config.save(cache);
		} catch (IOException ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving leaderboard cache"));
		}
	}

	public void invalidateCache() {
		expired = true;
	}

	public List<String> getLeaderboard() {
		if (expired) {
			update();
		}

		return leaderboard;
	}

	public List<String> getLoserboard() {
		if (expired) {
			update();
		}

		return loserboard;
	}

	private void update() {
		if (!updating) {
			updating = true;
		} else {
			return;
		}

		new LeaderboardUpdateThread();
	}

	private class InvalidateTask extends BukkitRunnable {
		@Override
		public void run() {
			invalidateCache();
		}
	}

	private class LeaderboardUpdateThread extends Thread {
		private LeaderboardUpdateThread() {
			super("KitPvP - Leaderboard Update");
			setPriority(MIN_PRIORITY);
			start();
		}

		@Override
		public void run() {
			Map<PlayerData, Integer> kills = new HashMap<>();
			Map<PlayerData, Integer> deaths = new HashMap<>();

			for (PlayerData data : plugin.getPlayerDataCache().getAllData().values()) {
				kills.put(data, data.getKills());
				deaths.put(data, data.getDeaths());
			}

			Comparator<Entry<PlayerData, Integer>> killCompare = new Comparator<Entry<PlayerData, Integer>>() {
				@Override
				public int compare(Entry<PlayerData, Integer> entry1, Entry<PlayerData, Integer> entry2) {
					return -entry1.getValue().compareTo(entry2.getValue());
				}
			};

			Comparator<Entry<PlayerData, Integer>> deathCompare = new Comparator<Entry<PlayerData, Integer>>() {
				@Override
				public int compare(Entry<PlayerData, Integer> entry1, Entry<PlayerData, Integer> entry2) {
					return entry1.getValue().compareTo(entry2.getValue());
				}
			};

			List<Entry<PlayerData, Integer>> sortedKills = new ArrayList<>(kills.entrySet());
			Collections.sort(sortedKills, killCompare);

			kills.clear();

			List<Entry<PlayerData, Integer>> sortedDeaths = new ArrayList<>(deaths.entrySet());
			Collections.sort(sortedDeaths, deathCompare);

			deaths.clear();

			leaderboard.clear();
			loserboard.clear();

			String format = "&7{0}. {1} &c{2}";

			for (int i = 0; i < sortedKills.size(); i++) {
				PlayerData data = sortedKills.get(i).getKey();
				data.setRank(i + 1);
				if (i < 10) {
					leaderboard.add(FormatUtil.format(format, i + 1, data.getLastKnownBy(), data.getKills()));
				}
			}

			for (int i = 0; i < sortedDeaths.size() && i < 10; i++) {
				PlayerData data = sortedKills.get(i).getKey();
				loserboard.add(FormatUtil.format(format, i + 1, data.getLastKnownBy(), data.getDeaths()));
			}

			updating = false;
			expired = false;

			plugin.getPlayerDataCache().save();

			new ScoreboardCallbackThread();
		}
	}

	private class ScoreboardCallbackThread extends Thread {
		private ScoreboardCallbackThread() {
			super("KitPvP - Scoreboard Callback");
			setPriority(MIN_PRIORITY);
			start();
		}

		@Override
		public void run() {
			plugin.getPlayerDataCache().cleanup();
			plugin.getScoreboardHandler().updateAll();
		}
	}
}