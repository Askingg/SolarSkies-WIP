package dev.askingg.solar.events;

import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import dev.askingg.solar.main.Solar;

public class SpawnerSpawn implements Listener {

	private Solar solar;

	public SpawnerSpawn(Solar solar) {
		this.solar = solar;
		solar.getServer().getPluginManager().registerEvents(this, solar);
	}

	@EventHandler
	public void onSpawn(SpawnerSpawnEvent e) {
		CreatureSpawner cSpawner = e.getSpawner();
		Location l = cSpawner.getLocation();
		if (solar.spawners.containsKey(l)) {
//			Spawner spawner = solar.spawners.get(l);
		}
	}
}
