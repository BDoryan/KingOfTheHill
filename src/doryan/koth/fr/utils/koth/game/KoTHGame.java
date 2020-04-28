package doryan.koth.fr.utils.koth.game;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.massivecraft.factions.Faction;

import doryan.koth.fr.Core;
import doryan.koth.fr.utils.Constants;
import doryan.koth.fr.utils.koth.KoTH;

public class KoTHGame {

	protected String kothName;
	
	public Faction factionTarget;

	public int timeForWin;
	public int currentTime;
	public BukkitTask timeTask;

	public KoTHGame(String kothName, int timeForWin) {
		this.kothName = kothName;
		this.currentTime = timeForWin;
		this.timeForWin = timeForWin;
		
		Bukkit.broadcastMessage(Constants.KOTH_PREFIX+" §6Le koth vient d'être démarré.");
	}

	public void setFactionTarget(Faction faction){
		this.factionTarget = faction;
	}
	
	public void startCapture(){
		timeTask = Bukkit.getScheduler().runTaskTimer(Core.instance, new Runnable() {
			@Override
			public void run() {
				currentTime--;
				
				if(currentTime == 0){
					getKoTH().win(factionTarget);
				}
			}
		}, 0, 20);
	}
	
	public void captureDropped(){
		timeTask.cancel();
		currentTime = timeForWin;
	}
	
	public KoTH getKoTH() {
		return KoTH.getKoTH(kothName);
	}
}
