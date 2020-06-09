package dev.askingg.solar.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Files;
import dev.askingg.solar.main.Solar;

@SuppressWarnings("deprecation")
public class Island {

	private Solar solar;

	private UUID leader;
	private Location spawn;
	private long created;
	private List<UUID> members;
	private List<UUID> trusted;
	private List<UUID> denied;
	private Location location1;
	private Location location2;
	private HashMap<UUID, Long> joinTime;
	private double value;
	private double solarcoins;

	public void save() {
		String leaderName = leader.toString();
		Files.data.set("islands." + leaderName + ".spawn.x", spawn.getX());
		Files.data.set("islands." + leaderName + ".spawn.y", spawn.getY());
		Files.data.set("islands." + leaderName + ".spawn.z", spawn.getZ());
		Files.data.set("islands." + leaderName + ".spawn.pitch", spawn.getPitch());
		Files.data.set("islands." + leaderName + ".created", created);
		List<String> members = new ArrayList<>();
		for (UUID u : this.members) {
			members.add(u.toString());
		}
		Files.data.set("islands." + leaderName + ".members", members);
		List<String> trusted = new ArrayList<>();
		for (UUID u : this.trusted) {
			trusted.add(u.toString());
		}
		Files.data.set("islands." + leaderName + ".trusted", trusted);
		List<String> denied = new ArrayList<>();
		for (UUID u : this.denied) {
			denied.add(u.toString());
		}
		Files.data.set("islands." + leaderName + ".denied", denied);
		Files.data.set("islands." + leaderName + ".region.1.x", location1.getX());
		Files.data.set("islands." + leaderName + ".region.1.y", location1.getY());
		Files.data.set("islands." + leaderName + ".region.1.z", location1.getZ());
		Files.data.set("islands." + leaderName + ".region.2.x", location2.getX());
		Files.data.set("islands." + leaderName + ".region.2.y", location2.getY());
		Files.data.set("islands." + leaderName + ".region.2.z", location2.getZ());
		for (UUID u : this.joinTime.keySet()) {
			Files.data.set("islands." + leaderName + ".joinTime." + u.toString(), this.joinTime.get(u));
		}
		Files.data.set("islands." + leaderName + ".value", value);
		Files.data.set("islands." + leaderName + ".cryptos", solarcoins);

		// try {
		// Files.data.save(Files.dataFile);
		// }catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}

	public Island(Solar solar, UUID leader, String schematicName) {
		this.solar = solar;
		this.leader = leader;
		spawn = new Location(Bukkit.getWorld("islands"), (solar.index * 1000) + 0.5, 102, 0.5);
		this.created = System.currentTimeMillis();
		members = new ArrayList<>();
		trusted = new ArrayList<>();
		denied = new ArrayList<>();
		location1 = new Location(Bukkit.getWorld("islands"), (solar.index * 1000) + 50, 0, 50);
		location2 = new Location(Bukkit.getWorld("islands"), (solar.index * 1000) - 50, 0, -50);
		joinTime = new HashMap<>();
		joinTime.put(leader, System.currentTimeMillis());
		value = 0;
		solarcoins = 0;
		spawn.getWorld().getBlockAt(new Location(Bukkit.getWorld("islands"), (solar.index * 1000) + 0.5, 101, 0.5)).setType(Material.BEDROCK);;
		pasteSchematic(schematicName);
		Player player = Bukkit.getPlayer(leader);
		solar.users.get(leader).setIsland(this);
		solar.index++;
		if (player != null) {
			player.teleport(spawn);
			Core.message(solar.prefix + "Your &aisland&f was created.", player);
		}
	}

	public Island(Solar solar, UUID leader, Location spawn, long created, List<UUID> members, List<UUID> trusted,
			List<UUID> denied, Location location1, Location location2, HashMap<UUID, Long> joinTime, double value,
			double solarcoins) {
		this.solar = solar;
		this.leader = leader;
		this.spawn = spawn;
		this.created = created;
		this.members = members;
		this.trusted = trusted;
		this.denied = denied;
		this.location1 = location1;
		this.location2 = location2;
		this.joinTime = joinTime;
		this.value = value;
		this.solarcoins = solarcoins;
	}

	public void messageAllMembers(String msg) {
		Player p = Bukkit.getPlayer(leader);
		if (p != null) {
			Core.message(msg, p);
		}
		for (UUID u : members) {
			p = Bukkit.getPlayer(u);
			if (p != null) {
				Core.message(msg, p);
			}
		}
	}

	public void disband() {
		solar.users.get(leader).setIsland(null);
		for (UUID u : members) {
			solar.users.get(u).setIsland(null);
		}
		solar.islands.remove(this);
	}

	public UUID getLeader() {
		return leader;
	}

	public boolean isLeader(UUID member) {
		if (leader.equals(member))
			return true;
		return false;
	}

	public Location getSpawnLocation() {
		return spawn;
	}

	public void setSpawnLocation(Location location) {
		spawn = location;
	}

	public long getCreationTime() {
		return created;
	}

	public List<UUID> getMembers() {
		return members;
	}

	public int getMemberCount() {
		return members.size();
	}

	public void join(UUID member) {
		members.add(member);
		solar.users.get(member).setIsland(this);
		joinTime.put(member, System.currentTimeMillis());
	}

	public boolean isMember(UUID player) {
		if (members.contains(player))
			return true;
		return false;
	}

	public void leave(UUID member) {
		members.remove(member);
		joinTime.remove(member);
		solar.users.get(member).setIsland(null);
	}

	public List<UUID> getTrusted() {
		return trusted;
	}

	public void trust(UUID trustee) {
		trusted.add(trustee);
	}

	public void unTrust(UUID trustee) {
		trusted.remove(trustee);
	}

	public boolean isTrusted(UUID player) {
		if (trusted.contains(player))
			return true;
		return false;
	}

	public void deny(UUID player) {
		denied.add(player);
	}

	public void undeny(UUID player) {
		denied.remove(player);
	}

	public boolean isDenied(UUID player) {
		if (denied.contains(player))
			return true;
		return false;
	}

	public void setLocation(Location location1, Location location2) {
		this.location1 = location1;
		this.location2 = location2;
	}

	public boolean isInRegion(Location location) {
		double x = location.getX();
		double z = location.getZ();
		if (x < location1.getX() && x > location2.getX()) {
			if (z < location1.getZ() && z > location2.getZ()) {
				return true;
			}
		}
		return false;
	}

	public void pasteSchematic(String schematicName) {
		EditSession es = new EditSession(
				new BukkitWorld(
						Bukkit.getPlayerExact(Bukkit.getOfflinePlayer(leader).getName()).getLocation().getWorld()),
				99999999);
		try {
			File schem = new File(solar.instance.getDataFolder(), schematicName + ".schematic");
			CuboidClipboard cc = MCEditSchematicFormat.getFormat(schem).load(schem);
			cc.paste(es, BukkitUtil.toVector(spawn), true);
		} catch (DataException | IOException | MaxChangedBlocksException ex) {
			ex.printStackTrace();
		}
	}

	public long getJoinTime(UUID member) {
		return joinTime.get(member);
	}

	public long getMemberTime(UUID member) {
		return System.currentTimeMillis() - joinTime.get(member);
	}

	public double getValue() {
		return value;
	}

	private List<Location> valueLocations = new ArrayList<>();;
	private double val;
	private long startTime;

	public void updateValue() {
		new BukkitRunnable() {
			public void run() {
				messageAllMembers(solar.prefix + "Calculating the &aisland's value&f, please wait...");
				startTime = System.currentTimeMillis();
				valueLocations.clear();
				val = 0;
				World w = Bukkit.getWorld("islands");
				double xx = location1.getX();
				double zz = location1.getZ();
				for (double x = location2.getX(); x < xx; x++) {
					for (double z = location2.getZ(); z < zz; z++) {
						for (double y = 255; y > 0; y--) {
							if (w.getBlockAt(new Location(w, x, y, z)).getType() != Material.AIR) {
								valueLocations.add(new Location(w, x, y, z));
							}
						}
					}
				}
				messageAllMembers(solar.prefix + "Found &b" + Core.decimals(0, valueLocations.size())
						+ "&f blocks, please allow up to &b" + ((valueLocations.size() / 1000) + 1) + "&f seconds.");

				new BukkitRunnable() {
					public void run() {
						for (int x = 0; x < 50; x++) {
							if (valueLocations.size() > 0) {
								Block b = valueLocations.get(0).getWorld().getBlockAt(valueLocations.get(0));
								if (solar.value.containsKey(b.getType().toString() + ";" + b.getData())) {
									val += solar.value.get(b.getType() + ";" + b.getData());
								}
								valueLocations.remove(valueLocations.get(0));
								if (valueLocations.size() % 1000 == 0) {
									if (valueLocations.size() > 0) {
										messageAllMembers(solar.prefix + "&b" + Core.decimals(0, valueLocations.size())
												+ "&f blocks remaining.");
									} else {
										value = val;
										messageAllMembers(solar.prefix + "Island value is &a£" + Core.decimals(2, value)
												+ "&7 (Took "
												+ Core.decimals(2, ((System.currentTimeMillis() - startTime) / 1000))
												+ "s.)");
										this.cancel();
										break;
									}
								}
							}
						}
					}
				}.runTaskTimerAsynchronously(solar, 1, 1);
			}
		}.runTaskLaterAsynchronously(solar, 0);
	}

	public void setCryptos(double cryptos) {
		this.solarcoins = cryptos;
		messageAllMembers(solar.prefix + "&cCrypto&f balance was set to &c" + Core.decimals(5, cryptos) + "&f.");
	}

	public void addCryptos(double cryptos) {
		this.solarcoins += cryptos;
		messageAllMembers(solar.prefix + "&c+ " + Core.decimals(5, cryptos));
	}

	public void removeCryltos(double cryptos) {
		this.solarcoins -= cryptos;
		messageAllMembers(solar.prefix + "&c- " + Core.decimals(5, cryptos));
	}

	public double getCryptos() {
		return solarcoins;
	}

	public boolean canAffortCryptos(double cryptos) {
		return this.solarcoins >= cryptos;

	}
}
