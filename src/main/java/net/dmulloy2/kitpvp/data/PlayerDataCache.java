/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.dmulloy2.io.Closer;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PlayerDataCache {
	private final KitPvP plugin;
	private final Connection connection;

	private final ConcurrentMap<UUID, PlayerData> data = new ConcurrentHashMap<>();

	public PlayerDataCache(KitPvP plugin) throws Throwable {
		this.plugin = plugin;

		Class.forName("org.sqlite.JDBC");
		File players = new File(plugin.getDataFolder(), "players.db");
		connection = DriverManager.getConnection("jdbc:sqlite:" + players.getAbsolutePath());

		Statement statement = null;

		try {
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS KitPvP_Players (uniqueId varchar(36), kills INTEGER,"
					+ " deaths INTEGER, streak INTEGER, rank INTEGER, points INTEGER, lastKnownBy varchar(16));");
		} finally {
			Closer.closeQuietly(statement);
		}
	}

	private void loadAll() {
		Statement statement = null;

		try {
			statement = connection.createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM KitPvP_Players");
			while (results.next()) {
				UUID uniqueId = UUID.fromString(results.getString("uniqueId"));
				if (!data.containsKey(uniqueId)) {
					PlayerData loaded = new PlayerData(results);
					data.put(uniqueId, loaded);
				}
			}
		} catch (SQLException ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data"));
		} finally {
			Closer.closeQuietly(statement);
		}
	}

	public Map<UUID, PlayerData> getAllData() {
		loadAll();
		return data;
	}

	public PlayerData getData(OfflinePlayer player) {
		// Try to grab from the cache
		UUID uniqueId = player.getUniqueId();
		PlayerData ret = data.get(uniqueId);

		if (ret == null) {
			// Try to grab from the SQL database
			Statement statement = null;

			try {
				statement = connection.createStatement();
				ResultSet results = statement.executeQuery("SELECT * FROM KitPvP_Players WHERE uniqueId='" + uniqueId + "';");
				if (results.next()) {
					ret = new PlayerData(results);
					data.put(uniqueId, ret);
				}
			} catch (SQLException ex) {
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data for " + player));
			} finally {
				Closer.closeQuietly(statement);
			}
		}

		if (player instanceof Player) {
			// Online players always have data
			if (ret == null) {
				ret = new PlayerData();
				data.put(uniqueId, ret);
			}

			// Update their name
			ret.setLastKnownBy(player.getName());
		}

		return ret;
	}

	public void save() {
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving players...");

		Statement statement = null;

		try {
			for (Entry<UUID, PlayerData> entry : data.entrySet()) {
				PlayerData data = entry.getValue();
				if (! data.shouldSave()) {
					continue;
				}

				statement = connection.createStatement();
				ResultSet results = statement.executeQuery("SELECT uniqueId FROM KitPvP_Players WHERE uniqueId='"
						+ entry.getKey() + "';");
				if (! results.next()) {
					statement.executeUpdate(String.format("INSERT INTO KitPvP_Players (uniqueId, kills, deaths, streak, rank, points, lastKnownBy)"
							+ " VALUES ('%s', %s, %s, %s, %s, %s, '%s');",
									entry.getKey(), data.getKills(), data.getDeaths(), data.getStreak(), data.getRank(), data.getPoints(), data.getLastKnownBy())
					);
				} else {
					statement.executeUpdate(String.format("UPDATE KitPvP_Players SET kills=%s, deaths=%s,"
							+ " streak=%s, rank=%s, points=%s, lastKnownBy='%s' WHERE uniqueId='%s'",
									data.getKills(), data.getDeaths(),  data.getStreak(), data.getRank(), data.getPoints(), data.getLastKnownBy(), entry.getKey())
					);
				}
			}
		} catch (SQLException ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving data"));
		} finally {
			Closer.closeQuietly(statement);
		}

		plugin.getLogHandler().log("Players saved! Took {0} ms.", System.currentTimeMillis() - start);
	}

	public void cleanup() {
		List<UUID> online = new ArrayList<>();
		for (Player player : Util.getOnlinePlayers()) {
			online.add(player.getUniqueId());
		}

		if (online.isEmpty()) {
			data.clear();
			return;
		}

		for (UUID uniqueId : data.keySet()) {
			if (!online.contains(uniqueId)) {
				data.remove(uniqueId);
			}
		}
	}
}