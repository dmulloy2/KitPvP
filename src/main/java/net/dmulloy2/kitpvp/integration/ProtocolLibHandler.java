/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.kitpvp.integration;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.integration.TypelessProvider;
import net.dmulloy2.kitpvp.packets.WrapperPlayClientClientCommand;

import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.EnumWrappers.ClientCommand;

/**
 * @author dmulloy2
 */
public class ProtocolLibHandler extends TypelessProvider {

	public ProtocolLibHandler(SwornPlugin handler) {
		super(handler, "ProtocolLib");
	}

	public void forceRespawn(Player player) {
		WrapperPlayClientClientCommand packet = new WrapperPlayClientClientCommand();
		packet.setAction(ClientCommand.PERFORM_RESPAWN);
		packet.receivePacket(player);
	}
}