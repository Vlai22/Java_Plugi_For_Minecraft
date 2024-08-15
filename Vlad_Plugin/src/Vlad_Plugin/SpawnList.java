package Vlad_Plugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnList implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] arg) {
		if(sender instanceof Player) {
			ArrayList<ArrayList<String>> result = Sql_DB.Select_Arrays("SELECT `name`, `posx`, `posy`, `posz` FROM `house_points` WHERE (status = 1)  AND (UUID = '" + Bukkit.getServer().getPlayer(sender.getName()).getUniqueId() + "')");
			int i = 0;
			for(ArrayList<String> item : result) {
				i++;
				sender.sendMessage(i + "." + item.get(0) + " находится на позиции: " + item.get(1) + " " + item.get(2) + " " + item.get(3));
			}
		}
		return false;
	}
}
