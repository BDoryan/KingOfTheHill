package doryan.koth.fr.commands;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import doryan.koth.fr.Core;
import doryan.koth.fr.listeners.Listeners;
import doryan.koth.fr.utils.Constants;
import doryan.koth.fr.utils.json.JsonItem;
import doryan.koth.fr.utils.koth.KoTH;
import doryan.koth.fr.utils.koth.game.KoTHLocation;

public class KoTHCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			commandSender.sendMessage("§e§m" + Core.LINE_SPACER);
			commandSender.sendMessage("");
			commandSender.sendMessage("§7Liste des commandes de §eHDV§7:");
			commandSender.sendMessage("  §f* §e/koth");
			commandSender.sendMessage("  §f* §e/koth §6wand");
			commandSender.sendMessage("  §f* §e/koth §6info §e<nom>");
			commandSender.sendMessage("  §f* §e/koth §6create §e<nom> <temps pour la capture [seconde(s)]");
			commandSender.sendMessage("  §f* §e/koth §6delete §e<nom>");
			commandSender.sendMessage("  §f* §e/koth §6[start/stop] §e<nom>");
			commandSender.sendMessage("  §f* §e/koth §6setautostart §e<nom> <jours [ex: mercredi,samedi]> <heure [ex: 01:30]>");
			commandSender.sendMessage("");
			commandSender.sendMessage("§e§m" + Core.LINE_SPACER);
		} else {
			if (commandSender.isOp()) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("wand")) {
						if (commandSender instanceof Player) {
							Player player = (Player) commandSender;

							ItemStack itemStack = new ItemStack(Material.STICK);
							ItemMeta itemMeta = itemStack.getItemMeta();
							itemMeta.setDisplayName(Constants.WAND_NAME);
							List<String> lore = new ArrayList<>();
							lore.add("§e§m-------------------------------");
							lore.add("");
							lore.add("§7clic-droit §fpour sélectionner le point n°1");
							lore.add("§7clic-gauche §fpour sélectionner le point n°2");
							lore.add("");
							lore.add("§e§m-------------------------------");

							itemMeta.setLore(lore);

							itemStack.setItemMeta(itemMeta);

							player.getInventory().addItem(itemStack);
						} else {
							commandSender.sendMessage(Constants.KOTH_PREFIX + " §cVous n'êtes pas un joueur !");
						}
					} else {
						commandSender.sendMessage(Constants.KOTH_PREFIX + " §cErreur, essayez: §f/koth");
					}
				} else if (args.length == 2) {
					String name = args[1];

					KoTH koth = null;

					if (args[0].equalsIgnoreCase("delete")) {
						if (KoTH.getKoTHFile(name) != null) {
							if (KoTH.deleteKoTH(name)) {
								commandSender.sendMessage(Constants.KOTH_PREFIX
										+ " §aSuccès, le koth vient d'être supprimé sans problème.");
							} else {
								commandSender.sendMessage(Constants.KOTH_PREFIX
										+ " §cErreur, un problème est survenue lors de la supprimation du KoTH !");
							}
						} else {
							commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe nom de koth n'existe pas !");
						}
					} else if (args[0].equalsIgnoreCase("info")) {
						if ((koth = KoTH.getKoTHLoaded(name)) != null) {
							commandSender.sendMessage("§6§m" + Core.LINE_SPACER);
							commandSender.sendMessage("");
							commandSender.sendMessage("§7Démarrer: " + (koth.isStart() ? "§aOui" : "§cNon"));
							commandSender.sendMessage("");
							commandSender.sendMessage("§7Nom: §f" + koth.getName());
							commandSender.sendMessage("§7Auteur: §f" + koth.getAuthor());
							commandSender.sendMessage("§7Location (capture cuboid): §f");
							commandSender.sendMessage("  §8* " + koth.getLocations()[0].toBukkitString());
							commandSender.sendMessage("  §8* " + koth.getLocations()[1].toBukkitString());
							commandSender.sendMessage("");
							commandSender.sendMessage("§7Date démarrage automatique: §f" + koth.getAutoStart());
							commandSender.sendMessage("");
							commandSender.sendMessage("§6§m" + Core.LINE_SPACER);
						} else {
							commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe koth est introuvable !");
						}
					} else if (args[0].equalsIgnoreCase("start")) {
						if ((koth = KoTH.getKoTHLoaded(name)) != null) {
							if (koth.start()) {
								commandSender.sendMessage(Constants.KOTH_PREFIX + " §aVous avez démarré le koth §f"
										+ koth.getName() + "§a.");
							} else {
								commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe KoTH est déjà démarré !");
							}
						} else {
							commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe koth est introuvable !");
						}
					} else if (args[0].equalsIgnoreCase("stop")) {
						if ((koth = KoTH.getKoTHLoaded(name)) != null) {
							if (koth.stop()) {
								commandSender.sendMessage(Constants.KOTH_PREFIX + " §aVous avez démarré le koth §f"
										+ koth.getName() + "§a.");
							} else {
								commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe KoTH n'est pas démarré !");
							}
						} else {
							commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe koth est introuvable !");
						}
					} else {
						commandSender.sendMessage(Constants.KOTH_PREFIX + " §cErreur, essayez: §f/koth");
					}
				} else if (args.length == 3) {
					String name = args[1];

					if (args[0].equalsIgnoreCase("create")) {
						if (commandSender instanceof Player) {
							Player player = (Player) commandSender;

							try {
								int timeForWin = Integer.valueOf(args[2]);

								Location[] kothLocations = Listeners.blockSelected.get(player);

								if (Listeners.blockSelected.containsKey(player) && kothLocations[0] != null
										&& kothLocations[1] != null) {

									if (KoTH.getKoTHFile(name) == null) {
										ArrayList<JsonItem> items = new ArrayList<>();

										for (ItemStack itemsStack : player.getInventory()) {
											if (itemsStack != null && itemsStack.getType() != Material.AIR) {
												items.add(new JsonItem(itemsStack));
											}
										}

										if (KoTH.createKoTH(name, player.getName(),
												new KoTHLocation[] {
														KoTHLocation.getKoTHLocationToBukkitLocation(kothLocations[0]),
														KoTHLocation
																.getKoTHLocationToBukkitLocation(kothLocations[1]) },
												timeForWin, items)) {
											commandSender.sendMessage(Constants.KOTH_PREFIX
													+ " §aSuccès, le koth vient d'être créé sans problème.");
										} else {
											commandSender.sendMessage(Constants.KOTH_PREFIX
													+ " §cErreur, un problème est survenue lors de la création du KoTH !");
										}
									} else {
										commandSender
												.sendMessage(Constants.KOTH_PREFIX + " §cCe nom est déjà utilisé !");
									}
								} else {
									commandSender.sendMessage(Constants.KOTH_PREFIX
											+ " §cVous n'avez pas sélectionner 2 points afin définir la zone du point de capture !");
								}
							} catch (Exception e) {
								commandSender.sendMessage(Constants.KOTH_PREFIX + " §cVeuillez metter un nombre !");
							}
						} else {
							youAreNotPlayer(commandSender);
						}
					} else {
						commandSender.sendMessage(Constants.KOTH_PREFIX + " §cErreur, essayez: §f/koth");
					}
				} else if (args.length  == 4){
					String name = args[1];
					if (args[0].equalsIgnoreCase("setautostart")) {
						KoTH koth = null;
						if((koth = KoTH.getKoTH(name)) != null){
							String[] days = args[2].replace(" ", "").split(",");
							
							int[] gregorianDays = new int[days.length];
							
							for(int i = 0; i < days.length; i++){
								gregorianDays[i] = Core.convertDayForEnglish(days[i]);
							}
							
							koth.setAutoStart(gregorianDays, args[3]);
							commandSender.sendMessage(Constants.KOTH_PREFIX+" §aLe KoTH §e"+koth.getName()+" §ace démarrera tous les §e"+args[2]+" §aà §e"+args[3]+"§a.");
						} else {
							commandSender.sendMessage(Constants.KOTH_PREFIX + " §cCe KoTH n'existe pas !");
						}
					}
				} else {
					commandSender.sendMessage(Constants.KOTH_PREFIX + " §cErreur, essayez: §f/koth");
				}
			} else {
				invalidCommand(commandSender);
			}
		}
		return false;

	}

	private void youAreNotPlayer(CommandSender commandSender) {
		commandSender.sendMessage(Constants.KOTH_PREFIX + " §cVous n'êtes pas un joueur !");
	}

	private void invalidCommand(CommandSender commandSender) {
		commandSender.sendMessage(Constants.KOTH_PREFIX + " §cVous n'avez pas la permission de faire cette commande !");
	}
}
