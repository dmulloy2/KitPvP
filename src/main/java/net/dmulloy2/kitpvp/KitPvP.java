/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp;

import java.util.logging.Level;

import net.dmulloy2.SwornAPI;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.commands.CmdReload;
import net.dmulloy2.gui.GUIHandler;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.integration.VaultHandler;
import net.dmulloy2.kitpvp.commands.CmdCreateKit;
import net.dmulloy2.kitpvp.commands.CmdSetLobby;
import net.dmulloy2.kitpvp.commands.CmdTopKilled;
import net.dmulloy2.kitpvp.commands.CmdTopKills;
import net.dmulloy2.kitpvp.data.DataHandler;
import net.dmulloy2.kitpvp.data.PlayerDataCache;
import net.dmulloy2.kitpvp.integration.ProtocolLibHandler;
import net.dmulloy2.kitpvp.integration.WorldEditHandler;
import net.dmulloy2.kitpvp.integration.WorldGuardHandler;
import net.dmulloy2.kitpvp.kits.KitHandler;
import net.dmulloy2.kitpvp.listeners.EntityListener;
import net.dmulloy2.kitpvp.listeners.PlayerListener;
import net.dmulloy2.kitpvp.scoreboard.Leaderboard;
import net.dmulloy2.kitpvp.scoreboard.ScoreboardHandler;
import net.dmulloy2.kitpvp.type.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.plugin.PluginManager;

import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
public class KitPvP extends SwornPlugin {
	private ScoreboardHandler scoreboardHandler;
	private PlayerDataCache playerDataCache;
	private DataHandler dataHandler;
	private KitHandler kitHandler;

	private Leaderboard leaderboard;
	private GUIHandler guiHandler;

	private ProtocolLibHandler protocolLibHandler;
	private WorldGuardHandler worldGuardHandler;
	private WorldEditHandler worldEditHandler;
	private VaultHandler vaultHandler;

	private String prefix;

	@Override
	public void onLoad() {
		SwornAPI.checkRegistrations();
	}

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();

		logHandler = new LogHandler(this);

		saveDefaultConfig();
		reloadConfig();

		prefix = Config.prefix;

		permissionHandler = new PermissionHandler(this);
		commandHandler = new CommandHandler(this);

		getCommandProps().setReloadPerm(Permission.RELOAD);

		PluginManager pm = getServer().getPluginManager();

		try {
			playerDataCache = new PlayerDataCache(this);
		} catch (Throwable ex) {
			logHandler.log(Level.SEVERE, Util.getUsefulStack(ex, "connecting to SQLite"));
			pm.disablePlugin(this);
			return;
		}

		leaderboard = new Leaderboard(this);

		scoreboardHandler = new ScoreboardHandler(this);
		dataHandler = new DataHandler(this);
		kitHandler = new KitHandler(this);
		guiHandler = new GUIHandler(this);

		commandHandler.setCommandPrefix("kitpvp");
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));

		commandHandler.registerPrefixedCommand(new CmdCreateKit(this));
		commandHandler.registerPrefixedCommand(new CmdSetLobby(this));
		commandHandler.registerPrefixedCommand(new CmdTopKilled(this));
		commandHandler.registerPrefixedCommand(new CmdTopKills(this));

		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);

		setupIntegration();

		logHandler.log("{0} has been enabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable() {
		long start = System.currentTimeMillis();

		playerDataCache.save();
		dataHandler.save();
		kitHandler.save();

		leaderboard.saveCache();

		logHandler.log("{0} has been disabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		Config.load(this);
	}

	@Override
	public void reload() {
		reloadConfig();
		prefix = Config.prefix;
		kitHandler.reload();
	}

	private void setupIntegration() {
		try {
			protocolLibHandler = new ProtocolLibHandler(this);
		} catch (Throwable ex) { }

		try {
			worldEditHandler = new WorldEditHandler(this);
		} catch (Throwable ex) { }

		try {
			worldGuardHandler = new WorldGuardHandler(this);
		} catch (Throwable ex) { }

		try {
			vaultHandler = new VaultHandler(this);
		} catch (Throwable ex) { }
	}

	public boolean isProtocolLibEnabled() {
		return protocolLibHandler != null && protocolLibHandler.isEnabled();
	}

	public boolean isWorldEditEnabled() {
		return worldEditHandler != null && worldEditHandler.isEnabled();
	}

	public boolean isWorldGuardEnabled() {
		return worldGuardHandler != null && worldGuardHandler.isEnabled();
	}

	public boolean isVaultEnabled() {
		return vaultHandler != null && vaultHandler.isEnabled();
	}
}
