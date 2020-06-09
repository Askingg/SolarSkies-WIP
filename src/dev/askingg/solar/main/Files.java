package dev.askingg.solar.main;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Files {

	private static Solar solar;

	public Files(Solar solar) {
		Files.solar = solar;
	}

	public static File configFile;
	public static FileConfiguration config;
	public static File dataFile;
	public static FileConfiguration data;

	public static void base() {
		if (!solar.getDataFolder().exists()) {
			solar.getDataFolder().mkdirs();
			Core.console("&7 •&f Created the &a" + solar.getDataFolder().getName().toString() + "&7 folder");
		}

		dataFile = new File(solar.getDataFolder(), "data.yml");
		if (!dataFile.exists()) {
			solar.saveResource("data.yml", false);
			Core.console("&7 •&f Created the &adata.yml&7 file");
		}
		data = YamlConfiguration.loadConfiguration(dataFile);

		configFile = new File(solar.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			solar.saveResource("config.yml", false);
			Core.console("&7 •&f Created the &aconfig.yml&7 file");
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		File users = new File(solar.getDataFolder() + File.separator + "users");
		if (!users.exists())
			users.mkdirs();
		
		solar.index = data.getInt("islandIndex");
	}
}