package Vlad_Plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				    	Location main_location = new Location( world_main, -1292, 78, -671);
				    	p.teleport(main_location);
				    	main.Login_is.put(sender.getName(), true);
				    	Scoreboard_My.Init_Scoreboard(p);
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