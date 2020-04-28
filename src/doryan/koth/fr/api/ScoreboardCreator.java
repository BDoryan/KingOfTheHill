package doryan.koth.fr.api;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public final class ScoreboardCreator {
	
	public static final int SIZE = 15;
	private static final String TEAM_PREFIX = "V_SS_TEAM_";
	private static final String TITLE = "SIMPLE_SCOREBOARD";
	private static final String OBJECTIVE_NAME = "V_SS_OBJECTIVE";
	private static final Map<Scoreboard, ScoreboardCreator> scoreboardCache = new WeakHashMap();
	private static final BiMap<Integer, OfflinePlayer> playerHolder = HashBiMap.create(15);
	private final Scoreboard scoreboard;
	private final Objective objective;
	private final Map<Integer, Team> teams = new HashMap();
	private int counter;

	public ScoreboardCreator(Scoreboard scoreboard) {
		this(scoreboard, null);
	}

	public ScoreboardCreator(Scoreboard scoreboard, String title) {
		this.scoreboard = scoreboard;

		int teamCounter = 0;
		for (int i = 15; i >= 0; i--) {
			Team team = scoreboard.registerNewTeam("V_SS_TEAM_" + teamCounter++);
			team.addPlayer(getPlayerForPosition(Integer.valueOf(i)));
			this.teams.put(Integer.valueOf(i), team);
		}
		this.objective = scoreboard.registerNewObjective("V_SS_OBJECTIVE", "dummy");
		this.objective.setDisplayName(title == null ? "SIMPLE_SCOREBOARD" : title);
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void clearText(int index) {
		setText(index, null);
	}

	public void setText(int index, String text) {
		int position = 15 - index;
		OfflinePlayer player = getPlayerForPosition(Integer.valueOf(position));
		Team team = (Team) this.teams.get(Integer.valueOf(position));
		if ((text == null) || (text.isEmpty())) {
			this.counter -= 1;
			team.setPrefix("");
			team.setSuffix("");
			this.scoreboard.resetScores(player);
			if (this.counter == 0) {
				this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
			}
			return;
		}
		this.counter += 1;
		if (this.scoreboard.getObjective(DisplaySlot.SIDEBAR) != this.objective) {
			this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		applyTeamName(text, team);

		this.objective.getScore(player).setScore(position);
	}

	public void setTitle(String title) {
		this.objective.setDisplayName(title);
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public int getSize() {
		return 15;
	}

	public static ScoreboardCreator of(Scoreboard scoreboard) {
		ScoreboardCreator ScoreboardCreator = (ScoreboardCreator) scoreboardCache.get(scoreboard);
		if (ScoreboardCreator != null) {
			return ScoreboardCreator;
		}
		ScoreboardCreator = new ScoreboardCreator(scoreboard);
		scoreboardCache.put(scoreboard, ScoreboardCreator);
		return ScoreboardCreator;
	}

	public static BiMap<Integer, OfflinePlayer> getPlayerHolder() {
		return HashBiMap.create(playerHolder);
	}

	public static OfflinePlayer getPlayerForPosition(Integer position) {
		return (OfflinePlayer) playerHolder.get(position);
	}

	public static Integer getPositionForPlayer(OfflinePlayer player) {
		return (Integer) playerHolder.inverse().get(player);
	}

	private static void applyTeamName(String string, Team team) {
		if (string.length() <= 16) {
			team.setPrefix(string);
			team.setSuffix("");
		} else {
			String firstPart = string.substring(0, 16);
			String secondPart = ChatColor.getLastColors(firstPart)
					+ string.substring(16, Math.min(32, string.length()));
			team.setPrefix(firstPart);
			team.setSuffix(secondPart.length() <= 16 ? secondPart : secondPart.substring(0, 16));
		}
	}

	static {
		for (int i = 15; i >= 0; i--) {
			String name = ChatColor.values()[i].toString() + ChatColor.RESET.toString();
			playerHolder.put(Integer.valueOf(i), Bukkit.getOfflinePlayer(name));
		}
	}

	public String getTitle() {
		return this.objective != null ? this.objective.getDisplayName() : null;
	}
}
