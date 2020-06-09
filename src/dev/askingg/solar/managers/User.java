package dev.askingg.solar.managers;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.askingg.solar.main.Solar;

public class User {

	private Solar solar;

	private UUID uuid;
	private Island island;
	private UUID leader;
	private UUID invite;
	private long inviteExpire;

	public User(Player player, Solar solar) {
		this.solar = solar;
		this.uuid = player.getUniqueId();
		for (Island island : solar.getIslands()) {
			if (island.getLeader().equals(uuid) || island.getMembers().contains(uuid)) {
				this.island = island;
				this.leader = island.getLeader();
				return;
			}
		}
		this.island = null;
		this.leader = null;
	}

	public boolean hasIsland() {
		if (island != null)
			return true;
		return false;
	}

	public Island getIsland() {
		return island;
	}

	public boolean isIslandLeader() {
		if (uuid.equals(leader))
			return true;
		return false;
	}

	public UUID getIslandLeader() {
		return leader;
	}

	public void setIsland(Island island) {
		this.island = island;
		if (island != null) {
			this.leader = island.getLeader();
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.teleport(this.island.getSpawnLocation());
			}
		} else {
			this.leader = null;
		}
	}

	public void invite(UUID island) {
		invite = island;
		inviteExpire = System.currentTimeMillis() + 60000;
	}

	public UUID getInvite() {
		return invite;
	}

	public void removeInvite() {
		invite = null;
		inviteExpire = 0;
	}

	public boolean hasInvite() {
		if (invite != null)
			return true;
		return false;
	}

	public long getInviteExpirationTime() {
		return inviteExpire;
	}

	public long getInviteExpirationDuration() {
		return inviteExpire - System.currentTimeMillis();
	}

	public File getUserFile() {
		return new File(solar.getDataFolder() + File.separator + "users", uuid.toString() + ".yml");
	}
}
