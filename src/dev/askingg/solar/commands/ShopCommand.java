package dev.askingg.solar.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import dev.askingg.solar.gui.ShopGUI;
import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Files;
import dev.askingg.solar.main.Solar;

public class ShopCommand implements CommandExecutor {

	private Solar solar;

	public ShopCommand(Solar solar) {
		this.solar = solar;
		solar.getCommand("shop").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (s instanceof ConsoleCommandSender || s.hasPermission("solar.shop.reload")) {
					Files.base();
					solar.shops.clear();
					solar.loadShops();
					Core.message(solar.prefix+"Config reloaded.", s);
				} else {
					Core.message(solar.prefix+"Sorry, but you don't have permission to do that.", s);
				}
				return true;
			}
		}
		Player p = (Player) s;
		p.openInventory(ShopGUI.menu(p, "categories"));
		return false;
	}
}
