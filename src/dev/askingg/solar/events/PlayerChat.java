package dev.askingg.solar.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev.askingg.solar.main.Core;
import dev.askingg.solar.main.Files;
import dev.askingg.solar.main.Solar;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class PlayerChat implements Listener {

	private Solar solar;

	public PlayerChat(Solar solar) {
		this.solar = solar;
		this.solar.getServer().getPluginManager().registerEvents(this, solar);
		loadConfig();
	}

	public List<String> player = new ArrayList<String>();
	public HashMap<String, String> playerChat = new HashMap<String, String>();
	public HashMap<String, List<String>> playerHover = new HashMap<String, List<String>>();
	public HashMap<String, String> playerClick = new HashMap<String, String>();
	public List<String> perm = new ArrayList<String>();
	public HashMap<String, String> permChat = new HashMap<String, String>();
	public HashMap<String, List<String>> permHover = new HashMap<String, List<String>>();
	public HashMap<String, String> permClick = new HashMap<String, String>();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (!e.isCancelled()) {
			Player p = e.getPlayer();
			String m = "";
			String h = "";
			String c = "";
			String message;
			if (p.hasPermission("solar.chat.color")) {
				message = e.getMessage();
			} else {
				message = ChatColor.stripColor(e.getMessage().replace("&", "§"));
			}
			if (this.player.contains(p.getName())) {
				m = color(p, Core.papi(p, this.playerChat.get(p.getName())).replace("%message%", message)
						.replace("%player%", p.getName()));

				if (this.playerHover.get(p.getName()).size() > 0) {
					for (String s : this.playerHover.get(p.getName())) {
						h += (Core.papi(p, s).replace("%player%", p.getName()) + "\n");
					}
				}
				c = Core.papi(p, (this.playerClick.get(p.getName()).replace("%player%", p.getName())));
			} else {
				for (String s : this.perm) {
					if (p.hasPermission("solar.chat." + s)) {
						m = color(p, Core.papi(p, this.permChat.get(s)).replace("%message%", message)
								.replace("%player%", p.getName()));
						if (this.permHover.get(s).size() > 0) {
							for (String st : this.permHover.get(s)) {
								h += (Core.papi(p, st).replace("%player%", p.getName()) + "\n");
							}
						}
						c = Core.papi(p, this.permClick.get(s).replace("%player%", p.getName()));
						break;
					}
				}
			}
			if (m.equals("")) {
				String s = this.perm.get(this.perm.size() - 1);
				m = color(p, Core.papi(p, this.permChat.get(s)).replace("%message%", message).replace("%player%",
						p.getName()));
				if (this.permHover.get(s).size() > 0) {
					for (String st : this.permHover.get(s)) {
						h += (Core.papi(p, st).replace("%player%", p.getName()) + "\n");
					}
				}
				c = Core.papi(p, this.permClick.get(s)).replace("%player%", p.getName());
			}
			for (Player pl : Bukkit.getOnlinePlayers()) {
				chat(pl, Core.color(m), c, h.replace("\\n", System.lineSeparator()));
			}
			Core.console(m);
			e.setCancelled(true);
			// p.setPlayerListName(Tablist.getFormat(p));
		}
	}

	public void chat(Player p, String chat, String click, String hover) {
		p.spigot().sendMessage(new ComponentBuilder(color(p, chat))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(p, hover)).create()))
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click)).create());
	}

	private String color(Player p, String s) {
		String m = "";
		String[] arg = s.split(" ");
		for (int x = 0; x < arg.length; x++) {
			if (x > 0) {
				m += " " + ChatColor.getLastColors(m.replace("&", "§")) + arg[x];
			} else {
				m += arg[x];
			}
		}
		return m;
	}
	
	public void loadConfig() {
		player.clear();
		playerChat.clear();
		playerHover.clear();
		playerClick.clear();
		perm.clear();
		permChat.clear();
		permHover.clear();
		permClick.clear();

		ConfigurationSection conf = Files.config.getConfigurationSection("chat.player");
		if (conf != null) {
			for (String p : conf.getKeys(false)) {
				player.add(p);
				playerChat.put(p, conf.getString(p + ".chat"));
				playerHover.put(p, conf.getStringList(p + ".hover"));
				playerClick.put(p, conf.getString(p + ".click"));
			}
		}

		conf = Files.config.getConfigurationSection("chat.permission");
		if (conf != null) {
			for (String s : conf.getKeys(false)) {
				perm.add(s);
				permChat.put(s, conf.getString(s + ".chat"));
				permHover.put(s, conf.getStringList(s + ".hover"));
				permClick.put(s, conf.getString(s + ".click"));
			}
		}
	}
}
