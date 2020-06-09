package dev.askingg.solar.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;

public class LuckyBlocksCommand implements CommandExecutor {

	private Solar solar;

	public LuckyBlocksCommand(Solar solar) {
		this.solar = solar;
		solar.getCommand("luckyblocks").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("give")) { // LB Give <Player> <Tier>
			if (args.length > 2) {
				Player p = Bukkit.getPlayer(args[1]);
				if (p != null) {
					int tier = 0;
					try {
						tier = Integer.parseInt(args[2]);
					} catch (Exception ex) {
						Core.message(solar.prefix + "Sorry, but &c" + args[2] + "&f is an invalid integer.", s);
						return true;
					}
					if (tier < 1)
						tier = 1;
					if (tier > 5)
						tier = 5;
					p.getInventory().addItem(solar.lbs.luckyBlock(tier));
					Core.message(
							solar.prefix + "You gave a &6T&e" + tier + "&6 Lucky&eBlock&f to &a" + p.getName() + "&f.",
							s);
					Core.message(solar.prefix + "You received a &6T&e" + tier + "&6 Lucky&eBlock&f.", p);
				} else {
					Core.message(solar.prefix + "Sorry, but &c" + args[1] + "&f is an invalid player.", s);
				}
			} else {
				Core.message(solar.prefix + "Usage&3 &l»&e /LB Give <Player> <Tier>", s);
			}
			return true;
		}
		Core.message(solar.prefix + "Commands for &6Lucky&eBlocks&3 &l»", s);
		Core.message("&3 -&e /LB Help&3 &l»&f View the help list.", s);
		Core.message("&3 -&e /LB Give&3 &l»&f Give a player LuckyBlocks(s).", s);
		return true;
	}
}
