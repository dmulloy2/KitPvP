/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.commands;

import java.util.List;

import net.dmulloy2.kitpvp.KitPvP;

/**
 * @author dmulloy2
 */

public class CmdTopKilled extends KitPvPCommand {

	public CmdTopKilled(KitPvP plugin) {
		super(plugin);
		this.name = "topkilled";
		this.description = "Top 10 players with the most deaths";
	}

	@Override
	public void perform() {
		sendMessage("&7---- &bTop 10 Deaths &7----");

		List<String> loserboard = plugin.getLeaderboard().getLoserboard();
		for (int i = 0; i < 10 && i < loserboard.size(); i++) {
			sendMessage(loserboard.get(i));
		}
	}
}
