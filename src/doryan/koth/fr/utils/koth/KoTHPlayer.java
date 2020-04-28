package doryan.koth.fr.utils.koth;

import org.bukkit.entity.Player;

import doryan.koth.fr.utils.scoreboard.PlayerScoreboard;

public class KoTHPlayer {

	public String inKothZone;
	public Player player;
	
	public PlayerScoreboard playerScoreboard;
	
	public boolean inChest = false;
	
	public KoTHPlayer(Player player){
		this.player = player;
		this.playerScoreboard = new PlayerScoreboard(this);
	}

	public void clearScoreboard() {/*
		for(int i = 0; i < 15; i++){
			playerScoreboard.scoreboardCreator.clearText(i);	
		}		*/
		
		playerScoreboard.scoreboardCreator.clearLines();
		playerScoreboard.scoreboardCreator.destroy();
	}
}
