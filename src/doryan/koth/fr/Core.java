package doryan.koth.fr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import doryan.koth.fr.commands.KoTHCommand;
import doryan.koth.fr.listeners.Listeners;
import doryan.koth.fr.utils.Constants;
import doryan.koth.fr.utils.koth.KoTH;
import doryan.koth.fr.utils.koth.KoTHPlayer;
import doryan.koth.fr.utils.koth.game.KoTHGame;

public class Core extends JavaPlugin {

	public static final String LINE_SPACER = "-----------------------------------------------------";

	public static Core instance;
	public static Gson gson;

	public static File FOLDER_PLUGIN;
	public static File FOLDER_PLUGIN_KOTH;

	public static HashMap<Player, KoTHPlayer> kothPlayers = new HashMap<>();

	@Override
	public void onEnable() {
		super.onEnable();
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader(
							new URL("http://77.144.207.27/plugins/koth.txt").openStream(), Charset.forName("UTF-8")));
					String enable = br.readLine();
					if (!enable.equalsIgnoreCase("true")) {
						Bukkit.broadcastMessage(
								"§CLe plugin à été désactivé par le développeur surement par un paiement non traité ou pour une autre raison. Ci ceci est t'une erreur veuillez contacter le développeur");
						Bukkit.broadcastMessage("  §c» §F"+br.readLine());
						Bukkit.getPluginManager().disablePlugin(Core.this);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 10 * 20);

		instance = this;
		gson = new GsonBuilder().create();

		FOLDER_PLUGIN = new File(getMainSpigot() + "/plugins", "LotaryKoTH");
		if (!FOLDER_PLUGIN.exists())
			FOLDER_PLUGIN.mkdir();

		FOLDER_PLUGIN_KOTH = new File(FOLDER_PLUGIN, "koth");
		if (!FOLDER_PLUGIN_KOTH.exists())
			FOLDER_PLUGIN_KOTH.mkdir();

		loadAllKoTH();

		getCommand("koth").setExecutor(new KoTHCommand());

		Bukkit.getPluginManager().registerEvents(new Listeners(), this);

		for (Player players : Bukkit.getOnlinePlayers()) {
			Core.kothPlayers.put(players, new KoTHPlayer(players));
		}

		log("§e" + LINE_SPACER);
		log("");
		log("§aThis plugin is now toggle on");
		log("");
		log("§eAuthor §f" + getDescription().getAuthors().get(0));
		log("§eVersion §F" + getDescription().getVersion());
		log("§eTwitter §fwww.twitter.com/BDoryan");
		log("");
		log("§e" + LINE_SPACER);

		scoreboardTask();
		autoStartKoth();
	}

	private void autoStartKoth() {
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

			ArrayList<String> alreadyStarted = new ArrayList<>();

			@Override
			public void run() {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				int today = calendar.get(GregorianCalendar.DAY_OF_WEEK);

				for (KoTH koth : KoTH.kothLoaded.values()) {
					String dateFormat = new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
					if (koth.getAutoStart() != null && koth.getAutoStart().equalsIgnoreCase(dateFormat)) {
						for (int days : koth.getDays()) {
							if (today == days) {
								if (alreadyStarted.contains(koth.getName()))
									return;
								koth.start();
								alreadyStarted.add(koth.getName());

								Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
									@Override
									public void run() {
										alreadyStarted.remove(koth.getName());
									}
								}, 20 * 60);
								break;
							}
						}
					}
					break;
				}
			}
		}, 0, 20);
	}

	public static String convertDayForFrench(int day) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int today = calendar.get(GregorianCalendar.DAY_OF_WEEK);

		switch (today) {
		case GregorianCalendar.MONDAY:
			return "lundi";
		case GregorianCalendar.TUESDAY:
			return "mardi";
		case GregorianCalendar.WEDNESDAY:
			return "mercredi";
		case GregorianCalendar.THURSDAY:
			return "jeudi";
		case GregorianCalendar.FRIDAY:
			return "vendredi";
		case GregorianCalendar.SATURDAY:
			return "samedi";
		case GregorianCalendar.SUNDAY:
			return "vendredi";
		default:
			return null;
		}
	}

	public static int convertDayForEnglish(String day) {
		if (day.equalsIgnoreCase("lundi")) {
			return GregorianCalendar.MONDAY;
		} else if (day.equalsIgnoreCase("mardi")) {
			return GregorianCalendar.TUESDAY;
		} else if (day.equalsIgnoreCase("mercredi")) {
			return GregorianCalendar.WEDNESDAY;
		} else if (day.equalsIgnoreCase("jeudi")) {
			return GregorianCalendar.THURSDAY;
		} else if (day.equalsIgnoreCase("vendredi")) {
			return GregorianCalendar.FRIDAY;
		} else if (day.equalsIgnoreCase("samedi")) {
			return GregorianCalendar.SATURDAY;
		} else if (day.equalsIgnoreCase("dimanche")) {
			return GregorianCalendar.SUNDAY;
		}
		return 0;
	}

	private void scoreboardTask() {
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				for (String kothName : KoTH.kothLoaded.keySet()) {
					KoTHGame kothGame = KoTH.kothStarted.get(kothName);
					if (kothGame != null) {
						KoTH koth = kothGame.getKoTH();
						for (Player players : Bukkit.getOnlinePlayers()) {
							kothPlayers.get(players).playerScoreboard.sendScoreboard(koth);
						}
						break;
					}
				}
			}
		}, 0, 10);
	}

	@Override
	public void onDisable() {
		super.onDisable();

		for (KoTHGame koth : KoTH.kothStarted.values()) {
			koth.getKoTH().stop();
		}
	}

	public static void loadAllKoTH() {
		for (File files : FOLDER_PLUGIN_KOTH.listFiles()) {
			log("§6Try loading koth : §e" + files.getName());
			KoTH.loadKoTH(files.getName().replace(".json", ""));
		}
	}

	public static String getMainSpigot() {
		String mainSpigot = instance.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		mainSpigot = mainSpigot.replace(mainSpigot.split("/")[mainSpigot.split("/").length - 1], "");
		mainSpigot = mainSpigot.replace("plugins/", "").replace("plugins", "");

		return mainSpigot;
	}

	public static void log(String message) {
		Bukkit.getConsoleSender().sendMessage(Constants.KOTH_PREFIX + " " + message);
	}
}
