package dev.askingg.solar.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.Island;
import dev.askingg.solar.managers.LuckyBlock;
import dev.askingg.solar.managers.Spawner;
import dev.askingg.solar.managers.User;

public class BlockBreak implements Listener {

	private Solar solar;

	public BlockBreak(Solar solar) {
		this.solar = solar;
		solar.getServer().getPluginManager().registerEvents(this, solar);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		Block b = e.getBlock();
		Location l = b.getLocation();
		World w = l.getWorld();
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
		if (isIsland || solar.wg.canBuild(p, b)) {
			if (b.getType() == Material.MOB_SPAWNER && solar.spawners.containsKey(l)) {
				Spawner spawner = solar.spawners.get(l);
				if (spawner.isPlacer(u)) {
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
					if (p.hasPermission("solar.donor")) {
						if (p.getInventory().firstEmpty() != -1) {
							p.getInventory().addItem(spawner.getItem());
							Core.message(solar.prefix+"You broke your " + solar.spawnerCmd.colors.get(spawner.getType())+Core.capitalize(spawner.getType().getName())+" Spawner&f.&7 (Added to your inventory.)", p);
						} else {
							p.getWorld().dropItem(p.getEyeLocation(), spawner.getItem());
							Core.message(solar.prefix+"You broke your " + solar.spawnerCmd.colors.get(spawner.getType())+Core.capitalize(spawner.getType().getName())+" Spawner&f.&7 (Dropped at your location.)", p);
						}
					} else {
						p.getWorld().dropItem(b.getLocation(), spawner.getItem());
						Core.message(solar.prefix+"You broke your " + solar.spawnerCmd.colors.get(spawner.getType())+Core.capitalize(spawner.getType().getName())+" Spawner&f.&7 (Dropped on the floor.)", p);
					}
					solar.spawners.remove(l);
				} else {
					e.setCancelled(true);
					Core.message(solar.prefix + "Sorry, but you cannot break &c"
							+ Bukkit.getOfflinePlayer(spawner.getPlacer()).getName() + "'s "
							+ solar.spawnerCmd.colors.get(spawner.getType())
							+ Core.capitalize(spawner.getType().getName()) + " Spawner&f.", p);
				}
			}
			if (b.getType() == Material.SPONGE && solar.luckyblocks.containsKey(l)) {
				LuckyBlock lb = solar.luckyblocks.get(l);
				e.getBlock().setType(Material.AIR);
				lb.open(p);
				solar.luckyblocks.remove(l);
			}
		}
	}
}
