/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.commands;

import java.util.List;

import net.dmulloy2.kitpvp.KitPvP;

/**
 * @author dmulloy2
 */

public class CmdTopKills extends KitPvPCommand {

	public CmdTopKills(KitPvP plugin) {
		super(plugin);
		this.name = "topkills";
		this.description = "Top 10 players with the most kills";
	}

	@Override
	public void perform() {
		sendMessage("&7---- &bTop 10 Kills &7----");

		List<String> leaderboard = plugin.getLeaderboard().getLeaderboard();
		for (int i = 0; i < 10 && i < leaderboard.size(); i++) {
			sendMessage(leaderboard.get(i));
		}
	}
}
