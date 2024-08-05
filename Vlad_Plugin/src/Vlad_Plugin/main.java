package Vlad_Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	public static HashMap<String, Boolean> Login_is = new HashMap<>();
    // Срабатывает при первом включении плагина
    @Override
    public void onEnable ( ) {
    	System.out.println("Vlad_Plugin_isEnable!!");
    	Bukkit.createWorld(new WorldCreator("world"));
    	Bukkit.createWorld(new WorldCreator("Login"));
    	Bukkit.getServer().getWorld("Login").setDifficulty(Difficulty.PEACEFUL);
    	Scoreboard_My.manager = Bukkit.getScoreboardManager(); 
    	getServer().getPluginManager().registerEvents(new MyHandler(), this);
    	getServer().getPluginCommand("login").setExecutor(new Login());
    	getServer().getPluginCommand("registr").setExecutor(new Registretion());
    	createStores();
    	try {
			inputHouseArray();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    // Срабатывает при отключении плагина
    @Override
    public void onDisable () {
    	System.out.println("Vlad_Plugin_isDisable!");
    }
    private static void createStores() {
    	//создание магазина обуви
    	Location store_1_loc = new Location(Bukkit.getServer().getWorld("world"), -1222, 69, -694);
    	Block block = Bukkit.getServer().getWorld("world").getBlockAt(store_1_loc);
    	MyHandler.Store_block.put(block, "Магазин 1");
    	Inventory store_1_inventory = Bukkit.createInventory(null, 9, "Магазин 1");
    	MyHandler.Store_inventory.put("Магазин 1", store_1_inventory);
    	store_1_inventory.clear();
    	ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
		ArrayList<ItemStack> array2 = new ArrayList<>();
		boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		array2.add(boots);
		MyHandler.Price_Items.put(boots, 100);
		boots = new ItemStack(Material.IRON_BOOTS, 1);
		array2.add(boots);
		MyHandler.Price_Items.put(boots, 200);
		boots = new ItemStack(Material.GOLDEN_BOOTS, 1);
		array2.add(boots);
		MyHandler.Price_Items.put(boots, 1000);
		boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
		array2.add(boots);
		MyHandler.Price_Items.put(boots, 10000);
		boots = new ItemStack(Material.NETHERITE_BOOTS, 1);
		array2.add(boots);
		MyHandler.Price_Items.put(boots, 20000);
		MyHandler.Inventory_arrays.put("Магазин обуви", array2);
		//создание магазина 1
		ArrayList<ItemStack> array = new ArrayList<>();
		boots = new ItemStack(Material.IRON_BOOTS, 1);
		ItemMeta boots_Meta = boots.getItemMeta();
		boots_Meta.setDisplayName("Магазин обуви");
		boots_Meta.setLore(Arrays.asList(
			    ChatColor.WHITE + "Эти ботинки являются кнопкой для перехода в магазин обуви", 
			    ChatColor.RED + "Эти ботинки не получится взять" 
		));
		boots.setItemMeta(boots_Meta);
		array.add(boots);
		MyHandler.Inventory_arrays.put("Магазин обуви", array2);
		//создание магазина нагрудников
		ItemStack bib = new ItemStack(Material.IRON_CHESTPLATE, 1);
		ItemMeta bib_Meta = bib.getItemMeta();
		bib_Meta.setDisplayName("Магазин нагрудников");
		bib_Meta.setLore(Arrays.asList(
			    ChatColor.WHITE + "Этот нагрудник являются кнопкой для перехода в магазин нагрудников", 
			    ChatColor.RED + "Этот нагрудник не получится взять" 
		));
		bib.setItemMeta(bib_Meta);
		array.add(bib);
		MyHandler.Inventory_arrays.put("Магазин 1", array);
		store_1_inventory.setItem(0, boots);
		store_1_inventory.setItem(1, bib);
    }
    public void inputHouseArray() throws SQLException {
		//магазин домов
    	Location store_2_loc = new Location(Bukkit.getServer().getWorld("world"), -1223, 70, -655);
    	Block block = Bukkit.getServer().getWorld("world").getBlockAt(store_2_loc);//получаем блок в мире
    	MyHandler.Store_block.put(block, "Магазин 2");//добавляем его в список блоков магазинов с названием Магазин 2
    	Inventory store_2_inventory = Bukkit.createInventory(null, 9, "Магазин 2");//создаём инвентарь Магазин 2
    	ArrayList<ItemStack> array = new ArrayList<>();
    	ArrayList<ArrayList<String>> result = Sql_DB.Select_Array("SELECT name, price FROM `houses` WHERE status = 1");
    	ItemStack house = new ItemStack(Material.STICK, 1);
    	result.forEach((result_arr) -> {
    		ItemMeta house_Meta = house.getItemMeta();
    		house_Meta.setDisplayName(result_arr.get(0));
    		house_Meta.setLore(Arrays.asList(
    			    ChatColor.WHITE + "Эта палка является арендой дома " + result.get(0), 
    			    ChatColor.RED + "Эту палку можно взять только если у вас достаточно денег на аренду" 
    		));
    		house.setItemMeta(house_Meta);
    		array.add(house);
    		MyHandler.Price_House.put(house, Integer.valueOf(result_arr.get(1)));
    	});

    	MyHandler.Inventory_arrays.put("Магазин 2", array);
    	int i=0;
    	array.forEach((item) -> {
    		store_2_inventory.setItem(i, item);
    	});
    	MyHandler.Store_inventory.put("Магазин 2", store_2_inventory);
    }
}