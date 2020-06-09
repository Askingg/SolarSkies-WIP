package dev.askingg.solar.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dev.askingg.solar.gui.IslandGUI;
import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.Island;
import dev.askingg.solar.managers.User;

public class IslandCommand implements CommandExecutor {

	private static List<UUID> disbanding = new ArrayList<>();
	private Solar solar;
	
	public IslandCommand(Solar solar) {
		this.solar=solar;
		solar.getCommand("island").setExecutor(this);
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				Core.message(solar.prefix + "Commands for islands&3 &l»", s);
				Core.message("&3 -&b /Island&3 &l»&f Open the island control panel.", s);
				Core.message("&3 -&b /Island Help&3 &l»&f View the help list..", s);
				Core.message("&3 -&b /Island Home&3 &l»&f Teleport to your island.", s);
				Core.message("&3 -&b /Island SetHome&3 &l»&f Set the teleport location of your island.", s);
				Core.message("&3 -&b /Island Create&3 &l»&f Create your island.", s);
				Core.message("&3 -&b /Island Disband&3 &l»&f Delete your island", s);
				Core.message("&3 -&b /Island Invite&3 &l»&f Invite additional members to your island.", s);
				Core.message("&3 -&b /Island Accept&3 &l»&f Accept an island invitation.", s);
				Core.message("&3 -&b /Island Kick&3 &l»&f Remove members from your island.", s);
				Core.message("&3 -&b /Island Trust&3 &l»&f Allow players to modify your island.", s);
				Core.message("&3 -&b /Island UnTrust&3 &l»&f Remove a player's trust status.", s);
				Core.message("&3 -&b /Island Reset&3 &l»&f Restart your island.", s);
				Core.message("&3 -&b /Island Value&3 &l»&f Calculate your island's value.", s);
				Core.message("&3 -&b /Island Top&3 &l»&f View the top island values.", s);
				return true;
			}
			if (args[0].equalsIgnoreCase("home") || args[0].equalsIgnoreCase("go") || args[0].equalsIgnoreCase("tp")
					|| args[0].equalsIgnoreCase("teleport")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					User user = solar.users.get(p.getUniqueId());
					if (user.hasIsland()) {
						p.teleport(user.getIsland().getSpawnLocation());
					} else {
						Core.message(solar.prefix + "Sorry, but you don't have an island.", s);
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("sethome") || args[0].equalsIgnoreCase("settp")
					|| args[0].equalsIgnoreCase("setteleport")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					UUID u = p.getUniqueId();
					User user = solar.users.get(u);
					if (user.hasIsland()) {
						Island is = user.getIsland();
						if (is.isInRegion(p.getLocation())) {
							is.setSpawnLocation(p.getLocation());
							is.messageAllMembers(solar.prefix + "&b" + p.getName() + "&f set the island home.");
						} else {
							Core.message(solar.prefix + "Sorry, but your location is now within your island's region.",
									p);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but you don't have an island.", s);
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("invite")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					UUID u = p.getUniqueId();
					User user = solar.users.get(u);
					if (user.hasIsland()) {
						Island is = user.getIsland();
						if (is.isLeader(u)) {
							if (args.length > 1) {
								Player pl = Bukkit.getPlayer(args[1]);
								if (pl != null) {
									UUID u2 = pl.getUniqueId();
									user = solar.users.get(u2);
									if (!user.hasIsland()) {
										user.invite(u);
										Core.message(
												solar.prefix + "You invited &b" + pl.getName() + "&f to your island.",
												p);
										Core.message(
												solar.prefix + "You were invited to join &b" + p.getName()
														+ "'s&f island. This invitation will expire in 60 seconds.",
												pl);
										String name = p.getName();
										String name2 = pl.getName();
										new BukkitRunnable() {
											public void run() {
												User user = solar.users.get(u2);
												if (user.hasInvite() && user.getInvite() == u) {
													user.removeInvite();
													if (p != null)
														Core.message(solar.prefix + "Your invitation to &b" + name2
																+ "&f has expired.", p);
													if (pl != null)
														Core.message(solar.prefix + "Your invitation to join &b" + name
																+ "'s&f island has expired.", pl);
												}
											}
										}.runTaskLaterAsynchronously(solar, 20 * 60);
									} else {
										Core.message(solar.prefix + "Sorry, but &c" + pl.getName()
												+ "&f already has an island.", p);
									}
								} else {
									Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.",
											p);
								}
							} else {
								Core.message(solar.prefix + "Usage&3 &l»&b /Island Invite <Player>", p);
							}
						} else {
							Core.message(solar.prefix + "Sorry, but you're not the leader of your island.", p);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but you don't have an island.", p);
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("join")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					UUID u = p.getUniqueId();
					User user = solar.users.get(u);
					if (!user.hasIsland()) {
						if (user.hasInvite()) {
							if (args.length > 1) {
								Player pl = Bukkit.getPlayer(args[1]);
								if (pl != null) {
									UUID u2 = pl.getUniqueId();
									if (user.getInvite() == u2) {
										Island island = solar.users.get(u2).getIsland();
										island.join(u);
										island.messageAllMembers(
												solar.prefix + "&b" + p.getName() + "&f joined the island.");
										user.removeInvite();
									} else {
										Core.message(solar.prefix + "Sorry, but you don't have an invitation from &c"
												+ pl.getName() + "&f.", p);
									}
								} else {
									Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.",
											p);
								}
							} else {
								Core.message(solar.prefix + "Usage&3 &l»&b /Island Join <Player>", p);
							}
						} else {
							Core.message(solar.prefix + "Sorry, but you don't have any pending invites.", p);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but you already have an island.", p);
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("create")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					if (!solar.users.get(p.getUniqueId()).hasIsland()) {
						Core.message(solar.prefix+"Sorry, please use the &b/Island&f menu.", p);
					} else {
						Core.message(solar.prefix + "Sorry, but you already have an island.", p);
					}
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("disband") || args[0].equalsIgnoreCase("delete")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					UUID u = p.getUniqueId();
					User user = solar.users.get(u);
					Island is = user.getIsland();
					if (user.hasIsland()) {
						if (is.isLeader(u)) {
							if (disbanding.contains(u)) {
								is.messageAllMembers(solar.prefix + "&b" + p.getName() + "&f disbaned the island.");
								is.disband();
								disbanding.remove(u);
							} else {
								disbanding.add(u);
								Core.message(solar.prefix
										+ "You have 10 second to confirm your island's deletion. To confirm, run &b/Island Disband&f once more.",
										p);
								new BukkitRunnable() {
									public void run() {
										if (disbanding.contains(u)) {
											disbanding.remove(u);
											Core.message(solar.prefix + "Your island disband has expired.", p);
										}
									}
								}.runTaskLater(solar, 200);
							}
						} else {
							Core.message(solar.prefix + "Sorry, but only the leader can disband an island.", p);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but you don't have an island.", p);
					}
				}
				return true;
			}
		} else {
			if (s instanceof Player) {
				Player p = (Player) s;
				if (solar.users.get(p.getUniqueId()).hasIsland()) {
					p.openInventory(IslandGUI.menu(p, "home"));
				} else {
					p.openInventory(IslandGUI.menu(p, "noIsland"));}
			}
		}
		return true;
	}


}
