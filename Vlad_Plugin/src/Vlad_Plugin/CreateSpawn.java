package Vlad_Plugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateSpawn implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] arg) {
		if(sender instanceof Player) {
			ArrayList<ArrayList<String>> result = Sql_DB.Select_Arrays("SELECT `ID`, `name`, `posx`, `posy`, `posz` FROM `house_points` WHERE (status = 1) AND (UUID = '" + Bukkit.getPlayer(sender.getName()).getUniqueId() + "')");
			int posx = Integer.valueOf(arg[1]);
			int posy = Integer.valueOf(arg[2]);
			int posz = Integer.valueOf(arg[3]);
			String name = arg[0];
			if(!result.isEmpty()) {
				if(result.size() <= 7) {
					boolean is_Creating = false;
					for(int i=0;i<result.size();i++) {
						if(name.equals(result.get(i).get(1)) || posx >= Integer.valueOf(result.get(i).get(2))-10 && posx <= Integer.valueOf(result.get(i).get(2)+10) && posy >= Integer.valueOf(result.get(i).get(3))-10 && posy <= Integer.valueOf(result.get(i).get(3))+10  && posz >= Integer.valueOf(result.get(i).get(4))-10 && posz <= Integer.valueOf(result.get(i).get(4))+10 ) {
							is_Creating = true;
							break;
						}
					}
					if(is_Creating) {
						sender.sendMessage("Вы уже создали точку с таким названием или кординатами!!");	
					}else {
						Block block = Bukkit.getServer().getWorld("world").getBlockAt(new Location(Bukkit.getServer().getWorld("world"), posx, posy, posz));
						if(block.getType().getTranslationKey().split("_")[1].toLowerCase() == "bed") {
							boolean is_Air_All = true;
							for(int x=posx-3;x<posx+3;x++) {
								for(int y=posy+1;y<posy+3;y++) {
									for(int z=posz-3;z<posz+3;z++) {
										Block now_block = Bukkit.getServer().getWorld("world").getBlockAt(new Location(Bukkit.getServer().getWorld("world"), x, y, z));
										if(now_block.getType() != Material.AIR) {
											is_Air_All = false;
										}
									}
								}
							}
							if(is_Air_All) {
								Sql_DB.Insert_Update("INSERT INTO `house_points`(`name`, `posx`, `posy`, `posz`, `UUID`, `status`) VALUES ('" + name + "', " + posx +  " ," + posy + " ," + posz + " ,'" + Bukkit.getServer().getPlayer(sender.getName()).getUniqueId() + "', 1)");
							}else {
								sender.sendMessage("Тут нельзя создать точку справна!!");
							}
						}
					}
				}else {
					sender.sendMessage("Нельзя создать больше точек спавна!!");
				}
			}else {
				Block block = Bukkit.getServer().getWorld("world").getBlockAt(new Location(Bukkit.getServer().getWorld("world"), posx, posy, posz));
				sender.sendMessage(block.getType().getTranslationKey().split("_")[1]);
				String name_block = block.getType().getTranslationKey().split("_")[1].replace(" ", "");
				if (block.getType().getTranslationKey().split("_").length >= 2) {
					if(name_block.equals("bed")) {
						boolean is_Air_All = true;
						for(int x=posx-3;x<posx+3;x++) {
							for(int y=posy+1;y<posy+3;y++) {
								for(int z=posz-3;z<posz+3;z++) {
									Block now_block = Bukkit.getServer().getWorld("world").getBlockAt(new Location(Bukkit.getServer().getWorld("world"), x, y, z));
									sender.sendMessage(now_block.getType().getTranslationKey());
									if(now_block.getType() != Material.AIR) {
										is_Air_All = false;
									}
								}
							}
						}
						if(is_Air_All) {
							Sql_DB.Insert_Update("INSERT INTO `house_points`(`name`, `posx`, `posy`, `posz`, `UUID`, `status`) VALUES ('" + name + "', " + posx +  " ," + posy + " ," + posz + " ,'" + Bukkit.getServer().getPlayer(sender.getName()).getUniqueId() + "', 1)");
						}else {
							sender.sendMessage("Тут нельзя создать точку справна!!");
						}
					}else {
						sender.sendMessage("Вы ставите точку не на кровать!!");
					}
				}else {
					sender.sendMessage("Вы ставите точку не на кровать!!");
				}
			}
		}
		return false;
	}
}
