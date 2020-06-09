package dev.askingg.solar.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.User;

public class PlayerInteract implements Listener {

	private Solar solar;
	private List<Material> cancels;

	public PlayerInteract(Solar solar) {
		this.solar = solar;
		this.cancels = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.FURNACE,
				Material.BREWING_STAND, Material.ARMOR_STAND, Material.ITEM_FRAME);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		if (cancels.contains(b.getType())) {
			Player p = e.getPlayer();
			User user = solar.users.get(p.getUniqueId());
			if (solar.wg.canBuild(p, b) || (user.hasIsland() && user.getIsland().isInRegion(b.getLocation()))) {
				
			}
		}
	}
}
