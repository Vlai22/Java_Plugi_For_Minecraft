package Vlad_Plugin;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Scoreboard_My {
	public static  ScoreboardManager manager;
	public static Objective objective;
	public static Scoreboard scoreboard;
	public static void Init_Scoreboard(Player player) {
	    // Создание табло
		scoreboard = (Scoreboard) manager.getNewScoreboard();
	    objective = scoreboard.registerNewObjective("topdata", "dummy","TopInfo"); 
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Выводит табло на боковой панели
	    String result = Sql_DB.Select_One("SELECT money FROM `users` WHERE name = '" + player.getName() + "'");
	    // Добавление записей в табло
	    objective.getScore("Topcoin:").setScore(Integer.valueOf(result));    
	    player.setScoreboard(scoreboard);
	}
	public static void Update_Scoreboard (Player player) {
		String result = Sql_DB.Select_One("SELECT money FROM `users` WHERE name = '" + player.getName() + "'");
		objective.getScore("Topcoin:").setScore(Integer.valueOf(result));
	}
	public static void Update_Sql_Scoreboard (Player player) {
		Sql_DB.Insert_Update("UPDATE `users` SET `money`='" + objective.getScore("Topcoin:").getScore() +"' WHERE name='" + player.getName() + "'");
	}
}
