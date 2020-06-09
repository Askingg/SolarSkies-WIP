package dev.askingg.solar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Solar;
import dev.askingg.solar.managers.User;

public class JoinLeave implements Listener {

	private Solar solar;

	public JoinLeave(Solar solar) {
		this.solar = solar;
		solar.getServer().getPluginManager().registerEvents(this, solar);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		solar.users.put(p.getUniqueId(), new User(e.getPlayer(), solar));
		if (!p.hasPlayedBefore()) {
			e.setJoinMessage(Core.color("&8[&3&l+&8] &fWelcome, &b" + p.getName() + "&f!"));
		} else if (p.hasPermission("solar.staff")) {
			e.setJoinMessage(Core.color("&8[&3&l+&8] &fWelcome back, &b" + p.getName() + "&f."));
		} else if (p.hasPermission("solar.donor")) {
			e.setJoinMessage(Core.color("&8[&b&l+&8] &fWelcome back, &b" + p.getName() + "&f."));
		} else {
			e.setJoinMessage(Core.color("&8[&a&l+&8] &fWelcome back, &b" + p.getName() + "&f."));
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		solar.users.remove(p.getUniqueId());
		if (p.hasPermission("solar.staff")) {
			e.setQuitMessage(Core.color("&8[&4&l-&8] &fFarewell, &c" + p.getName() + "&f."));
		} else if (p.hasPermission("solar.donor")) {
			e.setQuitMessage(Core.color("&8[&4-&8] &fFarewell, &c" + p.getName() + "&f."));
		} else {
			e.setQuitMessage(Core.color("&8[&c&l-&8] &fFarewell, &c" + p.getName() + "&f."));
		}
	}
}
