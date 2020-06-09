package dev.askingg.solar.managers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.askingg.solar.commands.SpawnerCommand;
import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Files;
import dev.askingg.solar.main.Solar;

public class Spawner {

	private Solar solar;
	private UUID placedBy;
	private Location location;
	private EntityType type;
	private int tier;

	public Spawner(Solar solar, UUID placedBy, Location location, EntityType type) {
		this.solar = solar;
		this.placedBy = placedBy;
		this.location = location;
		this.type = type;
		this.tier = 1;
	}

	public Spawner(Solar solar, UUID placedBy, Location location, EntityType type, int tier) {
		this.solar = solar;
		this.placedBy = placedBy;
		this.location = location;
		this.type = type;
		this.tier = tier;
	}

	public Location getLocation() {
		return location;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public EntityType getType() {
		return type;
	}

	public void upgrade() {
		tier++;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getTier() {
		return tier;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItem() {
		SpawnerCommand spawnerCmd = solar.spawnerCmd;
		ItemStack i = new ItemStack(Material.MOB_SPAWNER);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(Core.color(spawnerCmd.colors.get(type) + Core.capitalize(type.getName()) + " Spawner"));
		List<String> l = Arrays.asList(Core.color("&7"),
				Core.color("&7 •&f Tier " + spawnerCmd.colors.get(type) + tier));
		m.setLore(l);
		i.setItemMeta(m);
		return i;
	}

	public UUID getPlacer() {
		return placedBy;
	}

	public boolean isPlacer(UUID uuid) {
		return placedBy.equals(uuid);
	}

	@SuppressWarnings("deprecation")
	public void save() {
		String key = location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " "
				+ location.getZ();
		key = "spawners." + key.replace(".", ",") + ".";
		Files.data.set(key + "placedBy", placedBy.toString());
		Files.data.set(key + "type", type.getName());
		Files.data.set(key + "tier", tier);
	}
}
