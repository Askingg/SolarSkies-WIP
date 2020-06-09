package dev.askingg.solar.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.Shop;
import net.md_5.bungee.api.ChatColor;

public class ShopGUI implements Listener {

	private static Solar solar;

	public ShopGUI(Solar solar) {
		ShopGUI.solar = solar;
		solar.getServer().getPluginManager().registerEvents(this, solar);
	}

	public static HashMap<String, Integer> categorySlot = new HashMap<>();
	public static HashMap<String, Material> categoryMaterial = new HashMap<>();

	private static List<Player> opening = new ArrayList<>();
	private static HashMap<Player, Shop> open = new HashMap<>();
	private static HashMap<Player, Integer> amount = new HashMap<>();
	private static List<Integer> borderSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44,
			45, 46, 47, 48, 49, 50, 51, 52, 53);
	private static HashMap<Player, Integer> page = new HashMap<>();

	public static Inventory menu(Player p, String page) {
		if (!amount.containsKey(p)) {
			amount.put(p, 1);
		}
		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
		glass.setDurability((byte) 7);
		ItemMeta glassm = glass.getItemMeta();
		glassm.setDisplayName(Core.color("&7"));
		glass.setItemMeta(glassm);
		if (page.equals("categories")) {
			open.put(p, null);
		} else {
			open.put(p, solar.shops.get(page));
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////

		Inventory inv = Bukkit.createInventory(null, 27, Core.color("&b&lShop Categories"));
		if (page.equals("categories")) {
			for (int x = 0; x < 27; x++) {
				inv.setItem(x, glass);
			}
			for (String category : solar.shops.keySet()) {
				ItemStack i = new ItemStack(categoryMaterial.get(category));
				ItemMeta m = i.getItemMeta();
				List<String> l = new ArrayList<>();
				m.setDisplayName(Core.color("&b" + category));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Click to go to the &b" + category + "&f shop."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(categorySlot.get(category), i);
			}

		} else {
			////////////////////////////////////////////////////////////////////////////////////////////////////
			inv = Bukkit.createInventory(null, 54, Core.color("&b&l" + page));
			for (int x : borderSlots) {
				inv.setItem(x, glass);
			}
			ItemStack i = new ItemStack(Material.PAPER);
			ItemMeta m = i.getItemMeta();
			List<String> l = new ArrayList<>();
			m.setDisplayName(Core.color("&bPurchase/Sell Amount"));
			l.add(Core.color("&7"));
			int a = amount.get(p);
			if (a == 1) {
				l.add(Core.color("&7 •&b 1"));
			} else {
				l.add(Core.color("&7 •&f 1"));
			}
			if (a == 2) {
				l.add(Core.color("&7 •&b 2"));
			} else {
				l.add(Core.color("&7 •&f 2"));
			}
			if (a == 4) {
				l.add(Core.color("&7 •&b 4"));
			} else {
				l.add(Core.color("&7 •&f 4"));
			}
			if (a == 8) {
				l.add(Core.color("&7 •&b 8"));
			} else {
				l.add(Core.color("&7 •&f 8"));
			}
			if (a == 16) {
				l.add(Core.color("&7 •&b 16"));
			} else {
				l.add(Core.color("&7 •&f 16"));
			}
			if (a == 32) {
				l.add(Core.color("&7 •&b 32"));
			} else {
				l.add(Core.color("&7 •&f 32"));
			}
			if (a == 48) {
				l.add(Core.color("&7 •&b 48"));
			} else {
				l.add(Core.color("&7 •&f 48"));
			}
			if (a == 64) {
				l.add(Core.color("&7 •&b 64"));
			} else {
				l.add(Core.color("&7 •&f 64"));
			}
			l.add(Core.color("&7"));
			l.add(Core.color("&7 •&f Click to scroll through."));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(4, i);
			Shop shop = open.get(p);
			int pages = (shop.getItems().size() / 28) + 1;
			if (pages > 1 && getPage(p) < pages) {
				i = new ItemStack(Material.GLOWSTONE_DUST);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bNext Page"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Click to go to page &b" + (getPage(p) + 1) + "&f."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(53, i);
			}
			if (pages > 1 && getPage(p) > 1) {
				i = new ItemStack(Material.REDSTONE);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bPrevious Page"));
				l.add(Core.color("&7"));
				l.add(Core.color("&7 •&f Click to go to page &b" + (getPage(p) - 1) + "&f."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(45, i);
			}
			List<String> items = shop.getItems();
			for (int x = (getPage(p) - 1) * 28; x < (getPage(p) * 28); x++) {
				if (x >= items.size()) {
					break;
				}
				String itemID = items.get(x);
				Material mat = Material.getMaterial(itemID.split(";")[0]);
				int d = Integer.valueOf(itemID.split(";")[1]);
				i = new ItemStack(mat);
				i.setDurability((byte) d);
				m = i.getItemMeta();
				l.clear();
				if (d == 0) {
					m.setDisplayName(Core.color("&b" + mat.toString().toLowerCase()));
				} else {
					m.setDisplayName(Core.color("&b" + mat.toString().toLowerCase() + "&8 (&7" + d + "&8)"));
				}
				l.add(Core.color("&7"));
				if (shop.canBuy(mat, d)) {
					double price = shop.getPrice(mat, d);
					double fluc = shop.getBuyFluctuation(mat, d);
					if (fluc < 0) {
						l.add(Core.color("&f Buy&8 (&a▼ " + Core.decimals(2, fluc).replace("-", "") + "% from "
								+ Core.decimals(2, shop.getDefaultPrice(mat, d)) + "&8)"));
						l.add(Core.color("&7 •&f " + a + " at &a£" + Core.decimals(2, price * a)));
						l.add(Core.color("&7 •&f Left-Click to buy &a" + a + "&f."));
					} else {
						l.add(Core.color("&f Buy&8 (&c▲ " + Core.decimals(2, fluc) + "% from "
								+ Core.decimals(2, shop.getDefaultPrice(mat, d)) + "&8)"));
						l.add(Core.color("&7 •&f " + a + " at &c£" + Core.decimals(2, price * a)));
						l.add(Core.color("&7 •&f Left-Click to buy &c" + a + "&f."));
					}
				}
				if (shop.canSell(mat, d)) {
					l.add(Core.color("&7"));
					double worth = shop.getWorth(mat, d);
					double fluc = shop.getSellFluctuation(mat, d);
					if (fluc < 0) {
						l.add(Core.color("&f Sell&8 (&c▼ " + Core.decimals(2, fluc).replace("-", "") + "% from "
								+ Core.decimals(2, shop.getDefaultWorth(mat, d)) + "&8)"));
						l.add(Core.color("&7 •&f " + a + " at &c£" + Core.decimals(2, worth * a) + "&8 (&7£"
								+ Core.decimals(2, worth) + " each.&8)"));
						l.add(Core.color("&7 •&f Right-Click to sell &c" + a + "&f."));
						l.add(Core.color("&7 •&f Middle-Mouse to sell all."));
					} else {
						l.add(Core.color("&f Sell&8 (&a▲ " + Core.decimals(2, fluc) + "% from "
								+ Core.decimals(2, shop.getDefaultWorth(mat, d)) + "&8)"));
						l.add(Core.color("&7 •&f " + a + " at &a£" + Core.decimals(2, worth * a) + "&8 (&7£"
								+ Core.decimals(2, worth) + " each.&8)"));
						l.add(Core.color("&7 •&f Right-Click to sell &a" + a + "&f."));
						l.add(Core.color("&7 •&f Middle-Mouse to sell all."));
					}
				}
				m.setLore(l);
				i.setItemMeta(m);
				inv.addItem(i);
			}
		}
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (open.containsKey(p)) {
			e.setCancelled(true);
			if (open.get(p) == null) {
				if (e.getCurrentItem().getType() != Material.STAINED_GLASS_PANE && e.getRawSlot() < 27) {
					opening.add(p);
					p.openInventory(menu(p,
							ChatColor.stripColor(Core.color(e.getCurrentItem().getItemMeta().getDisplayName()))));
				}
			} else {
				////////////////////////////////////////////////////////////////////////////////////////////////////

				int a = amount.get(p);
				if (e.getRawSlot() == 4) {
					if (a == 1) {
						amount.put(p, 2);
					} else if (a == 2) {
						amount.put(p, 4);
					} else if (a == 4) {
						amount.put(p, 8);
					} else if (a == 8) {
						amount.put(p, 16);
					} else if (a == 16) {
						amount.put(p, 32);
					} else if (a == 32) {
						amount.put(p, 48);
					} else if (a == 48) {
						amount.put(p, 64);
					} else if (a == 64) {
						amount.put(p, 1);
					}
					opening.add(p);
					p.openInventory(menu(p, open.get(p).getCategory()));
					return;
				}
				if (e.getRawSlot() == 45 && e.getCurrentItem().getType() == Material.REDSTONE) {
					opening.add(p);
					page.put(p, getPage(p)-1);
					p.openInventory(menu(p, open.get(p).getCategory()));
					return;
				}
				if (e.getRawSlot() == 53 && e.getCurrentItem().getType() == Material.GLOWSTONE_DUST) {
					opening.add(p);
					page.put(p, getPage(p)+1);
					p.openInventory(menu(p, open.get(p).getCategory()));
					return;
				}
				if (e.getRawSlot() < 54) {
					Material m = e.getCurrentItem().getType();
					if (m != Material.AIR && m != Material.STAINED_GLASS_PANE) {
						Shop shop = open.get(p);
						int d = (int) e.getCurrentItem().getDurability();
						if (e.getClick() == ClickType.LEFT) {
							if (shop.canBuy(m, d)) {
								double cost = shop.getPrice(m, d) * a;
								if (solar.eco.getBalance(p) >= cost) {
									solar.eco.withdrawPlayer(p, cost);
									ItemStack i = new ItemStack(m, a);
									i.setDurability((byte) d);
									if (p.getInventory().firstEmpty() == -1) {
										p.getWorld().dropItem(p.getEyeLocation(), i);
									} else {
										p.getInventory().addItem(i);
									}
								} else {
									Core.message(solar.prefix + "Sorry, but you can't afford &c£"
											+ Core.decimals(2, cost) + "&7(You have &c£"
											+ Core.decimals(2, solar.eco.getBalance(p)) + "&7.)", p);
								}
							} else {
								Core.message(solar.prefix + "Sorry, but you can't buy &c" + m.toString().toLowerCase()
										+ "&f.", p);
							}
						} else if (e.getClick() == ClickType.RIGHT) {
							if (shop.canSell(m, d)) {
								int count = 0;
								for (ItemStack i : p.getInventory()) {
									if (i != null && i.getType() == m && (int) i.getDurability() == d) {
										if (i.hasItemMeta()
												&& (i.getItemMeta().hasDisplayName() || i.getItemMeta().hasLore())) {
											continue;
										}
										count += i.getAmount();
									}
								}
								if (count >= a) {
									ItemStack i = new ItemStack(m, a);
									i.setDurability((byte) d);
									p.getInventory().removeItem(i);
									p.updateInventory();
									double worth = shop.getWorth(m, d) * a;
									solar.eco.depositPlayer(p, worth);
									Core.message(solar.prefix + "You sold &b" + a + "x " + m.toString().toLowerCase()
											+ "&f for &a£" + Core.decimals(2, worth) + "&f.", p);
								} else {
									Core.message(solar.prefix + "Sorry, but you don't have &c" + a + "x "
											+ m.toString().toLowerCase() + "&f.", p);

								}
							} else {
								Core.message(solar.prefix + "Sorry, but you cannot sell &c" + m.toString().toLowerCase()
										+ "&f.", p);

							}
						} else if (e.getClick() == ClickType.MIDDLE) {
							if (shop.canSell(m, d)) {
								int count = 0;
								PlayerInventory pInv = p.getInventory();
								for (int x = 0; x < 36; x++) {
									ItemStack i = pInv.getItem(x);
									if (i != null && i.getType() == m && (int) i.getDurability() == d) {
										if (i.hasItemMeta()
												&& (i.getItemMeta().hasDisplayName() || i.getItemMeta().hasLore())) {
											continue;
										}
										count += i.getAmount();
										p.getInventory().setItem(x, null);
									}
								}
								if (count > 0) {
									p.updateInventory();
									double worth = count * shop.getWorth(m, d);
									solar.eco.depositPlayer(p, worth);
									Core.message(solar.prefix + "You sold &b" + Core.decimals(0, count) + "x "
											+ m.toString().toLowerCase() + "&f earning &a£" + Core.decimals(2, worth),
											p);

								} else {
									Core.message(solar.prefix + "Sorry, but you don't have &c" + a + "x "
											+ m.toString().toLowerCase() + "&f.", p);
								}
							} else {
								Core.message(solar.prefix + "Sorry, but you cannot sell &c" + m.toString().toLowerCase()
										+ "&f.", p);
							}
						}
					}
				}
			}
		}
	}

	private static int getPage(Player p) {
		if (page.containsKey(p))
			return page.get(p);
		return 1;
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (open.containsKey(p)) {
			if (opening.contains(p)) {
				opening.remove(p);
			} else {
				new BukkitRunnable() {
					public void run() {
						open.remove(p);
						if (page.containsKey(p))
							page.remove(p);
					}
				}.runTaskLaterAsynchronously(solar, 3);
			}
		}
	}
}
