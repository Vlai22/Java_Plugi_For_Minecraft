package Vlad_Plugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] arg) {
		if(sender instanceof Player) {
			ArrayList<String> result = Sql_DB.Select_Array("SELECT `posx`, `posy`, `posz` FROM `house_points` WHERE (status = 1)  AND (name = '" + arg[0] + "') AND (UUID = '" + Bukkit.getServer().getPlayer(sender.getName()).getUniqueId() + "')");
			if(!result.isEmpty()) {
				Location block_spawn = new Location(Bukkit.getServer().getWorld("world"), Double.valueOf(result.get(0)), Double.valueOf(result.get(1)), Double.valueOf(result.get(0)));
				Bukkit.getServer().getPlayer(sender.getName()).setBedSpawnLocation(block_spawn);
			}
		}
		return false;
	}
}
