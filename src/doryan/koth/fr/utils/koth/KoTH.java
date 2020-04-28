package doryan.koth.fr.utils.koth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import doryan.koth.fr.Core;
import doryan.koth.fr.utils.Constants;
import doryan.koth.fr.utils.FileUtils;
import doryan.koth.fr.utils.json.JsonItem;
import doryan.koth.fr.utils.koth.game.KoTHGame;
import doryan.koth.fr.utils.koth.game.KoTHLocation;

public class KoTH {

	public static HashMap<String, KoTHGame> kothStarted = new HashMap<>();
	public static HashMap<String, KoTH> kothLoaded = new HashMap<>();
	public static HashMap<String, Faction> kothWinner = new HashMap<>(); 

	public static File getKoTHFile(String name) {
		File kothFile = new File(Core.FOLDER_PLUGIN_KOTH, name + ".json");

		if (!kothFile.exists())
			return null;
		return kothFile;
	}

	public static KoTH getKoTHLoaded(String name) {
		if (!(kothLoaded.containsKey(name)))
			return null;
		return kothLoaded.get(name);
	}

	public static boolean createKoTH(String name, String author, KoTHLocation[] locations, int timeForWin, ArrayList<JsonItem> items) {
		if (kothLoaded.containsKey(name))
			return false;

		File kothFile = new File(Core.FOLDER_PLUGIN_KOTH, name + ".json");

		if (kothFile.exists())
			return false;

		try {
			kothFile.createNewFile();

			KoTH koth = new KoTH(name, author, locations, timeForWin, items);

			FileUtils.writeInFile(koth.toJson(), kothFile);

			koth.saveFile();

			Core.log(
					"§6KoTH IS CREATED : §e" + name + "§6, §e" + author + "§6, " + locations[0] + " / " + locations[1]);

			loadKoTH(name);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteKoTH(String name) {
		File kothFile = new File(Core.FOLDER_PLUGIN_KOTH, name + ".json");

		if (!kothFile.exists())
			return false;

		if (kothLoaded.containsKey(name)) {
			unloadKoTH(name);
		}

		kothFile.delete();

		Core.log("§6KoTH IS DELETED : §e" + name);

		return true;
	}

	public static boolean loadKoTH(String name) {
		File kothFile = new File(Core.FOLDER_PLUGIN_KOTH, name + ".json");

		if (!kothFile.exists())
			return false;

		KoTH koth = KoTH.fromJson(FileUtils.readInFile(kothFile));

		if (koth == null)
			return false;

		kothLoaded.put(name, koth);

		Core.log("§6KoTH IS LOADED : §e" + name);

		return true;
	}

	public static boolean unloadKoTH(String name) {
		if (!kothLoaded.containsKey(name))
			return false;

		KoTH koth = kothLoaded.get(name);
		koth.saveFile();

		if (koth.isStart())
			koth.stop();

		kothLoaded.remove(name);

		Core.log("§6KoTH IS UNLOADED : §e" + name);

		return true;
	}

	public static KoTH getKoTH(String name) {
		return kothLoaded.get(name);
	}

	protected List<UUID> playersInZone;

	protected String name;
	protected String author;
	
	protected int[] autoStartDays;
	protected String autoStart;

	protected KoTHLocation[] locations;

	protected int timeForWin;
	
	protected ArrayList<JsonItem> items;

	public KoTH(String name, String author, KoTHLocation[] locations, int timeForWin, ArrayList<JsonItem> items) {
		this.name = name;
		this.author = author;
		this.locations = locations;
		this.timeForWin = timeForWin;
		this.playersInZone = new ArrayList<>();
		this.items = items;
	}

	public void enterZone(Player player) {
		if (!(playersInZone.contains(player.getUniqueId()))) {
			playersInZone.add(player.getUniqueId());
			saveFile();

			if (isStart()) {
				KoTHGame kothGame = kothStarted.get(name);

				Faction playerFaction = FPlayers.i.get(player).getFaction();

				if (playerFaction == null || playerFaction.isNone() || playerFaction.isSafeZone()
						|| playerFaction.isWarZone()) {
					player.sendMessage(Constants.KOTH_PREFIX + " §cVous ne pouvez pas capture le koth sans faction !");
					return;
				}

				if (kothGame.factionTarget == null) {
					kothGame.setFactionTarget(playerFaction);
					kothGame.startCapture();
				}
			}
		}
	}

	public void exitZone(Player player) {
		if (playersInZone.contains(player.getUniqueId())) {
			playersInZone.remove(player.getUniqueId());
			saveFile();

			if (isStart()) {
				KoTHGame kothGame = kothStarted.get(name);

				Faction playerFaction = FPlayers.i.get(player).getFaction();

				if (playerFaction == null)
					return;

				if (playerFaction == null || playerFaction.isNone() || playerFaction.isSafeZone()
						|| playerFaction.isWarZone()) {
					player.sendMessage(Constants.KOTH_PREFIX + " §cVous ne pouvez pas capture le koth sans faction !");
					return;
				}

				if (kothGame.factionTarget != null
						&& playerFaction.getTag().equalsIgnoreCase(kothGame.factionTarget.getTag())) {

					boolean havePlayerInKoTHZone = false;

					for (UUID uuids : playersInZone) {
						Player players = Bukkit.getPlayer(uuids);

						if (players != null) {
							Faction playersFaction = FPlayers.i.get(players).getFaction();

							if (playersFaction != null) {
								if (!(playersFaction.isNone() && playersFaction.isSafeZone()
										&& playersFaction.isWarZone())) {
									if (playerFaction.getTag().equals(playersFaction.getTag())) {
										havePlayerInKoTHZone = true;
										break;
									}
								}
							}
						} else {
							playersInZone.remove(uuids);
						}
					}

					if (!(havePlayerInKoTHZone)) {
						kothGame.setFactionTarget(null);
						kothGame.captureDropped();
						Bukkit.broadcastMessage(Constants.KOTH_PREFIX + " §e" + playerFaction.getTag()
								+ " §cvient de perdre la capture du KoTH §e" + name + " §c!");

						if (playersInZone.size() > 0) {
							int i = 0;

							Faction foundNewFaction = FPlayers.i.get(Bukkit.getPlayer(playersInZone.get(i)))
									.getFaction();
							
							while (true) {
								if (foundNewFaction != null && !foundNewFaction.isNone() && !foundNewFaction.isSafeZone() && !foundNewFaction.isWarZone()) {
									kothGame.setFactionTarget(foundNewFaction);
									kothGame.startCapture();
									break;
								}

								i++;

								UUID uuid = playersInZone.get(i);
								if (playersInZone.size() > i) {
									break;
								} else {
									foundNewFaction = FPlayers.i.get(Bukkit.getPlayer(uuid)).getFaction();
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean isInZone(Player player) {
		return playersInZone.contains(player.getUniqueId());
	}

	public boolean start() {
		if (isStart())
			return false;
		kothStarted.put(name, new KoTHGame(name, timeForWin));
		Bukkit.broadcastMessage(Constants.KOTH_PREFIX + " §e" + name + " §aest maintenant démarré.");
		return true;
	}

	public boolean stop() {
		if (!(isStart()))
			return false;

		KoTHGame kothGame = kothStarted.get(name);

		if (kothGame.timeTask != null)
			kothGame.timeTask.cancel();

		kothStarted.remove(name);
		
		for(Player players : Bukkit.getOnlinePlayers()){
			Core.kothPlayers.get(players).clearScoreboard();
		}

		Bukkit.broadcastMessage(Constants.KOTH_PREFIX + " §e" + name + " §cest maintenant stoppé.");
		return true;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
		saveFile();
	}

	public KoTHLocation[] getLocations() {
		return locations;
	}

	public void setLocations(KoTHLocation[] locations) {
		this.locations = locations;
		saveFile();
	}

	public String getAutoStart() {
		return autoStart;
	}

	public void setAutoStart(int[] gregorianDays, String autoStart) {
		this.autoStartDays = gregorianDays;
		this.autoStart = autoStart;
		saveFile();
	}
	
	public int[] getDays(){
		return this.autoStartDays;
	}

	public KoTHGame getKothGame() {
		return kothStarted.get(name);
	}

	public boolean isStart() {
		return kothStarted.containsKey(name);
	}

	public void saveFile() {
		FileUtils.writeInFile(toJson(), new File(Core.FOLDER_PLUGIN_KOTH, this.name + ".json"));
	}

	public String toJson() {
		return Core.gson.toJson(this);
	}

	public static KoTH fromJson(String json) {
		return Core.gson.fromJson(json, KoTH.class);
	}

	public void win(Faction factionTarget) {
		kothStarted.remove(name);
		
		for(Player players : Bukkit.getOnlinePlayers()){
			Core.kothPlayers.get(players).clearScoreboard();
		}
		
		Location chestLocation = getChestLocation();
		chestLocation.getBlock().setType(Material.CHEST);
		
		Inventory chestInventory = ((Chest) chestLocation.getBlock().getState()).getBlockInventory();
		for(JsonItem jsonItems : items){
			chestInventory.addItem(jsonItems.getItemStack());
		}
		
		kothWinner.put(name, factionTarget);
		
		Bukkit.broadcastMessage(Constants.KOTH_PREFIX+" §e"+factionTarget.getTag()+" §avient de gagné le koth §e"+name+" §a.");
	}

	public Location getChestLocation() {
		
		int y = 0;
		
		if(locations[0].getLocation().getBlockY() /* HIGHER */ > locations[1].getLocation().getBlockY() /* LOWER */){
			y = locations[1].getLocation().getBlockY() + 1;
		} else {
			y = locations[0].getLocation().getBlockY() + 1;
		}
		
		int x = 0;
		int z = 0;
		 
		if(locations[0].getLocation().getBlockX() > locations[1].getLocation().getBlockX()){
			x = locations[1].getLocation().getBlockX() + ((locations[0].getLocation().getBlockX() - locations[1].getLocation().getBlockX()) / 2);
		} else {
			x = locations[0].getLocation().getBlockX() + ((locations[1].getLocation().getBlockX() - locations[0].getLocation().getBlockX()) / 2);
		}

		if (locations[0].getLocation().getBlockZ() > locations[1].getLocation().getBlockZ()) {
			z = locations[1].getLocation().getBlockZ() + ((locations[0].getLocation().getBlockZ() - locations[1].getLocation().getBlockZ()) / 2);
		} else {
			z = locations[0].getLocation().getBlockZ() + ((locations[1].getLocation().getBlockZ() - locations[0].getLocation().getBlockZ()) / 2);
		}
		
		return new Location(locations[0].getLocation().getWorld(), x, y, z);
	}
}
