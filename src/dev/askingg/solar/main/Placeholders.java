package dev.askingg.solar.main;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.minecraft.server.v1_12_R1.MinecraftServer;

public class Placeholders extends PlaceholderExpansion {

	public String getIdentifier() {
		return "solar";
	}

	public String getPlugin() {
		return null;
	}

	public String getAuthor() {
		return "Askingg";
	}

	public String getVersion() {
		return "1.0";
	}

	@SuppressWarnings("deprecation")
	public String onPlaceholderRequest(Player p, String id) {
		
		if (id.equalsIgnoreCase("ram_max")) {
			return Core.decimals(2, (double) Runtime.getRuntime().maxMemory() / 1048576000D) + "gb";
		}

		if (id.equalsIgnoreCase("ram_allocated")) {
			return Core.decimals(2, (double) Runtime.getRuntime().totalMemory() / 1048576000D) + "gb";
		}

		if (id.equalsIgnoreCase("ram_using")) {
			return Core.decimals(2, ((double) Runtime.getRuntime().totalMemory() / 1048576000D)
					- ((double) Runtime.getRuntime().freeMemory() / 1048576000D)) + "gb";
		}

		if (id.equalsIgnoreCase("ram_free")) {
			return Core.decimals(2, (double) Runtime.getRuntime().freeMemory() / 1048576000D) + "gb";
		}

		if (id.equalsIgnoreCase("tps")) {
			return Core.decimals(2, MinecraftServer.getServer().recentTps[0]);

		}

		return "&7&oUnknown placeholder";
	}
}