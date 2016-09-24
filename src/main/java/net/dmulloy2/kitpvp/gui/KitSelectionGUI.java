/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.gui;

import net.dmulloy2.gui.AbstractGUI;
import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.gui.KitConfirmGUI.Action;
import net.dmulloy2.kitpvp.kits.Kit;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class KitSelectionGUI extends AbstractGUI {
	private final KitPvP plugin;

	public KitSelectionGUI(KitPvP plugin, Player player) {
		super(plugin, player);
		this.plugin = plugin;
		this.setup();
	}

	@Override
	public int getSize() {
		int size = Config.kitSelectionSize;
		return size > 0 ? size : NumberUtil.roundUp(plugin.getKitHandler().getKits().size(), 9);
	}

	@Override
	public String getTitle() {
		return FormatUtil.format(Config.kitSelectionTitle);
	}

	@Override
	public void stock(Inventory inventory) {
		for (Kit kit : plugin.getKitHandler().getKits()) {
			int slot = kit.getSlot();
			if (slot != -1) {
				ItemStack icon = kit.getIcon(player);
				if (icon != null) {
					inventory.setItem(slot, icon);
				}
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack clicked = event.getCurrentItem();
		if (clicked != null) {
			Kit kit = plugin.getKitHandler().getKit(clicked, player);
			if (kit != null) {
				event.setCancelled(true);

				Action action = null;
				if (kit.hasPermission(player)) {
					action = Action.GIVE;
				} else if (kit.getCost() > 0) {
					action = Action.PURCHASE;
				} else {
					action = Action.PREVIEW;
				}

				player.closeInventory();

				KitConfirmGUI kcGUI = new KitConfirmGUI(plugin, player, kit, action);
				plugin.getGuiHandler().open(kcGUI);
			}
		}
	}
}
