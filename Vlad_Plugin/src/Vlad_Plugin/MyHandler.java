package Vlad_Plugin;

import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.block.Block;

public class MyHandler implements Listener {
	public static HashMap<String, Boolean> Capture_is = new HashMap<>();
	private HashMap<String, Integer> Capture_attempt = new HashMap<>();
	private HashMap<String, String> Capture_text = new HashMap<>();
	private HashMap<String, BukkitTask> Capture_timer = new HashMap<>();
	public static HashMap<Block, String> Store_block = new HashMap<>();
	public static HashMap<String, Inventory> Store_inventory = new HashMap<>();
	public static HashMap<String, ArrayList<ItemStack>> Inventory_arrays = new HashMap<>();
	public static HashMap<ItemStack, Integer> Price_Items = new HashMap<>();
	public static HashMap<ItemStack, Integer> Price_House = new HashMap<>();
	public static HashMap<ItemStack, Integer> Price_Items_Sale = new HashMap<>();
	public static HashMap<ItemStack, Integer> Value_Items_Sale = new HashMap<>();
	public void sendCaptureMap (Player player) {//функция выдачи карты капчи игроку
		player.getInventory().clear();
		MapView map = Bukkit.createMap(player.getWorld());
    	//очистка всех функций рендеринга карты
    	Iterator<MapRenderer> iter = map.getRenderers().iterator();
    	while(iter.hasNext()){
    	    map.removeRenderer(iter.next());
    	}
    	//добавление своей функции рендеренга карты
    	ItemStack map_item = new ItemStack(Material.FILLED_MAP); //создание карты 
    	MapMeta meta = (MapMeta) map_item.getItemMeta();//получение её мета данных 
    	map.addRenderer(new MapRenderer() {//создание рендера карты
            boolean rendered = false;
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                if (rendered) return;
            	String text = "";//создание рандомной капчи и рисовка её на карте
            	String text_char = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
            	for(int i=0;i<3;i++) {
            		text += text_char.charAt(Capture_img.Random(0, text_char.length()));
            	}
            	Capture_text.put(player.getName(), text);
                mapCanvas.drawImage(0, 0, Capture_img.Create_img_Capture(text));
                mapView.setScale(MapView.Scale.NORMAL);
                rendered = true;
            }
        });
    	meta.setMapView(map);//установка нарисованной карты для раннее созданного экземплара карты
    	map_item.setItemMeta(meta);//установка мета данных элементу
    	player.getPlayer().getInventory().addItem(map_item);//выдача карты игроку
    	Capture_timer.put(player.getName() , Bukkit.getScheduler().runTaskLater(Bukkit.getServer().getPluginManager().getPlugin("Vlad_Plugin"), new Runnable() {
    	       @Override
    	       //создание таймера для того что бы кикнуть игрока за долгое бездействие и не нагружать сервер
    	       public void run() {
    	    	   if(!Capture_is.get(player.getName()) && Capture_attempt.get(player.getName())<3) {
    	    		   	Capture_attempt.put(player.getName(), Capture_attempt.get(player.getName())+1);
    	  				sendCaptureMap(player);
    	  				player.sendMessage("Извини но ты не успел по времени");
    	    	   }else if(Capture_attempt.get(player.getName())>=3) {
    	  				player.kickPlayer("Вы не прошли капчу!!Вероятно вы бот!!");
    	    	   }
    	       }
    	 }, 200));//200 = 10 секунд 
	}
	@EventHandler(priority = EventPriority.HIGH)//событиие присоединения игрока
    public void onJoinEvent(PlayerJoinEvent event) { 
    	World world_capture =  Bukkit.getServer().getWorld("Login");//телепорт игрока в мир логина в капчу
    	Location capture = new Location( world_capture, -192, 65, -209 );
    	event.getPlayer().teleport(capture);
    	Capture_is.put(event.getPlayer().getName(), false);//добавление данных о игроках в хем  таблицы
    	Capture_attempt.put(event.getPlayer().getName(), 0);
    	Capture_text.put(event.getPlayer().getName(), "");
    	//отправка сообдения игроку
    	event.getPlayer().sendMessage("Привет, тебе необходимо пройти проверку на бота!!\n Введи в чат то что видешь на своей карте!!");
    	sendCaptureMap(event.getPlayer());//отправка карты игроку
    }
	@EventHandler(priority = EventPriority.HIGH)//событие отправки сообщения игроком
	public void onSendMessage(AsyncPlayerChatEvent event) {
		//данное событие работает только в случае если капча не пройдена 
		if(!Capture_is.get(event.getPlayer().getName()) && Capture_attempt.get(event.getPlayer().getName()) <3) {
			if(Capture_text.get(event.getPlayer().getName()).equals(event.getMessage())) {
				Capture_is.put(event.getPlayer().getName(), true);
				Capture_timer.get(event.getPlayer().getName()).cancel();//уничтожение таймера
		    	Location login = new Location(event.getPlayer().getWorld(), 50, 63, -157);
		        // Запуск синхронной задачи необходим для правильной работы так как получение сообщения асинхронное
		         new BukkitRunnable() {
		               @Override
		               public void run() {
		                   event.getPlayer().teleport(login);
		               }
		           }.runTask(Bukkit.getPluginManager().getPlugin("Vlad_Plugin"));
		    	event.getPlayer().sendMessage("Вы молодец, вы не бот!! Теперь можно зарегестрироваться\n или войти в свой аккаунт");
		    	event.getPlayer().getInventory().clear();
			}else {
				Capture_attempt.put(event.getPlayer().getName(), Capture_attempt.get(event.getPlayer().getName())+1);
				Bukkit.getScheduler().cancelTask(Capture_timer.get(event.getPlayer().getName()).getTaskId());
				Capture_timer.remove(event.getPlayer().getName());
		        // Запуск синхронной задачи необходим для правильной работы так как получение сообщения асинхронное
		        new BukkitRunnable() {
		              @Override
		              public void run() {
		            	  sendCaptureMap(event.getPlayer());
		              }
		        }.runTask(Bukkit.getPluginManager().getPlugin("Vlad_Plugin"));
				event.getPlayer().sendMessage("Вы ввели не верную капчу, у вас осталось " +(3-Capture_attempt.get(event.getPlayer().getName())) + " попыток!!");
			}
		}else if(Capture_attempt.get(event.getPlayer().getName()) >=3) {
			event.getPlayer().kickPlayer("Вы не прошли капчу!! Вероятно вы бот!!");
		}
	}
	@EventHandler//событие клика на какой либо объект в инветоре
	public void onClickItem(InventoryClickEvent event) {
		ItemStack select_item = event.getCurrentItem();//множество проверок на не нуль для корректной работы без ошибок
		if(select_item != null) {
			ItemMeta select_meta = select_item.getItemMeta();
			if (select_meta != null) {
				String select_name = select_meta.getDisplayName();
				if(select_name != null) {//узнаем взял ли человек элемент
					ArrayList<ItemStack> array = Inventory_arrays.get(select_name);
					if(array != null) {//проверяем не равен ли этот элемент переходу в какой либо инвентарь
						event.getInventory().clear();//если да то очищаем нынешний и заполняем новыми элементами
						array.forEach((item) -> event.getInventory().addItem(item));
					}else {//если нет то узнаем является ли данный инвентарь магазином
						//в противном случае просто используем дефолтную логику то есть не пишем код
						if(Store_inventory.get(event.getView().getTitle()) != null) {
							int price_item = 0;
							int price_house = 0;
							int price_item_sale = 0;
							int value_item_sale = 0;
							if(Price_Items.containsKey(event.getCurrentItem())) {
								price_item = Price_Items.get(event.getCurrentItem());//узнаем если выбранный элемент в таблице элементов для покупки	
							}
							if(Price_House.containsKey(event.getCurrentItem())) {
								price_house = Price_House.get(event.getCurrentItem());
							}
							if(Price_Items_Sale.containsKey(event.getCurrentItem())) {
								price_item_sale = Price_Items_Sale.get(event.getCurrentItem());
							}
							if(Value_Items_Sale.containsKey(event.getCurrentItem())) {
								value_item_sale = Value_Items_Sale.get(event.getCurrentItem());
							}
							if (price_item != 0) {
								if(Scoreboard_My.objective.getScore("Topcoin:").getScore() >= price_item) {
									event.getWhoClicked().sendMessage("Вы успешно купили вещь!!");
									Scoreboard_My.objective.getScore("Topcoin:").setScore(Scoreboard_My.objective.getScore("Topcoin:").getScore() - price_item);
									Scoreboard_My.Update_Sql_Scoreboard((Player) event.getWhoClicked());
								}else{
									event.getWhoClicked().sendMessage("У вас недостаточно денег для того что бы купить данный предмет!!");
									event.setCancelled(true);
								}
							}else {
								event.getWhoClicked().sendMessage("Эта вещь не продаётся но вы можете её забрать!!");
							}
							if(price_house != 0) {
								if(Scoreboard_My.objective.getScore("Topcoin:").getScore() >= price_house) {
									event.getWhoClicked().sendMessage("Вы успешно купили дом!!");
									Scoreboard_My.objective.getScore("Topcoin:").setScore(Scoreboard_My.objective.getScore("Topcoin:").getScore() - price_house);
									Scoreboard_My.Update_Sql_Scoreboard((Player) event.getWhoClicked());
									RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
									RegionManager regions = container.get(BukkitAdapter.adapt(event.getWhoClicked().getWorld()));
									if (regions != null) {
										ProtectedRegion region = regions.getRegion(select_name);
										DefaultDomain members = region.getMembers();
										members.addPlayer(event.getWhoClicked().getUniqueId());
									}
									String array1[] = select_name.split("_");
									String dore =  array1[0] + "_dore_" + array1[1];
									ProtectedRegion region = regions.getRegion(dore);
									DefaultDomain members = region.getMembers();
									members.addPlayer(event.getWhoClicked().getUniqueId());
									ArrayList<ItemStack> array2 = Inventory_arrays.get(event.getView().getTitle());
									for(int i=0; i<Inventory_arrays.get(event.getView().getTitle()).size(); i++) {
										if(array2.get(i).equals(event.getCurrentItem())) {
											array2.remove(i);
										}
									}
									Inventory_arrays.put(event.getView().getTitle(), array2);
									event.getWhoClicked().sendMessage(Inventory_arrays.get(event.getView().getTitle()).get(0).getType().getTranslationKey());
								}else{
									event.getWhoClicked().sendMessage("У вас недостаточно денег для того что бы купить данный дом!!");
									event.setCancelled(true);
								}
							}else {
								event.getWhoClicked().sendMessage("Эта вещь не продаётся но вы можете её забрать!!");
							}				
						}
					}
				}
			}
		}
	}
	@EventHandler 
	public void onClickBlock(PlayerInteractEvent event) {//событие обработки клика на блок
		if(event.getClickedBlock() != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) ) {
			if(event.getClickedBlock().getType() == Material.CHEST) {//если данный блок сундук
				//то если есть для него кастомный интерфейс то отрываем его если нет то дефолтное поведение
				event.setCancelled(true);
				String name = Store_block.get(event.getClickedBlock());
				Inventory now_inventory =  Store_inventory.get(name);
				if(now_inventory != null) {
					event.getPlayer().openInventory(now_inventory);	
				}else {
					event.setCancelled(false);
				}
			}else {
				event.setCancelled(false);
			}
		}
	}
	@EventHandler
	public void outInventory(InventoryCloseEvent event) {
		String name = event.getView().getTitle();//собфтие закрытия инвенторя
		if(name != null) {//если инвентарь кастомный тогда очищаем его и заполняем дефолтными компонентами для этого инфенторя
			ArrayList<ItemStack> array = Inventory_arrays.get(name);
			if(array != null) {
				event.getInventory().clear();
				array.forEach((item) -> event.getInventory().addItem(item));	
			}	
		}else {
			event.getPlayer().sendMessage("Ошибка игры, пожалуйста перезагрузите игру!!");
		}
	}
}

