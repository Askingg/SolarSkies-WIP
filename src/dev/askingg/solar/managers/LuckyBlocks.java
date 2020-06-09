package dev.askingg.solar.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;

public class LuckyBlocks {

	private Solar solar;

	public LuckyBlocks(Solar solar) {
		this.solar = solar;
	}

	public HashMap<Location, Integer> luckyBlocks = new HashMap<Location, Integer>();

	public int getLuckyChance(int tier) {
		if (tier == 1) {
			return 45;
		} else if (tier == 2) {
			return 50;
		} else if (tier == 3) {
			return 55;
		} else if (tier == 4) {
			return 60;
		} else if (tier == 5) {
			return 70;
		}
		return -1;
	}

	public int getUnluckyChance(int tier) {
		return 100 - getLuckyChance(tier);
	}

	public ItemStack luckyBlock(int tier) {
		ItemStack i = new ItemStack(Material.SPONGE);
		ItemMeta m = i.getItemMeta();
		List<String> l = new ArrayList<>();
		m.setDisplayName(Core.color("&6Lucky &eBlock &6T&e" + tier));
		l.add(Core.color("&7"));
		l.add(Core.color("&7 &7 When mined:"));
		l.add(Core.color("&7 •&f Lucky &a" + getLuckyChance(tier) + "%"));
		l.add(Core.color("&7 •&f Unlucky &c" + getUnluckyChance(tier) + "%"));
		m.setLore(l);
		i.setItemMeta(m);
		return i;
	}

	public boolean isLuckyBlock(ItemStack i) {
		if (i != null && i.getType() == Material.SPONGE && i.hasItemMeta()) {
			ItemMeta m = i.getItemMeta();
			if (m.hasDisplayName() && m.hasLore() && m.getDisplayName().contains(Core.color("&6Lucky &eBlock &6T&e"))
					&& m.getLore().get(1).equals(Core.color("&7 &7 When mined:"))) {
				return true;
			}
		}
		return false;
	}

	public int getTier(ItemStack lb) {
		return Integer.valueOf(lb.getItemMeta().getDisplayName().split(Core.color("&6Lucky &eBlock &6T&e"))[1]);
	}
}
