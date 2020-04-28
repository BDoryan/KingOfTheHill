package doryan.koth.fr.utils.scoreboard;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import doryan.koth.fr.api.ScoreboardCreator;
import doryan.koth.fr.api.ScoreboardSign_1_8_8;
import doryan.koth.fr.utils.koth.KoTH;
import doryan.koth.fr.utils.koth.KoTHPlayer;
import doryan.koth.fr.utils.koth.game.KoTHGame;

public class PlayerScoreboard {

	public KoTHPlayer kothPlayer;
	public ScoreboardSign_1_8_8 scoreboardCreator;

	public PlayerScoreboard(KoTHPlayer kothPlayer) {
		this.kothPlayer = kothPlayer;
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		//this.scoreboardCreator = new ScoreboardCreator(sb);
		this.scoreboardCreator = new ScoreboardSign_1_8_8(kothPlayer.player, "§e* §cKOTH §e*");
		this.scoreboardCreator.create();
	}

	public void sendScoreboard(KoTH koth) {
		if (koth == null) {
			kothPlayer.player.setScoreboard(null);
			return;
		}
		
		if (kothPlayer.player.getScoreboard() != null) {
			if(!this.scoreboardCreator.created){
				this.scoreboardCreator.create();
			}
			
			KoTHGame kothGame = KoTH.kothStarted.get(koth.getName());
			
/*
			this.scoreboardCreator.setText(0, "§8§m----------------------------");
			this.scoreboardCreator.setText(1, "§eMonde:");
			this.scoreboardCreator.setText(2, "§6» §c" + koth.getName());
			this.scoreboardCreator.setText(3, "§6 ");
			this.scoreboardCreator.setText(4, "§ePositon:");
			this.scoreboardCreator.setText(5, "§CX §7» §b" + koth.getChestLocation().getBlockX());
			this.scoreboardCreator.setText(6, "§CY §7» §b" + koth.getChestLocation().getBlockY());
			this.scoreboardCreator.setText(7, "§cZ §7» §b" + koth.getChestLocation().getBlockZ());
			this.scoreboardCreator.setText(8, "§e ");
			this.scoreboardCreator.setText(9, "§eTemps restants:");
			this.scoreboardCreator.setText(10, "§6» §b"	+ new SimpleDateFormat("mm:ss").format(kothGame.currentTime * 1000));
			this.scoreboardCreator.setText(11, "§c ");
			this.scoreboardCreator.setText(12, "§ECapturé par:");
			this.scoreboardCreator.setText(13, "§6» §b"+(kothGame.factionTarget == null ? "§CPersonne" : kothGame.factionTarget.getTag()));
			this.scoreboardCreator.setText(14, "§8§m----------------------------");
			*/
			
			this.scoreboardCreator.setLine(0, "§8§m----------------------------");
			this.scoreboardCreator.setLine(1, "§eMonde:");
			this.scoreboardCreator.setLine(2, "§6» §c" + koth.getName());
			this.scoreboardCreator.setLine(3, "§6 ");
			this.scoreboardCreator.setLine(4, "§ePositon:");
			this.scoreboardCreator.setLine(5, "§CX §7» §b" + koth.getChestLocation().getBlockX());
			this.scoreboardCreator.setLine(6, "§CY §7» §b" + koth.getChestLocation().getBlockY());
			this.scoreboardCreator.setLine(7, "§cZ §7» §b" + koth.getChestLocation().getBlockZ());
			this.scoreboardCreator.setLine(8, "§e ");
			this.scoreboardCreator.setLine(9, "§eTemps restants:");
			this.scoreboardCreator.setLine(10, "§6» §b"	+ new SimpleDateFormat("mm:ss").format(kothGame.currentTime * 1000));
			this.scoreboardCreator.setLine(11, "§c ");
			this.scoreboardCreator.setLine(12, "§ECapturé par:");
			this.scoreboardCreator.setLine(13, "§6» §b"+(kothGame.factionTarget == null ? "§CPersonne" : kothGame.factionTarget.getTag()));
			this.scoreboardCreator.setLine(14, "§8§m----------------------------");
			
			//kothPlayer.player.setScoreboard(this.scoreboardCreator.getScoreboard());	
		}
	}
}
