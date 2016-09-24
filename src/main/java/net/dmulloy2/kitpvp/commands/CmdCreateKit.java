/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.kitpvp.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.kits.Kit;
import net.dmulloy2.kitpvp.type.Permission;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */
public class CmdCreateKit extends KitPvPCommand {

	public CmdCreateKit(KitPvP plugin) {
		super(plugin);
		this.name = "createkit";
		this.addRequiredArg("name");
		this.addOptionalArg("slot");
		this.addOptionalArg("display");
		this.permission = Permission.CREATE_KIT;
		this.description = "Creates a new kit from your inventory";
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		List<ItemStack> items = new ArrayList<>();
		for (ItemStack item : player.getInventory().getStorageContents()) {
			if (item != null && item.getType() != Material.AIR) {
				items.add(item);
			}
		}

		checkArgument(items.size() > 0, "Your inventory is empty!");

		List<ItemStack> armor = new ArrayList<>();
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (item != null && item.getType() != Material.AIR) {
				armor.add(item);
			}
		}

		String name = args[0];
		int slot = -1;
		String display = null;

		if (args.length == 2) {
			slot = argAsInt(1, false);
			if (slot == -1) {
				display = args[1];
			}
		} else if (args.length >= 3) {
			slot = argAsInt(1, true);
			display = getFinalArg(2);
		}

		Kit kit = new Kit(plugin, name, display, items, armor);
		plugin.getKitHandler().addKit(kit);
	}
}