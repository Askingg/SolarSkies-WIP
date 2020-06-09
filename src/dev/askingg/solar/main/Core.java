package dev.askingg.solar.main;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Core {
	
	public static String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String uuidToName(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid).getName();
	}

	public static Player uuidToPlayer(UUID uuid) {
		return Bukkit.getPlayer(uuid);
	}

	public static OfflinePlayer uuidToofflinePlayer(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}

	@SuppressWarnings("deprecation")
	public static UUID nameToUUID(String p) {
		return Bukkit.getOfflinePlayer(p).getUniqueId();
	}

	public static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String decimals(Integer decimalSpaces, Double number) {
		return String.format("%1$,." + decimalSpaces + "f", number);
	}

	public static String decimals(Integer decimalSpaces, long number) {
		return String.format("%1$,." + decimalSpaces + "f", 0.0 + number);
	}

	public static String decimals(Integer decimalSpaces, int number) {
		return String.format("%1$,." + decimalSpaces + "f", 0.0 + number);
	}

	public static String papi(Player p, String str) {
		return PlaceholderAPI.setPlaceholders((OfflinePlayer) p, str);
	}

	public static long timeToMillis(String time, String date) {
		LocalDateTime localDateTime = LocalDateTime.parse(time + " " + date,
				DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static String longToDate(long milis) {
		Date d = new Date(milis);
		DateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		return df.format(d);
	}

	public static List<Player> highLowIntPlayer(HashMap<Player, Integer> inputs) {
		List<Player> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).collect(Collectors.toList());
		return sorted;
	}

	public static List<Player> highLowDoublePlayer(HashMap<Player, Double> inputs) {
		List<Player> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).collect(Collectors.toList());
		return sorted;
	}

	public static List<String> highLowInt(HashMap<String, Integer> inputs) {
		List<String> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).collect(Collectors.toList());
		return sorted;
	}

	public static List<String> highLowDouble(HashMap<String, Double> inputs) {
		List<String> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).collect(Collectors.toList());
		return sorted;
	}

	@SuppressWarnings("deprecation")
	public static List<Player> highLowIntPlayerNoOP(HashMap<Player, Integer> inputs) {
		List<Player> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).filter(player -> !Bukkit.getOfflinePlayer(player.getName()).isOp())
				.collect(Collectors.toList());
		return sorted;
	}

	@SuppressWarnings("deprecation")
	public static List<Player> highLowDoublePlayerNoOP(HashMap<Player, Double> inputs) {
		List<Player> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).filter(player -> !Bukkit.getOfflinePlayer(player.getName()).isOp())
				.collect(Collectors.toList());
		return sorted;
	}

	@SuppressWarnings("deprecation")
	public static List<String> highLowIntNoOP(HashMap<String, Integer> inputs) {
		List<String> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).filter(player -> !Bukkit.getOfflinePlayer(player).isOp())
				.collect(Collectors.toList());
		return sorted;
	}

	@SuppressWarnings("deprecation")
	public static List<String> highLowDoubleNoOP(HashMap<String, Double> inputs) {
		List<String> sorted = inputs.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.map(Map.Entry::getKey).filter(player -> !Bukkit.getOfflinePlayer(player).isOp())
				.collect(Collectors.toList());
		return sorted;
	}

	public static String numerals(int input) {
		if (input == 0 || input > 100) {
			return decimals(0, input);
		}
		String s = "";
		while (input == 100) {
			s += "C";
			input -= 100;
		}
		while (input >= 90) {
			s += "XC";
			input -= 90;
		}
		while (input >= 50) {
			s += "L";
			input -= 50;
		}
		while (input >= 40) {
			s += "XL";
			input -= 40;
		}
		while (input >= 10) {
			s += "X";
			input -= 10;
		}
		while (input >= 9) {
			s += "IX";
			input -= 9;
		}
		while (input >= 5) {
			s += "V";
			input -= 5;
		}
		while (input >= 4) {
			s += "IV";
			input -= 4;
		}
		while (input >= 1) {
			s += "I";
			input -= 1;
		}
		return s;
	}

	public static String time(int seconds) {
		if (seconds < 60) {
			return seconds + "s";
		}
		int minutes = seconds / 60;
		int s = 60 * minutes;
		int secondsLeft = seconds - s;
		if (minutes < 60) {
			if (secondsLeft > 0) {
				return String.valueOf(minutes + "m " + secondsLeft + "s");
			}
			return String.valueOf(minutes + "m");
		}
		if (minutes < 1440) {
			String time = "";
			int hours = minutes / 60;
			time = hours + "h";
			int inMins = 60 * hours;
			int leftOver = minutes - inMins;
			if (leftOver >= 1) {
				time = time + " " + leftOver + "m";
			}
			if (secondsLeft > 0) {
				time = time + " " + secondsLeft + "s";
			}
			return time;
		}
		String time = "";
		int days = minutes / 1440;
		time = days + "d";
		int inMins = 1440 * days;
		int leftOver = minutes - inMins;
		if (leftOver >= 1) {
			if (leftOver < 60) {
				time = time + " " + leftOver + "m";
			} else {
				int hours = leftOver / 60;
				time = time + " " + hours + "h";
				int hoursInMins = 60 * hours;
				int minsLeft = leftOver - hoursInMins;
				if (leftOver >= 1) {
					time = time + " " + minsLeft + "m";
				}
			}
		}
		if (secondsLeft > 0) {
			time = time + " " + secondsLeft + "s";
		}
		return time;
	}

	public static String time(long millis) {
		int seconds = (int) (millis / 1000);
		if (seconds < 60) {
			return seconds + "s";
		}
		int minutes = seconds / 60;
		int s = 60 * minutes;
		int secondsLeft = seconds - s;
		if (minutes < 60) {
			if (secondsLeft > 0) {
				return String.valueOf(minutes + "m " + secondsLeft + "s");
			}
			return String.valueOf(minutes + "m");
		}
		if (minutes < 1440) {
			String time = "";
			int hours = minutes / 60;
			time = hours + "h";
			int inMins = 60 * hours;
			int leftOver = minutes - inMins;
			if (leftOver >= 1) {
				time = time + " " + leftOver + "m";
			}
			if (secondsLeft > 0) {
				time = time + " " + secondsLeft + "s";
			}
			return time;
		}
		String time = "";
		int days = minutes / 1440;
		time = days + "d";
		int inMins = 1440 * days;
		int leftOver = minutes - inMins;
		if (leftOver >= 1) {
			if (leftOver < 60) {
				time = time + " " + leftOver + "m";
			} else {
				int hours = leftOver / 60;
				time = time + " " + hours + "h";
				int hoursInMins = 60 * hours;
				int minsLeft = leftOver - hoursInMins;
				if (leftOver >= 1) {
					time = time + " " + minsLeft + "m";
				}
			}
		}
		if (secondsLeft > 0) {
			time = time + " " + secondsLeft + "s";
		}
		return time;
	}

	public static String number(double d) {
		NumberFormat form = NumberFormat.getInstance(Locale.ENGLISH);

		form.setMaximumFractionDigits(2);

		form.setMinimumFractionDigits(0);
		if (d < 1000.0D) {
			return form.format(d);
		}
		if (d < 1000000.0D) {
			return form.format(d / 1000.0D) + "k";
		}
		if (d < 1.0E9D) {
			return form.format(d / 1000000.0D) + "M";
		}
		if (d < 1.0E12D) {
			return form.format(d / 1.0E9D) + "B";
		}
		if (d < 1.0E15D) {
			return form.format(d / 1.0E12D) + "T";
		}
		if (d < 1.0E18D) {
			return form.format(d / 1.0E15D) + "Q";
		}
		if (d < 1.0E21D) {
			return form.format(d / 1.0E18D) + "aa";
		}
		if (d < 1.0E24D) {
			return form.format(d / 1.0E21D) + "ab";
		}
		if (d < 1.0E27D) {
			return form.format(d / 1.0E24D) + "ac";
		}
		if (d < 1.0E30D) {
			return form.format(d / 1.0E27D) + "ad";
		}
		if (d < 1.0E33D) {
			return form.format(d / 1.0E30D) + "ae";
		}

		long l = (long) d;
		return String.valueOf(l);
	}

	public static void hover(Player p, String chat, String click, String hover) {
		p.spigot()
				.sendMessage(new ComponentBuilder(color(chat))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hover)).create()))
						.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click)).create());
	}

	public static void hoverRunCMD(Player p, String chat, String click, String hover) {
		p.spigot()
				.sendMessage(new ComponentBuilder(color(chat))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hover)).create()))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click)).create());
	}

	public static void noPermission(Player p) {
		p.sendMessage(color("Sorry, but you don't have permission to do that"));
	}

	public static void noPermission(CommandSender s) {
		s.sendMessage(color("Sorry, but you don't have permission to do that"));
	}

	public static void console(String msg) {
		Bukkit.getConsoleSender().sendMessage(color(msg));
	}

	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(color(msg));
	}

	public static void message(String msg, Player p) {
		p.sendMessage(color(msg));
	}

	public static void message(String msg, CommandSender sender) {
		sender.sendMessage(color(msg));
	}
}
