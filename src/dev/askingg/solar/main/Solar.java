package dev.askingg.solar.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import dev.askingg.solar.commands.IslandCommand;
import dev.askingg.solar.commands.LuckyBlocksCommand;
import dev.askingg.solar.commands.ShopCommand;
import dev.askingg.solar.commands.SolarCoinCommand;
import dev.askingg.solar.commands.SpawnerCommand;
import dev.askingg.solar.events.BlockBreak;
import dev.askingg.solar.events.BlockPlace;
import dev.askingg.solar.events.JoinLeave;
import dev.askingg.solar.events.PlayerChat;
import dev.askingg.solar.events.SpawnerSpawn;
import dev.askingg.solar.gui.IslandGUI;
import dev.askingg.solar.gui.ShopGUI;
import dev.askingg.solar.managers.Island;
import dev.askingg.solar.managers.LuckyBlock;
import dev.askingg.solar.managers.LuckyBlocks;
import dev.askingg.solar.managers.Shop;
import dev.askingg.solar.managers.Spawner;
import dev.askingg.solar.managers.User;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Solar extends JavaPlugin {

	public Solar instance;
	public String prefix = "&b&lSolar&f&lSkies&b &l>>&f ";
	public HashMap<UUID, User> users = new HashMap<>();
	public List<Island> islands = new ArrayList<>();
	public int index = 0;
	public HashMap<String, Shop> shops = new HashMap<>();
	public Economy eco = null;
	public Permission perms = null;
	public HashMap<Location, Spawner> spawners = new HashMap<>();;
	public SpawnerCommand spawnerCmd;
	public WorldGuardPlugin wg;
	public PlayerChat playerChat;
	public HashMap<String, Double> value = new HashMap<>();
	public LuckyBlocks lbs;
	public HashMap<Location, LuckyBlock> luckyblocks = new HashMap<>();

	public List<Island> getIslands() {
		return islands;
	}

	@SuppressWarnings("deprecation")
	public void onEnable() {
		instance = this;
		new Files(this);
		Files.base();
		eco = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		wg = WorldGuardPlugin.inst();

		World world = Bukkit.getWorld("islands");
		if (world == null) {
			WorldCreator creator = new WorldCreator("islands");
			creator.generator(new ChunkGenerator() {
				@Override
				public byte[] generate(World world, Random random, int x, int z) {
					return new byte[32768];
				}
			});
			world = creator.createWorld();
		}
		new IslandCommand(this);
		new JoinLeave(this);
		new BlockBreak(this);
		new IslandGUI(this);
		new ShopCommand(this);
		new ShopGUI(this);
		new SpawnerSpawn(this);
		spawnerCmd = new SpawnerCommand(this);
		new BlockPlace(this);
		new SolarCoinCommand(this);
		playerChat = new PlayerChat(this);
		lbs = new LuckyBlocks(this);
		new LuckyBlocksCommand(this);

		loadShops();
		playerChat.loadConfig();

		ConfigurationSection islands = Files.data.getConfigurationSection("islands");
		if (islands != null)
			for (String leaderString : islands.getKeys(false)) {
				ConfigurationSection island = islands.getConfigurationSection(leaderString);
				UUID leader = UUID.fromString(leaderString);
				List<UUID> members = new ArrayList<>();
				for (String uuidString : islands.getStringList(leaderString + ".members")) {
					members.add(UUID.fromString(uuidString));
				}
				List<UUID> trusted = new ArrayList<>();
				for (String uuidString : islands.getStringList(leaderString + ".trusted")) {
					trusted.add(UUID.fromString(uuidString));
				}
				List<UUID> denied = new ArrayList<>();
				for (String uuidString : islands.getStringList(leaderString + ".denied")) {
					denied.add(UUID.fromString(uuidString));
				}
				HashMap<UUID, Long> joinTime = new HashMap<>();
				for (String member : island.getConfigurationSection("joinTime").getKeys(false)) {
					joinTime.put(UUID.fromString(member), island.getLong("joinTime." + member));
				}
				this.islands.add(new Island(this, leader, new Location(Bukkit.getWorld("islands"),
						islands.getDouble(leaderString + ".spawn.x"), islands.getDouble(leaderString + ".spawn.y"),
						islands.getDouble(leaderString + ".spawn.z"), islands.getLong(leaderString + ".spawn.yaw"),
						islands.getLong(leaderString + ".spawn.pitch")), islands.getLong(leaderString + ".created"),
						members, trusted, denied,
						new Location(Bukkit.getWorld("islands"), island.getDouble("region.1.x"),
								island.getDouble("region.1.y"), island.getDouble("region.1.z")),
						new Location(Bukkit.getWorld("islands"), island.getDouble("region.2.x"),
								island.getDouble("region.2.y"), island.getDouble("region.2.z")),
						joinTime, island.getDouble("value"), island.getDouble("cryptos")));
			}

		ConfigurationSection spawners = Files.data.getConfigurationSection("spawners");
		if (spawners != null)
			for (String locationString : spawners.getKeys(false)) {
				ConfigurationSection spawner = spawners.getConfigurationSection(locationString);
				String[] wxyz = locationString.split(" ");
				Location loc = new Location(Bukkit.getWorld(wxyz[0]), Double.valueOf(wxyz[1].replace(",", ".")),
						Double.valueOf(wxyz[2].replace(",", ".")), Double.valueOf(wxyz[3].replace(",", ".")));
				this.spawners.put(loc, new Spawner(this, UUID.fromString(spawner.getString("placedBy")), loc,
						EntityType.fromName(spawner.getString("type")), spawner.getInt("tier")));
			}

		ConfigurationSection luckyBlocks = Files.data.getConfigurationSection("luckyBlocks");
		if (luckyBlocks != null)
			for (String locString : luckyBlocks.getKeys(false)) {
				String[] wxyz = locString.split(" ");
				Location loc = new Location(Bukkit.getWorld(wxyz[0]), Double.valueOf(wxyz[1].replace(",", ".")), Double.valueOf(wxyz[2].replace(",", ".")),
						Double.valueOf(wxyz[3].replace(",", ".")));
				luckyblocks.put(loc, new LuckyBlock(this, luckyBlocks.getInt(locString + ".tier"),
						UUID.fromString(luckyBlocks.getString(locString + ".placedBy"))));
			}

		new BukkitRunnable() {
			public void run() {
				Core.broadcast(prefix + "Shop prices are fluctuating.");
				for (Shop shop : shops.values()) {
					shop.fluctuate();
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 20 * 60 * 60);
		new Placeholders().register();
	}

	public void onDisable() {
		Files.data.set("islandIndex", index);
		Files.data.set("islands", null);
		for (Island island : islands) {
			island.save();
		}
		Files.data.set("spawners", null);
		for (Spawner spawner : spawners.values()) {
			spawner.save();
		}
		Files.data.set("luckyBlocks", null);
		for (Location loc : luckyblocks.keySet()) {
			LuckyBlock lb = luckyblocks.get(loc);
			String locString = (loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ()).replace(".", ",");
			Files.data.set("luckyBlocks." + locString + ".tier", lb.getTier());
			Files.data.set("luckyBlocks." + locString + ".placedBy", lb.getPlacedBy().toString());
		}

		try {
			Files.data.save(Files.dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void loadShops() {
		for (String category : Files.config.getConfigurationSection("shop.categories").getKeys(false)) {
			ShopGUI.categorySlot.put(category, Files.config.getInt("shop.categories." + category + ".slot"));
			ShopGUI.categoryMaterial.put(category,
					Material.getMaterial(Files.config.getString("shop.categories." + category + ".type")));

			Shop shop = new Shop(category, this);
			shops.put(category, shop);
			for (String item : Files.config.getConfigurationSection("shop.items").getKeys(false)) {
				if (Files.config.getString("shop.items." + item + ".category").equalsIgnoreCase(category)) {
					double buy = -1;
					double sell = -1;
					// Random rand = new Random();
					if (Files.config.getString("shop.items." + item + ".buy") != null) {
						buy = Files.config.getDouble("shop.items." + item + ".buy");
						// double b = Files.config.getDouble("shop.items." + item + ".buy");
						// double max = b + ((b / 100) * 10);
						// double min = b - ((b / 100) * 10);
						// double price = min + (max - min) * rand.nextDouble();
						// Core.console(item);
						// Core.console(price+"");
						// shop.setPrice(item, price);
						// shop.setBuyFluctuation(item, 100-((price / b) * 100));
					}
					if (Files.config.getString("shop.items." + item + ".sell") != null) {
						sell = Files.config.getDouble("shop.items." + item + ".sell");
						// double b = Files.config.getDouble("shop.items." + item + ".sell");
						// double max = b + ((b / 100) * 10);
						// double min = b - ((b / 100) * 10);
						// double worth = ((max - min) + min) * rand.nextDouble();
						// shop.setWorth(item, worth);
						// shop.setSellFluctuation(item, ((worth / b) * 100));
					}
					shop.addItem(Material.getMaterial(item.split(";")[0]), Integer.valueOf(item.split(";")[1]), buy,
							sell);
				}
			}
		}
	}
}
