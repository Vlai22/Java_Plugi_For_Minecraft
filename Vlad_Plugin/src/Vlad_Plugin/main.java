package Vlad_Plugin;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

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
    	getServer().getPluginCommand("tpspawn").setExecutor(new TpSpawn());
    	getServer().getPluginCommand("createspawn").setExecutor(new CreateSpawn());
    	getServer().getPluginCommand("removespawn").setExecutor(new RemoveSpawn());
    	getServer().getPluginCommand("setspawn").setExecutor(new SetSpawn());
    	getServer().getPluginCommand("spawnlist").setExecutor(new SpawnList());
    	try {
			inputHouseArray();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	CreateSurvey();
    	createStores();
    }
    // Срабатывает при отключении плагина
    @Override
    public void onDisable () {
    	System.out.println("Vlad_Plugin_isDisable!");
    }
    public void inputHouseArray() throws SQLException {
		//магазин домов
    	Location store_2_loc = new Location(Bukkit.getServer().getWorld("world"), -1223, 70, -655);
    	Block block = Bukkit.getServer().getWorld("world").getBlockAt(store_2_loc);//получаем блок в мире
    	MyHandler.Store_block.put(block, "Магазин 2");//добавляем его в список блоков магазинов с названием Магазин 2
    	Inventory store_2_inventory = Bukkit.createInventory(null, 9, "Магазин 2");//создаём инвентарь Магазин 2
    	ArrayList<ItemStack> array = new ArrayList<>();
    	ArrayList<ArrayList<String>> result = Sql_DB.Select_Arrays("SELECT name, price FROM `houses` WHERE status = 1");
    	result.forEach((result_arr) -> {
    		ItemStack house = new ItemStack(Material.STICK, 1);
    		ItemMeta house_Meta = house.getItemMeta();
    		house_Meta.setDisplayName(result_arr.get(0));
    		house_Meta.setLore(Arrays.asList(
    			    ChatColor.WHITE + "Эта палка является арендой дома " + result_arr.get(0), 
    			    ChatColor.RED + "Эту палку можно взять только если у вас достаточно денег на аренду" 
    		));
    		house.setItemMeta(house_Meta);
    		array.add(house);
    		MyHandler.Price_House.put(house, Integer.valueOf(result_arr.get(1)));
    	});
    	MyHandler.Inventory_arrays.put("Магазин 2", array);
    	for(int i=0;i<array.size();i++){
    		store_2_inventory.setItem(i, array.get(i));
    	}
    	MyHandler.Store_inventory.put("Магазин 2", store_2_inventory);
    }
    public void CreateSurvey() {
    	try {
			inputHouseArray();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	ArrayList<ArrayList<String>> result = Sql_DB.Select_Arrays("SELECT ID, date_for_sale, name FROM `houses` WHERE status = 2");
    	LocalDateTime now = LocalDateTime.now();
    	if(!result.isEmpty()) {
    		result.forEach((item) -> {
    			String[] items = item.get(1).split("-");
    			if(String.valueOf(now.getYear()).equals(items[0]) && String.valueOf(now.getMonthValue()).equals(items[1]) && String.valueOf(now.getDayOfMonth()).equals(items[2]) && String.valueOf(now.getHour()).equals(items[3]) && (String.valueOf(now.getMinute()).equals(items[4]) || now.getMinute() >= Integer.valueOf(items[4])-1)) {
    				if(now.getMinute() == Integer.valueOf(items[4])-1) {
    					Bukkit.getScheduler().runTaskLater(Bukkit.getServer().getPluginManager().getPlugin("Vlad_Plugin"), new Runnable() {
    			    	       @Override
    			    	       public void run() {
    			    	    	   Sql_DB.Insert_Update("UPDATE `houses` SET `uuid`='',`date_sale`='',`date_for_sale`='',`status`='1' WHERE ID = " + item.get(0));
    			    	       }
    			    	 }, 1200 + Integer.valueOf(items[5])*20);//если до окончания аренды примерно 2 минуты то мы создаём отложеную задачу на 1 минуту и количество секунд до окончания аренды 
    				}else if(now.getMinute() == Integer.valueOf(items[4])) {
    					Bukkit.getScheduler().runTaskLater(Bukkit.getServer().getPluginManager().getPlugin("Vlad_Plugin"), new Runnable() {
 			    	       @Override
 			    	       public void run() {
 			    	    	   Sql_DB.Insert_Update("UPDATE `houses` SET `uuid`='',`date_sale`='',`date_for_sale`='',`status`='1' WHERE ID = " + item.get(0));
 			    	       }
 			    	 }, Integer.valueOf(items[5])*20);//если до окончания аренды примерно 1 минуты то мы создаём отложеную задачу на количество секунд до окончания аренды
    				}
    				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
					RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
					if (regions != null) {
						ProtectedRegion region = regions.getRegion(item.get(2));
						DefaultDomain members = region.getMembers();
						members.clear();
					}
					String array1[] = item.get(2).split("_");
					String dore =  array1[0] + "_dore_" + array1[1];
					ProtectedRegion region = regions.getRegion(dore);
					DefaultDomain members = region.getMembers();
					members.clear();
    			}
    		});
    	}
    	Bukkit.getServer().getOnlinePlayers().forEach((player) ->{
    		MyHandler.saveInventory(player);
    	});
		Bukkit.getScheduler().runTaskLater(Bukkit.getServer().getPluginManager().getPlugin("Vlad_Plugin"), new Runnable() {
 	       @Override
 	       public void run() {
 	    	   CreateSurvey();
 	       }
 	 }, 1200);//перевызов данной функции каждую минуту
    }
    public void createStores() {
		//магазин вещей
    	//магазин в магазине
    	ArrayList<ArrayList<String>> result = Sql_DB.Select_Arrays("SELECT  `name`, `pox`, `posy`, `posz`, `ID` FROM `stores` WHERE (type = 3) AND (status = 1)");
    	result.forEach((store) -> {
    		Location now_location = new Location(Bukkit.getServer().getWorld("world"), Integer.valueOf(store.get(1)), Integer.valueOf(store.get(2)), Integer.valueOf(store.get(3)));
        	Block now_block = Bukkit.getServer().getWorld("world").getBlockAt(now_location);//получаем блок в мире
        	MyHandler.Store_block.put(now_block, store.get(0));//добавляем его в список блоков магазинов с названием магазина
        	Inventory store_2_inventory = Bukkit.createInventory(null, 54, store.get(0));//создаём инвентарь магазина
        	ArrayList<ItemStack> store_2_itemsstack = new ArrayList<>();
        	ArrayList<ArrayList<String>> stores_in_store = Sql_DB.Select_Arrays("SELECT  `name`, `pox`, `posy`, `posz`, `itemstackname` FROM `stores` WHERE (type = 1) AND (store_in_store = " + store.get(4) +") AND (status = 1)");
        	stores_in_store.forEach((item) -> {
        			ItemStack store_item = new ItemStack(Material.getMaterial(item.get(4)), 1);
        			ItemMeta store_item_meta = store_item.getItemMeta();
        			store_item_meta.setDisplayName(item.get(0));
        	    	store_item_meta.setLore(Arrays.asList(
        	    			   ChatColor.WHITE + "Этот предмет не получится забрать",
        	    			   ChatColor.WHITE + "так как данный предмет переход в ",
        	    			   ChatColor.WHITE + "магазин товаров" 
        	    	));
        	    	store_item.setItemMeta(store_item_meta);
        			store_2_inventory.addItem(store_item);
        			store_2_itemsstack.add(store_item);
        			ArrayList<ItemStack> store_itemsstack = new ArrayList<>();
        			ArrayList<ArrayList<String>> items_in_store = Sql_DB.Select_Arrays("SELECT `itemstackname`, `price`, `amount`, `type` FROM `items_for_sale` WHERE store_name = '" + item.get(0) + "'");
        			items_in_store.forEach((item_in_store) -> {
        				ItemStack element_in_store = new ItemStack(Material.getMaterial(item_in_store.get(3)), Integer.valueOf(item_in_store.get(2)));
        				MyHandler.Price_Items.put(element_in_store, Integer.valueOf(item_in_store.get(1)));
        				store_itemsstack.add(element_in_store);
        			});
                	MyHandler.Inventory_arrays.put(item.get(0), store_itemsstack);
        	});
        	MyHandler.Store_inventory.put(store.get(0), store_2_inventory);
        	MyHandler.Inventory_arrays.put(store.get(0), store_2_itemsstack);
    	});
    	//обычный магазин
    	ArrayList<ArrayList<String>> result_stores_classic = Sql_DB.Select_Arrays("SELECT  `name`, `pox`, `posy`, `posz`, `status`, `ID` FROM `stores` WHERE (type = 1) AND (status = 1) ");
    	result_stores_classic.forEach((item) -> {
    		Location now_location = new Location(Bukkit.getServer().getWorld("world"), Integer.valueOf(item.get(1)), Integer.valueOf(item.get(2)), Integer.valueOf(item.get(3)));
        	Block now_block = Bukkit.getServer().getWorld("world").getBlockAt(now_location);//получаем блок в мире
        	MyHandler.Store_block.put(now_block, item.get(0));//добавляем его в список блоков магазинов с названием магазина
        	Inventory store_2_inventory = Bukkit.createInventory(null, 54, item.get(0));//создаём инвентарь магазина
        	ArrayList<ItemStack> store_2_itemsstack = new ArrayList<>();
        	ArrayList<ArrayList<String>> result_stores = Sql_DB.Select_Arrays("SELECT `itemstackname`, `price`, `amount`, `type` FROM `items_for_sale` WHERE store_name = '" + item.get(0) + "'");
        	result_stores.forEach((item_result) -> {
        		ItemStack element_in_store = new ItemStack(Material.getMaterial(item_result.get(3)), Integer.valueOf(item_result.get(2)));
				MyHandler.Price_Items.put(element_in_store, Integer.valueOf(item_result.get(1)));
				store_2_itemsstack.add(element_in_store);
				store_2_inventory.addItem(element_in_store);
        	});
        	MyHandler.Store_inventory.put(item.get(0), store_2_inventory);
        	MyHandler.Inventory_arrays.put(item.get(0), store_2_itemsstack);
    	});
    	//скупщики товаров
    	ArrayList<ArrayList<String>> result_stores_in_sale = Sql_DB.Select_Arrays("SELECT  `name`, `pox`, `posy`, `posz`, `status`, `ID` FROM `stores` WHERE (type = 2) AND (status = 1) AND (store_in_store = 0)");
    	result_stores_in_sale.forEach((item) -> {
    		Location now_location = new Location(Bukkit.getServer().getWorld("world"), Integer.valueOf(item.get(1)), Integer.valueOf(item.get(2)), Integer.valueOf(item.get(3)));
        	Block now_block = Bukkit.getServer().getWorld("world").getBlockAt(now_location);//получаем блок в мире
        	MyHandler.Store_block.put(now_block, item.get(0));//добавляем его в список блоков магазинов с названием магазина
        	Inventory store_2_inventory = Bukkit.createInventory(null, 54, item.get(0));//создаём инвентарь магазина
        	ArrayList<ItemStack> store_2_itemsstack = new ArrayList<>();
        	ArrayList<ArrayList<String>> result_stores = Sql_DB.Select_Arrays("SELECT `itemstackname`, `amount`, `price` FROM `items_for_purchase` WHERE store_name = '" + item.get(0) + "'");
        	result_stores.forEach((item_result) -> {
        		ItemStack element_in_store = new ItemStack(Material.getMaterial(item_result.get(0)), Integer.valueOf(item_result.get(1)));
				MyHandler.Price_Items_Sale.put(element_in_store, Integer.valueOf(item_result.get(2)));
				MyHandler.Value_Items_Sale.put(element_in_store, Integer.valueOf(item_result.get(1)));
				store_2_itemsstack.add(element_in_store);
				store_2_inventory.addItem(element_in_store);
        	});
        	MyHandler.Store_inventory.put(item.get(0), store_2_inventory);
        	MyHandler.Inventory_arrays.put(item.get(0), store_2_itemsstack);
    	});
    }
}