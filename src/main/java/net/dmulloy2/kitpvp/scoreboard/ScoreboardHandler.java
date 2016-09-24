/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.scoreboard;

import java.util.List;

import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.data.PlayerData;
import net.dmulloy2.types.CustomScoreboard;
import net.dmulloy2.types.CustomScoreboard.Builder;
import net.dmulloy2.types.CustomScoreboard.EntryFormat;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author dmulloy2
 */

public class ScoreboardHandler {
	private final KitPvP plugin;

	public ScoreboardHandler(KitPvP plugin) {
		this.plugin = plugin;
		this.updateAll();
	}

	public void updateAll() {
		for (Player player : Util.getOnlinePlayers()) {
			String world = player.getWorld().getName().toLowerCase();
			if (world.equals(Config.world)) {
				update(player);
			} else {
				unregister(player);
			}
		}
	}

	public void update(Player player) {
		Scoreboard board = player.getScoreboard();
		if (board == null || CustomScoreboard.isDefault(board)) {
			board = plugin.getServer().getScoreboardManager().getNewScoreboard();
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		Builder builder = CustomScoreboard.newBuilder(board, "KitPvP")
				.displayName(FormatUtil.format("&b&l> &f{0} &b&l<", player.getName()))
				.displaySlot(DisplaySlot.SIDEBAR)
				.entryFormat(EntryFormat.ON_LINE)
				.keyPrefix(FormatUtil.format("&e> &7"))
				.valuePrefix(FormatUtil.format(": &c"))
				.addEntry("Kills", data.getKills())
				.addEntry("Deaths", data.getDeaths())
				.addEntry("Streak", data.getStreak())
				.addEntry("&7===================");

		List<String> leaderboard = plugin.getLeaderboard().getLeaderboard();
		if (leaderboard != null) {
			builder.addEntry("&e> &7Top Ranking Players");
			for (int index = 0; index < 5 && index < leaderboard.size(); index++) {
				builder.addEntry(leaderboard.get(index));
			}

			builder.addEntry("&7===================&7")
				.addEntry("Your Ranking", data.getRank());	
		}

		builder.build().applyTo(player);
	}

	public void unregister(Player player) {
		Scoreboard board = player.getScoreboard();
		if (board != null) {
			Objective objective = board.getObjective("KitPvP");
			if (objective != null) {
				objective.unregister();
			}
		}
	}
}
