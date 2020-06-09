package dev.askingg.solar.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.Island;
import dev.askingg.solar.managers.User;

public class IslandGUI implements Listener {

	private static Solar solar;

	public IslandGUI(Solar solar) {
		IslandGUI.solar = solar;
		solar.getServer().getPluginManager().registerEvents(this, solar);
	}

	private static HashMap<Player, String> open = new HashMap<>();
	private static List<Player> a = new ArrayList<>();
	private static List<Player> disband = new ArrayList<>();
	private static HashMap<Player, UUID> kick = new HashMap<>();
	private static HashMap<UUID, Long> value = new HashMap<>();

	@SuppressWarnings("deprecation")
	public static Inventory menu(Player p, String page) {
		Inventory inv = Bukkit.createInventory(null, 45, Core.color("&b&lIsland Panel"));
		User user = solar.users.get(p.getUniqueId());
		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
		glass.setDurability((byte) 7);
		ItemMeta glassm = glass.getItemMeta();
		glassm.setDisplayName(Core.color("&7"));
		glass.setItemMeta(glassm);
		if (user.hasIsland()) {
			Island island = user.getIsland();

			////////////////////////////////////////////////////////////////////////////////////////////////////

			if (page.equals("home")) {
				for (int x = 0; x < 45; x++) {
					inv.setItem(x, glass);
				}
				ItemStack i = new ItemStack(Material.BED);
				ItemMeta m = i.getItemMeta();
				List<String> l = new ArrayList<>();
				m.setDisplayName(Core.color("&bHome"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Left-Click to teleport to your island."));
				l.add(Core.color("&7 •&f Right-Click to set your island's home."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(10, i);
				if (user.isIslandLeader()) {
					i = new ItemStack(Material.BARRIER);
					m = i.getItemMeta();
					l.clear();
					m.setDisplayName(Core.color("&cDisband Island"));
					l.add(Core.color("&7"));
					l.add(Core.color("&7 •&f Double-Click to disband your island."));
					m.setLore(l);
					i.setItemMeta(m);
					inv.setItem(31, i);
				}
				i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta sm = (SkullMeta) i.getItemMeta();
				l.clear();
				sm.setOwner(p.getName());
				sm.setDisplayName(Core.color("&bMembers"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Left-Click to manage island members."));
				l.add(Core.color("&7 •&f Right-Click to invite island members."));
				sm.setLore(l);
				i.setItemMeta(sm);
				inv.setItem(11, i);
				new BukkitRunnable() {
					public void run() {
						if (open.containsKey(p) && open.get(p).equals("home")) {
							UUID leader = island.getLeader();
							Inventory inv = p.getOpenInventory().getTopInventory();
							if (value.containsKey(leader) && System.currentTimeMillis() < value.get(leader)) {
								ItemStack i = new ItemStack(Material.GOLD_INGOT);
								ItemMeta m = i.getItemMeta();
								List<String> l = new ArrayList<>();
								m.setDisplayName(Core.color("&bValue"));
								l.add(Core.color("&7"));
								l.add(Core.color("&7 •&f Value&7 »&b £" + Core.decimals(2, island.getValue())));
								l.add(Core.color("&7"));
								l.add(Core.color("&7 •&f Cooldown&7 »&c "
										+ Core.time(value.get(leader) - System.currentTimeMillis())));
								m.setLore(l);
								i.setItemMeta(m);
								inv.setItem(12, i);
							} else {
								ItemStack i = new ItemStack(Material.GOLD_INGOT);
								ItemMeta m = i.getItemMeta();
								List<String> l = new ArrayList<>();
								m.setDisplayName(Core.color("&bValue"));
								l.add(Core.color("&7"));
								l.add(Core.color("&7 •&f Value&7 »&b £" + Core.decimals(2, island.getValue())));
								l.add(Core.color("&7"));
								l.add(Core.color("&7 •&f Click to calculate your island's value."));
								m.setLore(l);
								i.setItemMeta(m);
								inv.setItem(12, i);
							}
						} else {
							this.cancel();
						}
					}
				}.runTaskTimerAsynchronously(solar, 0, 20);

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("members")) {
				inv = Bukkit.createInventory(null, 27, Core.color("&b&lIsland Members"));
				for (int x = 0; x < 27; x++) {
					inv.setItem(x, glass);
				}
				ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta sm = (SkullMeta) i.getItemMeta();
				List<String> l = new ArrayList<>();
				sm.setDisplayName(Core.color("&b" + Bukkit.getOfflinePlayer(island.getLeader()).getName()));
				sm.setOwner(Bukkit.getOfflinePlayer(island.getLeader()).getName());
				l.add(Core.color("&7"));
				l.add(Core.color(
						"&7 •&f Island leader for " + Core.time(island.getMemberTime(island.getLeader())) + "."));
				sm.setLore(l);
				i.setItemMeta(sm);
				inv.setItem(11, i);
				for (int x = 0; x < island.getMemberCount(); x++) {
					UUID member = island.getMembers().get(x);
					i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
					sm = (SkullMeta) i.getItemMeta();
					l.clear();
					sm.setDisplayName(Core.color("&b" + Bukkit.getOfflinePlayer(member).getName()));
					sm.setOwner(Bukkit.getOfflinePlayer(member).getName());
					l.add(Core.color("&7"));
					l.add(Core.color("&7 •&f Island member for " + Core.time(island.getMemberTime(member)) + "."));
					if (user.isIslandLeader()) {
						l.add(Core.color("&7 •&f Double-Click to kick."));
					}
					sm.setLore(l);
					i.setItemMeta(sm);
					inv.setItem(x + 12, i);
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("invite")) {
				inv = Bukkit.createInventory(null, 36, Core.color("&b&lIsland Recruiting"));
				new BukkitRunnable() {
					public void run() {
						if (open.containsKey(p) && open.get(p).equals("invite")) {
							Inventory inv = p.getOpenInventory().getTopInventory();
							for (int x = 0; x < 36; x++) {
								inv.setItem(x, glass);
							}
							int x = 0;
							for (Player p2 : Bukkit.getOnlinePlayers()) {
								if (p == p2)
									continue;
								String name = p2.getName();
								User user2 = solar.users.get(p2.getUniqueId());
								ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
								SkullMeta m = (SkullMeta) i.getItemMeta();
								m.setOwner(name);
								List<String> l = new ArrayList<>();
								if (user2.hasIsland()) {
									m.setDisplayName(Core.color("&c" + name));
									if (user2.isIslandLeader()) {
										l.add(Core.color("&7 •&f Island leader."));
									} else {
										l.add(Core.color("&7 •&f Island member.&7 ("
												+ Bukkit.getOfflinePlayer(user2.getIslandLeader()).getName() + ")"));
									}
								} else {
									if (user2.hasInvite()) {
										if (user2.getInvite().equals(p.getUniqueId())) {
											m.setDisplayName(Core.color("&a" + name));
											l.add(Core.color("&7 •&f Invite expires in &a"
													+ Core.time(user2.getInviteExpirationDuration()) + "&f."));
										} else {
											m.setDisplayName(Core.color("&e" + name));
											l.add(Core.color("&7 •&f Invite from &e"
													+ Bukkit.getOfflinePlayer(user2.getInvite()).getName()
													+ "&f expires in &e"
													+ Core.time(user2.getInviteExpirationDuration()) + "&f."));
											l.add(Core.color("&7 •&f You can invite &e" + name
													+ "&f once their current invitation expires."));
										}
									} else {
										m.setDisplayName(Core.color("&a" + name));
										l.add(Core.color("&7 •&f Click to invite &a" + name + "&f."));
									}
								}
								l.add(Core.color("&7"));
								if (island.isTrusted(p2.getUniqueId())) {
									l.add(Core.color("&7 •&f Middle-Mouse to remove &b" + name + "'s&f trust status."));
								} else {
									l.add(Core.color("&7 •&f Middle-Mouse to trust &b" + name + "&f."));
								}
								m.setLore(l);
								i.setItemMeta(m);
								inv.setItem(x, i);
								x++;
							}
						}
					}
				}.runTaskTimerAsynchronously(solar, 0, 20);
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////

		} else {
			if (page.equals("noIsland")) {
				inv = Bukkit.createInventory(null, 27, Core.color("&b&lIsland Panel"));
				for (int x = 0; x < 27; x++) {
					inv.setItem(x, glass);
				}
				ItemStack i = new ItemStack(Material.GRASS);
				ItemMeta m = i.getItemMeta();
				List<String> l = new ArrayList<>();
				m.setDisplayName(Core.color("&bCreate Island"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Click to generate your island."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(11, i);
				new BukkitRunnable() {
					public void run() {
						if (open.containsKey(p) && open.get(p).equals("noIsland")) {
							Inventory inv = p.getOpenInventory().getTopInventory();
							if (user.hasInvite()) {
								UUID inviter = user.getInvite();
								String name = Bukkit.getOfflinePlayer(inviter).getName();
								ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
								SkullMeta sm = (SkullMeta) i.getItemMeta();
								sm.setOwner(name);
								l.clear();
								sm.setDisplayName(Core.color("&bInvite From &b" + name));
								l.add(Core.color("&7"));
								l.add(Core.color("&7 •&f Left-Click to accept invite."));
								l.add(Core.color("&7 •&f Right-Click to decline invite."));
								l.add(Core.color("&7"));
								l.add(Core.color(
										"&7 •&f Expires in &c" + Core.time((user.getInviteExpirationDuration()))));
								sm.setLore(l);
								i.setItemMeta(sm);
								inv.setItem(15, i);
							} else {
								ItemStack i = new ItemStack(Material.SKULL_ITEM, 1,
										(short) SkullType.SKELETON.ordinal());
								ItemMeta m = i.getItemMeta();
								l.clear();
								m.setDisplayName(Core.color("&cNo Pending Invites"));
								l.add(Core.color("&7"));
								l.add(Core.color("&7 •&f Sorry, but you have no pending invites."));
								m.setLore(l);
								i.setItemMeta(m);
								inv.setItem(15, i);
							}
						} else {
							this.cancel();
						}
					}
				}.runTaskTimerAsynchronously(solar, 0, 20);

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("islandSelection")) {
				inv = Bukkit.createInventory(null, 27, Core.color("&b&lSchematic Selection"));
				for (int x = 0; x < 27; x++) {
					inv.setItem(x, glass);
				}
				ItemStack i = new ItemStack(Material.DIRT);
				ItemMeta m = i.getItemMeta();
				List<String> l = new ArrayList<>();
				m.setDisplayName(Core.color("&bClassic 1"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Old school L-shaped island."));
				l.add(Core.color("&7 •&f Some grass, stone and an oak tree."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(11, i);
				i = new ItemStack(Material.GRASS);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bClassic 2"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Typical circular shapred island."));
				l.add(Core.color("&7 •&f Some grass, stone and a spruce tree."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(12, i);
				i = new ItemStack(Material.BEDROCK);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bRaw"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Nothing but a block of bedrock and your starter chest."));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f &oDon't wanna have to mine blocks to clear your island?"));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(13, i);
				i = new ItemStack(Material.SAPLING);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bSkyFactory"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f SkyFactory inspired island."));
				l.add(Core.color("&7 •&f One bedrock, dirt and an oak tree."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(14, i);
				i = new ItemStack(Material.MYCEL);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bCustom"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f A large custom island."));
				l.add(Core.color("&7 •&f A variety of resources, giving you a kick start."));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Built by 'Budgizee' on PMC."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(15, i);

			}
		}
		open.put(p, page);
		return inv;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (open.containsKey(p)) {
			String page = open.get(p);
			e.setCancelled(true);
			int slot = e.getRawSlot();
			User user = solar.users.get(p.getUniqueId());
			Island island = user.getIsland();

			////////////////////////////////////////////////////////////////////////////////////////////////////

			if (page.equals("home")) {
				if (slot == 10) {
					if (e.getClick() == ClickType.LEFT) {
						p.teleport(island.getSpawnLocation());
						p.closeInventory();
						Core.message(solar.prefix + "Teleporting you to your island.", p);
					} else if (e.getClick() == ClickType.RIGHT) {
						p.closeInventory();
						island.setSpawnLocation(p.getLocation());
						island.messageAllMembers(
								solar.prefix + "&b" + p.getName() + "&f changed the location of the island home.");
					}
				} else if (slot == 11) {
					if (e.getClick() == ClickType.LEFT) {
						a.add(p);
						p.openInventory(menu(p, "members"));
					} else if (e.getClick() == ClickType.RIGHT) {
						a.add(p);
						p.openInventory(menu(p, "invite"));
					}
				} else if (slot == 12) {
					UUID leader = island.getLeader();
					if (!value.containsKey(leader) || System.currentTimeMillis() > value.get(leader)) {
						value.put(leader, System.currentTimeMillis() + 120000);
						island.updateValue();
					}
				} else if (slot == 31) {
					if (e.getCurrentItem().getType() == Material.BARRIER) {
						if (disband.contains(p)) {
							island.messageAllMembers(solar.prefix + "&c" + p.getName() + "&f disbaned the island!");
							island.disband();
							p.closeInventory();
						} else {
							disband.add(p);
							new BukkitRunnable() {
								public void run() {
									if (disband.contains(p))
										disband.remove(p);
								}
							}.runTaskLaterAsynchronously(solar, 5);
						}
					}
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("members")) {
				ItemStack i = e.getCurrentItem();
				if (i.getType() == Material.SKULL_ITEM) {
					if (user.isIslandLeader()) {
						UUID member = Bukkit.getOfflinePlayer(((SkullMeta) i.getItemMeta()).getOwner()).getUniqueId();
						if (p.getUniqueId() != member) {
							if (kick.containsKey(p)) {
								if (kick.get(p) == member) {
									island.messageAllMembers(
											solar.prefix + "&c" + ((SkullMeta) i.getItemMeta()).getOwner()
													+ "&f was kicked from the island by &b" + p.getName() + "&f.");
									island.leave(member);
								} else {
									kick.put(p, member);
									new BukkitRunnable() {

										public void run() {
											kick.remove(p);
										}
									}.runTaskLaterAsynchronously(solar, 5);
								}
							} else {
								kick.put(p, member);
								new BukkitRunnable() {
									public void run() {
										kick.remove(p);
									}
								}.runTaskLaterAsynchronously(solar, 5);
							}

						} else {
							Core.message(solar.prefix + "Sorry, but you cannot kick yourself from your own island.", p);
						}
					}
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("noIsland")) {
				if (slot < 27) {
					if (slot == 11) {
						a.add(p);
						p.openInventory(menu(p, "islandSelection"));
					} else if (slot == 15) {
						if (user.hasInvite()) {
							solar.users.get(user.getInvite()).getIsland().join(p.getUniqueId());
							user.removeInvite();
							p.closeInventory();
						} else {
							Core.message(solar.prefix + "Sorry, but you don't have a pending invitation.", p);
						}
					}
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("invite")) {
				ItemStack i = e.getCurrentItem();
				if (slot < 36) {
					if (i.getType() == Material.SKULL_ITEM) {
						OfflinePlayer pl = Bukkit
								.getOfflinePlayer(ChatColor.stripColor(Core.color(i.getItemMeta().getDisplayName())));
						UUID uuid = pl.getUniqueId();
						ClickType click = e.getClick();
						if (click.equals(ClickType.MIDDLE)) {
							if (user.isIslandLeader()) {
								if (island.isTrusted(uuid)) {
									island.unTrust(uuid);
									Core.message(solar.prefix + "You removed &c" + pl.getName() + "'s&f trust status.",
											p);
									if (pl.isOnline())
									Core.message(solar.prefix + "Your trust status on &c" + p.getName()
											+ "'s&f island was removed.", Bukkit.getPlayer(uuid));
								} else {
									island.trust(uuid);
									Core.message(
											solar.prefix + "You trusted &a" + pl.getName() + "'s&f on your island.", p);
									if (pl.isOnline())
										Core.message(
												solar.prefix + "You were trusted on &a" + pl.getName() + "'s&f island.",
												Bukkit.getPlayer(uuid));
								}
							} else {
								Core.message(solar.prefix + "Sorry, but only the island leader can do that.", p);
							}
						} else if (click.equals(ClickType.LEFT)) {

						} else if (click.equals(ClickType.RIGHT)) {

						}
					}
				}

				////////////////////////////////////////////////////////////////////////////////////////////////////

			} else if (page.equals("islandSelection") && slot < 27) {
				ItemStack i = e.getCurrentItem();
				if (i.getType() != Material.STAINED_GLASS_PANE) {
					solar.islands.add(new Island(solar, p.getUniqueId(),
							ChatColor.stripColor(Core.color(i.getItemMeta().getDisplayName()))));
					p.closeInventory();
				}
			}
		}

	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (open.containsKey(p)) {
			if (!a.contains(p)) {
				new BukkitRunnable() {
					public void run() {
						open.remove(p);
					}
				}.runTaskLaterAsynchronously(solar, 3);
			} else {
				a.remove(p);
			}
		}
	}
}
