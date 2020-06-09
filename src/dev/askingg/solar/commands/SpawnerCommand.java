package dev.askingg.solar.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;

public class SpawnerCommand implements CommandExecutor {

	private Solar solar;
	public List<EntityType> types = new ArrayList<>();;
	public HashMap<EntityType, String> colors = new HashMap<>();;
	public HashMap<EntityType, Integer> maxTiers = new HashMap<>();

	public void addType(EntityType type, String color, int maxTier) {
		types.add(type);
		colors.put(type, color);
		maxTiers.put(type, maxTier);
	}

	public SpawnerCommand(Solar solar) {
		this.solar = solar;
		solar.getCommand("spawners").setExecutor(this);
		addType(EntityType.PIG, "&d", 5);
		addType(EntityType.COW, "&7", 5);
		addType(EntityType.SHEEP, "&f", 5);
		addType(EntityType.ZOMBIE, "&a", 5);
		addType(EntityType.SKELETON, "&f", 5);
		addType(EntityType.WITHER_SKELETON, "&8", 5);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("types")) {
				String msg = "";
				for (EntityType type : types) {
					if (msg.equals("")) {
						msg += solar.prefix + "&fSpawner Types&3 &l»" + colors.get(type) + " "
								+ Core.capitalize(type.getName());
					} else {
						msg += "&f, " + colors.get(type) + Core.capitalize(type.getName());
					}
				}
				Core.message(msg, s);
				return true;
			}
			if (args[0].equalsIgnoreCase("give")) { // Spawners Give <Player> <Type> <Tier>
				if (s instanceof ConsoleCommandSender || s.hasPermission("solar.spawners.give")) {
					if (args.length > 2) {
						Player p = Bukkit.getPlayer(args[1]);
						if (p != null) {
							EntityType type = EntityType.fromName(args[2].toUpperCase());
							if (type != null) {
								int tier = 1;
								if (args.length > 3) {
									try {
										tier = Integer.parseInt(args[3]);
									} catch (Exception ex) {
										Core.message(
												solar.prefix + "Sorry, but &c" + args[3] + "&f is an invalid integer.",
												s);
										return true;
									}
									if (tier < 1)
										tier = 1;
									if (tier > maxTiers.get(type))
										tier = maxTiers.get(type);
								}
								ItemStack i = new ItemStack(Material.MOB_SPAWNER);
								ItemMeta m = i.getItemMeta();
								m.setDisplayName(Core.color(colors.get(type) + Core.capitalize(type.getName()) + " Spawner"));
								List<String> l = Arrays.asList(Core.color("&7"), Core.color("&7 •&f Tier " + colors.get(type)+tier));
								m.setLore(l);
								i.setItemMeta(m);
								if (p.getInventory().firstEmpty()==-1) {
									p.getWorld().dropItem(p.getEyeLocation(), i);
									Core.message(solar.prefix+"You received a " + colors.get(type)+Core.capitalize(type.getName())+" Spawner&f.&7 (Dropped on the floor.)", s);
								} else {
									p.getInventory().addItem(i);
									p.updateInventory();
									Core.message(solar.prefix+"You received a " + colors.get(type)+Core.capitalize(type.getName())+" Spawner&f.", s);
								}
								Core.message(solar.prefix+"You gave a " + colors.get(type)+Core.capitalize(type.getName())+" Spawner&f to &b" + p.getName(), s);
							} else {
								Core.message(solar.prefix + "Sorry, but &c" + args[2] + "&f is an invalid EntityType.",
										s);
							}
						} else {
							Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.", s);
						}
					} else {
						Core.message(solar.prefix + "Usage&3 &l»&e /Spawners Give <Player> <Type> (Tier)", s);
					}
				} else {
					Core.message(solar.prefix + "Sorry, but you don't have permission to do that.", s);
				}
				return true;
			}
		}
		Core.message(solar.prefix + "Commands for &6Spawners&3 &l»", s);
		Core.message("&3 -&e /Spawners Help&3 &l»&f View the help list.", s);
		Core.message("&3 -&e /Spawners List&3 &l»&f View all spawner types.", s);
		Core.message("&3 -&e /Spawners Give&3 &l»&f Give a player spawner(s).", s);
		return true;
	}

}
