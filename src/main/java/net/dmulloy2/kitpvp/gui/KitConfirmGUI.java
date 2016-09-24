/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.gui;

import net.dmulloy2.gui.AbstractGUI;
import net.dmulloy2.integration.VaultHandler;
import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.kitpvp.kits.Kit;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.NumberUtil;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

/**
 * @author dmulloy2
 */

public class KitConfirmGUI extends AbstractGUI {
	private final KitPvP plugin;

	public static enum Action {
		GIVE, PREVIEW, PURCHASE, ;
	}

	private final Action action;
	private final Kit kit;

	public KitConfirmGUI(KitPvP plugin, Player player, Kit kit, Action action) {
		super(plugin, player);
		this.plugin = plugin;
		this.kit = kit;
		this.action = action;
		this.setup();
	}

	@Override
	public int getSize() {
		return NumberUtil.roundUp(kit.getTotalSize(), 9) + 9;
	}

	@Override
	public String getTitle() {
		return FormatUtil.format(Config.kitConfirmTitle.replace("%k", kit.getDisplay()));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void stock(Inventory inventory) {
		for (ItemStack item : kit.getItems()) {
			inventory.addItem(item);
		}

		if (kit.getArmor() != null) {
			for (ItemStack armor : kit.getArmor()) {
				inventory.addItem(armor);
			}
		}

		int index = getSize() - 7;

		ItemStack back = new ItemStack(Material.WOOL, 1, DyeColor.RED.getWoolData());

		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(FormatUtil.format("&4&lBack to Kits"));
		back.setItemMeta(meta);

		inventory.setItem(index, back);

		ItemStack choose = new ItemStack(Material.WOOL);
		meta = choose.getItemMeta();
		meta.setDisplayName(FormatUtil.format("&lPlease Choose"));

		inventory.setItem(index += 2, choose);

		ItemStack send = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getWoolData());
		meta = send.getItemMeta();

		switch (action) {
			case GIVE:
				meta.setDisplayName(FormatUtil.format("&2&lSelect This Kit"));
				break;
			case PURCHASE:
				VaultHandler vault = plugin.getVaultHandler();
				if (vault != null && vault.isEnabled()) {
					meta.setDisplayName(FormatUtil.format("&2&lPurchase This Kit For {0}", vault.format(kit.getCost())));
				} else {
					meta.setDisplayName(FormatUtil.format("&4&lError: Economy is missing!"));
				}
				break;
			case PREVIEW:
				meta.setDisplayName(FormatUtil.format("&2&lLocked"));
				break;
			default:
				throw new IllegalArgumentException("Unknown action " + action);
		}

		send.setItemMeta(meta);

		inventory.setItem(index += 2, send);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack clicked = event.getCurrentItem();
		if (clicked != null) {
			event.setCancelled(true);
			if (clicked.getType() == Material.WOOL) {
				Wool wool = (Wool) clicked.getData();
				if (wool.getColor() == DyeColor.RED) {
					player.closeInventory();

					KitSelectionGUI ksGUI = new KitSelectionGUI(plugin, player);
					plugin.getGuiHandler().open(ksGUI);
				} else if (wool.getColor() == DyeColor.LIME) {
					switch (action) {
						case GIVE:
							player.closeInventory();
							kit.giveItems(player);
							sendpMessage("&eYou have received kit &b{0}&e.", kit.getName());
							break;
						case PURCHASE:
							VaultHandler vault = plugin.getVaultHandler();
							if (vault == null || !vault.isEnabled()) {
								sendpMessage("&cCannot purchase: Vault is missing!");
								break;
							}

							if (vault.has(player, kit.getCost())) {
								player.closeInventory();
								String response = vault.withdrawPlayer(player, kit.getCost());
								if (response == null) {
									vault.addPermission(player, kit.getPermission());
									kit.giveItems(player);
									sendpMessage("&eYou have purchased kit &b{0} &efor &b{1}&e.", kit.getName(),
											vault.format(kit.getCost()));
								} else {
									sendpMessage("&cFailed to purchase kit: {0}", response);
								}
							} else {
								sendpMessage("&cYou lack the funds to buy this kit!");
							}

							break;
						case PREVIEW:
							break;
						default:
							throw new IllegalArgumentException("Unknown action " + action);
					}
				}
			}
		}
	}
}
