package dev.askingg.solar.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.askingg.solar.main.Solar;

public class Shop {

	private Solar solar;
	
	private String category;
	private List<String> items = new ArrayList<>();
	private HashMap<String, Double> buy = new HashMap<>();
	private HashMap<String, Double> sell = new HashMap<>();
	private HashMap<String, Double> defaultBuy = new HashMap<>();
	private HashMap<String, Double> defaultSell = new HashMap<>();
	private HashMap<String, Double> fluctuationBuy = new HashMap<>();
	private HashMap<String, Double> fluctuationSell = new HashMap<>();

	public Shop(String category, Solar solar) {
		this.setCategory(category);
		this.solar=solar;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void addItem(Material material, int data, double buy, double sell) {
		String item = material.toString() + ";" + data;
		items.add(item);
		if (buy != -1)
			this.defaultBuy.put(item, buy);
		if (sell != -1) {
			this.defaultSell.put(item, sell);
			solar.value.put(item, sell);
		}
	}

	public List<String> getItems() {
		return items;
	}

	public boolean canBuy(Material material, int data) {
		return defaultBuy.containsKey(material.toString() + ";" + data);
	}

	public double getPrice(Material material, int data) {
		return buy.get(material.toString() + ";" + data);
	}

	public void setPrice(String id, double price) {
		buy.put(id, price);
	}

	public boolean canSell(Material material, int data) {
		return defaultSell.containsKey(material.toString() + ";" + data);
	}

	public double getWorth(Material material, int data) {
		return sell.get(material.toString() + ";" + data);
	}

	public void setWorth(String id, double worth) {
		sell.put(id, worth);
	}

	public void sell(ItemStack item) {
		sell.get(item.getType().toString() + ";" + item.getDurability());
	}

	public double getBuyFluctuation(Material material, int data) {
		return fluctuationBuy.get(material.toString() + ";" + data);
	}

	public double getSellFluctuation(Material material, int data) {
		return fluctuationSell.get(material.toString() + ";" + data);
	}

	public void setBuyFluctuation(String item, double fluctuation) {
		fluctuationBuy.put(item, fluctuation);
	}

	public void setSellFluctuation(String item, double fluctuation) {
		fluctuationSell.put(item, fluctuation);
	}
	
	public double getDefaultPrice(Material material, int data) {
		return defaultBuy.get(material.toString() + ";" + data);
	}
	
	public double getDefaultWorth(Material material, int data) {
		return defaultSell.get(material.toString() + ";" + data);
	}

	public void fluctuate() {
		Random rand = new Random();
		for (String item : items) {
			if (defaultBuy.containsKey(item)) {
				double b = defaultBuy.get(item);
				double max = b + ((b / 100) * 20);
				double min = b - ((b / 100) * 20);
				double price = min + (max - min) * rand.nextDouble();
				buy.put(item, price);
				fluctuationBuy.put(item, ((price / b) * 100) - 100);
			}
			if (defaultSell.containsKey(item)) {
				double s = defaultSell.get(item);
				double max = s + ((s / 100) * 10);
				double min = s - ((s / 100) * 10);
				double worth = min + (max - min) * rand.nextDouble();
				sell.put(item, worth);
				fluctuationSell.put(item, ((worth / s) * 100) - 100);
			}
		}
	}
}
