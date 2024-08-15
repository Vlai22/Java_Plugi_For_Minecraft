package Vlad_Plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Login implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] arg) {
		if(sender instanceof Player && MyHandler.Capture_is.get(sender.getName())) {
			Player p = (Player) sender;
			if(arg[0].length() >=3) {
				String result = Sql_DB.Select_One("SELECT name FROM `users` WHERE (name = '" + sender.getName() + "') AND (pass = '" + arg[0] + "')");
				if(result != null) {
					if(result == "") {
						sender.sendMessage("Ошибка вы ввели не верный пароль!!!");
					}else {
				    	World world_main =  Bukkit.getServer().getWorld("world");
				    	Location main_location = new Location( world_main, -387, 95, 368);// -1292 78 -671
				    	p.teleport(main_location);
				    	main.Login_is.put(sender.getName(), true);
				    	Scoreboard_My.Init_Scoreboard(p);
				    	String restul_invenroty = Sql_DB.Select_One("SELECT `inventory` FROM `users` WHERE UUID = '" + p.getUniqueId() + "'");
				    	String[] result_items = restul_invenroty.split("\n");
				    	for(int i=0;i<result_items.length;i++) {
				    		String[] result_item = result_items[i].split(";");
				    		ItemStack item = new ItemStack(Material.getMaterial(result_item[0].replace("Type:", "")), Integer.valueOf(result_item[1].replace("Amount:", "")));
				    		item.setDurability(Short.valueOf(result_item[2].replace("Durability:", "")));
				    		if(result_item.length>4) {
				    			for(int j=4;j<result_item.length;j+=2) {
				    				Enchantment item_encharm = Enchantment.getByKey(NamespacedKey.fromString(result_item[j+1].replace("Enchantment_" + String.valueOf((j-4)/2+1) + ":", "")));
				    				item.addEnchantment(item_encharm, Integer.valueOf(result_item[j].replace("Enchantment_Level_" + String.valueOf((j-4)/2+1) + ":", "")));
				    			}
				    		}
				    		p.getInventory().setItem(Integer.valueOf(result_item[3].replace("Pos:", "")), item);
				    	}
					}
				}else{
					sender.sendMessage("Ошибка сервера по пробуйте позже!!");
				}
			}else {
				sender.sendMessage("Ошибка вы ввели слишком короткий пароль");
			}
		}else {
			System.out.print("Ошибка вы не игрок!!");
		}
		return true;
	}
}