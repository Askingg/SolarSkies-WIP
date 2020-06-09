package dev.askingg.solar.managers;

import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Files;
import dev.askingg.solar.main.Solar;

public class LuckyBlock {

	private Solar solar;
	private LuckyBlocks lbs;

	private int tier;
	private UUID placedBy;

	public LuckyBlock(Solar solar, int tier, UUID placedBy) {
		this.solar = solar;
		this.lbs = solar.lbs;
		this.tier = tier;
		this.placedBy = placedBy;
	}

	public boolean wasPlacedBy(UUID uuid) {
		return placedBy.equals(uuid);
	}

	public int getTier() {
		return this.tier;
	}

	public UUID getPlacedBy() {
		return this.placedBy;
	}

	public void open(Player p) {
		Random r = new Random();
		if ((r.nextInt(100) + 1) <= lbs.getLuckyChance(tier)) {
			Core.message(solar.prefix + "&aLucky.&7 (" + tier + ")", p);
		} else {
			Core.message(solar.prefix + "&cUnlucky.&7 (" + tier + ")", p);
		}
	}
}
