/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.commands;

import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.type.Permission;

/**
 * @author dmulloy2
 */

public class CmdSetLobby extends KitPvPCommand {

	public CmdSetLobby(KitPvP plugin) {
		super(plugin);
		this.name = "setlobby";
		this.description = "Set the lobby";
		this.permission = Permission.SET_LOBBY;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		plugin.getDataHandler().setLobby(player.getLocation());
		sendpMessage("&eLobby set to your location.");
	}
}
