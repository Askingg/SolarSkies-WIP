package dev.askingg.solar.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.Island;
import dev.askingg.solar.managers.User;

public class SolarCoinCommand implements CommandExecutor {

	private Solar solar;

	public SolarCoinCommand(Solar solar) {
		this.solar = solar;
		solar.getCommand("solarcoin").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) { // coins Bal (Player)
				if (args.length > 1) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						User user = solar.users.get(p.getUniqueId());
						if (user.hasIsland()) {
							if (user.isIslandLeader()) {
								Core.message(solar.prefix + "&a" + p.getName() + "'s island&f has &c"
										+ Core.decimals(5, user.getIsland().getCryptos()) + " &bS&fC&f.", p);
							} else {
								Core.message(solar.prefix + "&a"
										+ Bukkit.getOfflinePlayer(user.getIsland().getLeader()).getName()
										+ "'s island&f has &c" + Core.decimals(5, user.getIsland().getCryptos())
										+ " &bS&fC&f.", p);
							}
						} else {
							Core.message(
									solar.prefix + "Sorry, but &c" + p.getName() + "&f doesn't have an &aisland&f.", s);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.", s);
					}
				} else {
					if (s instanceof Player) {
						User user = solar.users.get(((Player) s).getUniqueId());
						if (user.hasIsland()) {
							Core.message(solar.prefix + "&cCrypto&f balance&3 &l»&c "
									+ Core.decimals(5, user.getIsland().getCryptos()) + "&f.", s);
						} else {
							Core.message(solar.prefix + "Sorry, but you don't have an &aisland&f.", s);
						}
					}
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("add")) {// coins Give <Player> <Amount>
				if( s instanceof ConsoleCommandSender || s.hasPermission("solar.coins.give")) {
				if (args.length >= 3) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (solar.users.get(p.getUniqueId()).hasIsland()) {
							double coins = 0;
							try {
								coins = Double.parseDouble(args[2]);
							} catch (Exception ex) {
								Core.message(solar.prefix + "Sorry, but &c" + args[2] + "&f is an invalid double.", s);
								return true;
							}
							solar.users.get(p.getUniqueId()).getIsland().addCryptos(coins);
						} else {
							Core.message(
									solar.prefix + "Sorry, but &c" + p.getName() + "&f doesn't have an &aisland&f.", s);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.", s);
					}
				} else {
					Core.message(solar.prefix + "Usage&3 &l»&b /Solar&fCoin Give <Player> <Amount>", s);
				}
				} else {
					Core.message(solar.prefix+"Sorry, but you don't have permission to do that.", s);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("withdraw")
					|| args[0].equalsIgnoreCase("remove")) {// Cryptos Take <Player> <Amount>
				if( s instanceof ConsoleCommandSender || s.hasPermission("solar.coins.take")) {
				if (args.length >= 3) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (solar.users.get(p.getUniqueId()).hasIsland()) {
							double coins = 0;
							try {
								coins = Double.parseDouble(args[2]);
							} catch (Exception ex) {
								Core.message(solar.prefix + "Sorry, but &c" + args[2] + "&f is an invalid double.", s);
								return true;
							}
							Island island = solar.users.get(p.getUniqueId()).getIsland();
							if (island.canAffortCryptos(coins)) {
								island.removeCryltos(coins);
							} else {
								island.setCryptos(0);
							}
						} else {
							Core.message(
									solar.prefix + "Sorry, but &c" + p.getName() + "&f doesn't have an &aisland&f.", s);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.", s);
					}
				} else {
					Core.message(solar.prefix + "Usage&3 &l»&b /Solar&fCoin Give <Player> <Amount>", s);
				}
				} else {
					Core.message(solar.prefix+"Sorry, but you don't have permission to do that.", s);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("put")) {// Cryptos Set <Player> <Amount>
				if( s instanceof ConsoleCommandSender || s.hasPermission("solar.coins.set")) {
				if (args.length >= 3) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (solar.users.get(p.getUniqueId()).hasIsland()) {
							double coins = 0;
							try {
								coins = Double.parseDouble(args[2]);
							} catch (Exception ex) {
								Core.message(solar.prefix + "Sorry, but &c" + args[2] + "&f is an invalid double.", s);
								return true;
							}
							solar.users.get(p.getUniqueId()).getIsland().setCryptos(coins);
						} else {
							Core.message(
									solar.prefix + "Sorry, but &c" + p.getName() + "&f doesn't have an &aisland&f.", s);
						}
					} else {
						Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.", s);
					}
				} else {
					Core.message(solar.prefix + "Usage&3 &l»&b /Solar&fCoin Give <Player> <Amount>", s);
				}
				} else {
					Core.message(solar.prefix+"Sorry, but you don't have permission to do that.", s);
				}
				return true;
			}
		}
		Core.message(solar.prefix + "Commands for &bSolar&fCoins&3 &l»", s);
		Core.message("&3 -&b /Solar&fCoin&b Help&3 &l»&f View the help list.", s);
		Core.message("&3 -&b /Solar&fCoin&b Bal&3 &l»&f View an &aisland's&b Solar&fCoin balance.", s);
		Core.message("&3 -&b /Solar&fCoin&b Give&3 &l»&f Give an &aisland&f. &c&bS&fC&f.", s);
		Core.message("&3 -&b /Solar&fCoin&b Take&3 &l»&f Withdraw &c&bS&fC&f from an &aisland&f.", s);
		Core.message("&3 -&b /Solar&fCoin&b Set&3 &l»&f Set an &aisland's&b Solar&fCoin balance.", s);
		return true;
	}
}
