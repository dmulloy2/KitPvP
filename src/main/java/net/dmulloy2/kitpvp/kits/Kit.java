/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.kitpvp.kits;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.integration.VaultHandler;
import net.dmulloy2.kitpvp.Config;
import net.dmulloy2.kitpvp.KitPvP;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.ItemUtil;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Data;

/**
 * @author dmulloy2
 */

@Data
public class Kit implements ConfigurationSerializable {
	private String name;
	private int slot = -1;

	private String display;
	private List<String> lore;
	private ItemStack icon;

	private List<ItemStack> items;
	private List<ItemStack> armor;

	private double cost = -1.0D;
	private String rank;

	private transient final KitPvP plugin;

	public Kit(KitPvP plugin, String name, String display, List<ItemStack> items, List<ItemStack> armor) {
		this.plugin = plugin;
		this.name = name;
		this.display = display;
		this.items = items;
		this.armor = armor;
	}

	public Kit(KitPvP plugin, MemorySection section) {
		this.plugin = plugin;
		this.name = section.getName().toLowerCase();
		this.slot = section.getInt("slot", -1);

		if (section.isSet("display")) {
			this.display = section.getString("display");
		}

		if (section.isSet("lore")) {
			this.lore = section.getStringList("lore");
		}

		if (section.isSet("icon")) {
			this.icon = ItemUtil.readItem(section.getString("icon"));
		}

		this.items = deserialize(section.getStringList("items"));

		if (section.isSet("armor")) {
			this.armor = deserialize(section.getStringList("armor"));
		}

		this.cost = section.getDouble("cost");

		if (section.isSet("rank")) {
			this.rank = section.getString("rank");
		}
	}

	public ItemStack getIcon(Player player) {
		if (icon == null) {
			return null;
		}

		ItemStack icon = this.icon.clone();
		ItemMeta meta = icon.getItemMeta();

		if (display != null) {
			meta.setDisplayName(FormatUtil.format(display));
		}

		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
		if (this.lore != null) {
			for (String line : this.lore) {
				lore.add(FormatUtil.format(line));
			}
		}

		if (! hasPermission(player)) {
			if (cost > 0) {
				VaultHandler vault = plugin.getVaultHandler();
				if (vault != null && vault.isEconPresent()) {
					if (vault.has(player, cost)) {
						lore.add(FormatUtil.format("&c&lLocked! Costs &a&l{0}&c&l!", vault.format(cost)));
					} else {
						lore.add(FormatUtil.format("&c&lLocked! Costs {0}!", vault.format(cost)));
					}
				} else {
					lore.add(FormatUtil.format("&c&lLocked! Error: Missing economy!"));
				}
			} else if (rank != null) {
				lore.add(FormatUtil.format("&c&lLocked! Comes with {0}!", rank));
			} else {
				lore.add(FormatUtil.format("&c&lLocked!"));
			}
		} else if (cost < 0 && rank == null) {
			lore.add(FormatUtil.format("&a&lFree!"));
		}

		meta.setLore(lore);
		icon.setItemMeta(meta);
		return icon;
	}

	public boolean hasPermission(Player player) {
		return player.isOp() || player.hasPermission(getPermission());
	}

	public String getPermission() {
		return Config.permissionPrefix + name.toLowerCase();
	}

	public void giveItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		InventoryUtil.clear(inventory);

		inventory.setContents(items.toArray(new ItemStack[items.size()]));

		if (armor != null) {
			for (ItemStack item : armor) {
				Material material = item.getType();
				if (isHelmet(material)) {
					inventory.setHelmet(item);
				} else if (isChestplate(material)) {
					inventory.setChestplate(item);
				} else if (isLeggings(material)) {
					inventory.setLeggings(item);
				} else if (isBoots(material)) {
					inventory.setBoots(item);
				}
			}
		}
	}

	private static boolean isHelmet(Material material) {
		switch (material) {
			case LEATHER_HELMET:
			case GOLD_HELMET:
			case IRON_HELMET:
			case DIAMOND_HELMET:
			case CHAINMAIL_HELMET:
				return true;
			default:
				return false;
		}
	}

	private static boolean isChestplate(Material material) {
		switch (material) {
			case LEATHER_CHESTPLATE:
			case GOLD_CHESTPLATE:
			case IRON_CHESTPLATE:
			case DIAMOND_CHESTPLATE:
			case CHAINMAIL_CHESTPLATE:
			default:
				return false;
		}
	}

	private static boolean isLeggings(Material material) {
		switch (material) {
			case LEATHER_LEGGINGS:
			case GOLD_LEGGINGS:
			case IRON_LEGGINGS:
			case DIAMOND_LEGGINGS:
			case CHAINMAIL_LEGGINGS:
				return true;
			default:
				return false;
		}
	}

	private static boolean isBoots(Material material) {
		switch (material) {
			case LEATHER_BOOTS:
			case GOLD_BOOTS:
			case IRON_BOOTS:
			case DIAMOND_BOOTS:
			case CHAINMAIL_BOOTS:
				return true;
			default:
				return false;
		}
	}

	public int getTotalSize() {
		int size = items.size();
		if (armor != null) {
			size += armor.size();
		}

		return size;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new LinkedHashMap<>();

		data.put("slot", slot);

		if (display != null) {
			data.put("display", display);
		}

		if (lore != null) {
			data.put("lore", lore);
		}

		if (icon != null) {
			data.put("icon", ItemUtil.serialize(icon));
		}

		data.put("items", serialize(items));

		if (armor != null) {
			data.put("armor", serialize(armor));
		}

		if (rank != null) {
			data.put("rank", rank);
		}

		data.put("cost", cost);
		return data;
	}

	private List<String> serialize(List<ItemStack> items) {
		List<String> ret = new ArrayList<>();
		for (ItemStack item : items) {
			if (item != null) {
				ret.add(ItemUtil.serialize(item));
			} else {
				ret.add(null);
			}
		}

		return ret;
	}

	private List<ItemStack> deserialize(List<String> items) {
		List<ItemStack> ret = new ArrayList<>();
		for (String item : items) {
			ItemStack stack = ItemUtil.readItem(item);
			if (stack != null) {
				ret.add(stack);
			} else {
				ret.add(null);
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}