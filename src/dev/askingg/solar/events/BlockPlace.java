package dev.askingg.solar.events;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.askingg.solar.commands.SpawnerCommand;
import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.Island;
import dev.askingg.solar.managers.LuckyBlock;
import dev.askingg.solar.managers.Spawner;
import dev.askingg.solar.managers.User;

public class BlockPlace implements Listener {

	private Solar solar;

	public BlockPlace(Solar solar) {
		this.solar = solar;
		solar.getServer().getPluginManager().registerEvents(this, solar);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		World w = e.getBlock().getWorld();
		UUID u = e.getPlayer().getUniqueId();
		Location l = e.getBlock().getLocation();
		boolean isIsland = false;
		if (w.getName().equals("islands")) {
			User user = solar.users.get(u);
			if (user.hasIsland()) {
				for (Island island : solar.islands) {
					if (island.isLeader(u) || island.isMember(u) || island.isTrusted(u)) {
						if (island.isInRegion(l)) {
							isIsland = true;
							break;
						}
					}
				}
			}
			if (!isIsland) {
				e.setCancelled(true);
				return;
			}
		}
		if (isIsland || solar.wg.canBuild(e.getPlayer(), e.getBlock())) {
			ItemStack i = e.getItemInHand();
			if (i.getType() == Material.MOB_SPAWNER && i.hasItemMeta()) {
				ItemMeta m = i.getItemMeta();
				if (m.hasDisplayName() && m.hasLore()) {
					SpawnerCommand spawnerCmd = solar.spawnerCmd;
					for (EntityType type : spawnerCmd.types) {
						if (m.getDisplayName().equals(
								Core.color(spawnerCmd.colors.get(type) + Core.capitalize(type.getName()) + " Spawner"))
								&& m.getLore().size() == 2) {
							int tier = Integer.valueOf(ChatColor.stripColor(m.getLore().get(1).split(" Tier ")[1]));
							solar.spawners.put(l, new Spawner(solar, e.getPlayer().getUniqueId(), l, type, tier));
							Core.message(solar.prefix + "You placed a " + solar.spawnerCmd.colors.get(type)
									+ Core.capitalize(type.getName()) + " Spawner&f.", e.getPlayer());
							CreatureSpawner cSpawner = (CreatureSpawner) e.getBlock().getState();
							cSpawner.setCreatureTypeByName(type.getName());
							if (tier == 1) {
								cSpawner.setMaxSpawnDelay(1000);
								cSpawner.setMinSpawnDelay(250);
								cSpawner.setSpawnCount(3);
								cSpawner.setMaxNearbyEntities(14);
								cSpawner.setSpawnRange(4);
								cSpawner.setRequiredPlayerRange(16);
							} else if (tier == 2) {
								cSpawner.setMaxSpawnDelay(900);
								cSpawner.setMinSpawnDelay(225);
								cSpawner.setSpawnCount(4);
								cSpawner.setMaxNearbyEntities(16);
								cSpawner.setSpawnRange(4);
								cSpawner.setRequiredPlayerRange(20);
							} else if (tier == 3) {
								cSpawner.setMaxSpawnDelay(800);
								cSpawner.setMinSpawnDelay(200);
								cSpawner.setSpawnCount(5);
								cSpawner.setMaxNearbyEntities(20);
								cSpawner.setSpawnRange(5);
								cSpawner.setRequiredPlayerRange(24);
							} else if (tier == 4) {
								cSpawner.setMaxSpawnDelay(700);
								cSpawner.setMinSpawnDelay(175);
								cSpawner.setSpawnCount(5);
								cSpawner.setMaxNearbyEntities(25);
								cSpawner.setSpawnRange(4);
								cSpawner.setRequiredPlayerRange(28);
							} else if (tier == 5) {
								cSpawner.setMaxSpawnDelay(600);
								cSpawner.setMinSpawnDelay(150);
								cSpawner.setSpawnCount(7);
								cSpawner.setMaxNearbyEntities(30);
								cSpawner.setSpawnRange(6);
								cSpawner.setRequiredPlayerRange(32);
							}
							cSpawner.update();
							break;
						}
					}
				}
			}
			if (solar.lbs.isLuckyBlock(i)) {
				solar.luckyblocks.put(l, new LuckyBlock(solar, solar.lbs.getTier(i), u));
			}
		}
	}
}
