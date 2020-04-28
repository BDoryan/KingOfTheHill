package doryan.koth.fr.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.sun.security.auth.module.Krb5LoginModule;

import doryan.koth.fr.Core;
import doryan.koth.fr.utils.Constants;
import doryan.koth.fr.utils.Cuboid;
import doryan.koth.fr.utils.koth.KoTH;
import doryan.koth.fr.utils.koth.KoTHPlayer;

public class Listeners implements Listener {

	public static HashMap<Player, Location[]> blockSelected = new HashMap<>();

	@EventHandler
	public void PlayerJoinEvent_(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		Core.kothPlayers.put(player, new KoTHPlayer(player));
	}

	@EventHandler
	public void PlayerQuitEvent_(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		KoTHPlayer kothPlayer = Core.kothPlayers.get(player);

		kothPlayer.clearScoreboard();

		if (kothPlayer.inKothZone != null) {
			KoTH.getKoTH(kothPlayer.inKothZone).exitZone(player);
		}

		Core.kothPlayers.remove(player);
	}

	@EventHandler
	public void BlockBreakEvent_(BlockBreakEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStackInHand = player.getItemInHand();

		if (itemStackInHand != null && itemStackInHand.hasItemMeta() && itemStackInHand.getItemMeta().hasDisplayName()
				&& itemStackInHand.getItemMeta().getDisplayName().equals(Constants.WAND_NAME)) {
			Block block = e.getBlock();

			if (!blockSelected.containsKey(player)) {
				blockSelected.put(player, new Location[2]);
			}

			blockSelected.get(player)[0] = block.getLocation();
			e.setCancelled(true);

			player.sendMessage(Constants.KOTH_PREFIX + " §6Vous avez sélectionner le point n°§e1§6.");
		}
	}
	
	@EventHandler
	public void InventoryCloseEvent_(InventoryCloseEvent e){
		Player player = (Player) e.getPlayer();
		KoTHPlayer kothPlayer = Core.kothPlayers.get(player);

		if(e.getView().getType() == InventoryType.CHEST){
			for(KoTH koths : KoTH.kothLoaded.values()){
				if(new Cuboid(koths.getLocations()[0].getLocation(), koths.getLocations()[1].getLocation()).isInCube(player.getLocation())){
					for(ItemStack items : e.getView().getTopInventory().getContents()){
						if(items != null && items.getType() != Material.AIR){
							player.getInventory().addItem(items);
						}
					}
					
					e.getView().getTopInventory().clear();
					koths.getChestLocation().getBlock().setType(Material.AIR);
					
					kothPlayer.inChest = false;
					break;
				}
			}
		}
	}

	@EventHandler
	public void PlayerInteractEvent_(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block clickedBlock = e.getClickedBlock();

		if (clickedBlock != null && e.getClickedBlock().getType() == Material.CHEST) {
			for (KoTH koths : KoTH.kothLoaded.values()) {
				Location kothChestLocation = koths.getChestLocation();
				if (clickedBlock.getLocation().getBlockX() == kothChestLocation.getBlockX()
						&& clickedBlock.getLocation().getBlockY() == kothChestLocation.getBlockY()
						&& clickedBlock.getLocation().getBlockZ() == kothChestLocation.getBlockZ()) {
					Faction playerFaction = FPlayers.i.get(player).getFaction();
					if(!(KoTH.kothWinner.containsKey(koths.getName()) && KoTH.kothWinner.get(koths.getName()) == playerFaction)){
						e.setCancelled(true);
						player.sendMessage(Constants.KOTH_PREFIX+" §cVous ne pouvez pas ouvrir le coffre !");
					} else {
						Core.kothPlayers.get(player).inChest = true;
					}
					break;
				}
			}
		}

		ItemStack itemStackInHand = player.getItemInHand();

		if (itemStackInHand != null && itemStackInHand.hasItemMeta() && itemStackInHand.getItemMeta().hasDisplayName()
				&& itemStackInHand.getItemMeta().getDisplayName().equals(Constants.WAND_NAME)) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
				Block block = e.getClickedBlock();

				if (!blockSelected.containsKey(player)) {
					blockSelected.put(player, new Location[2]);
				}

				blockSelected.get(player)[1] = block.getLocation();

				player.sendMessage(Constants.KOTH_PREFIX + " §6Vous avez sélectionner le point n°§e2§6.");
			}
		}
	}

	public HashMap<Player, Location> lastPlayerLocation = new HashMap<>();

	@EventHandler
	public void PlayerMoveEvent_(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		KoTHPlayer kothPlayer = Core.kothPlayers.get(player);

		if (lastPlayerLocation.containsKey(player)) {
			Location lastLocation = lastPlayerLocation.get(player);
			if (lastLocation.getBlockX() != e.getTo().getBlockX() || lastLocation.getBlockY() != e.getTo().getBlockY()
					|| lastLocation.getBlockZ() != e.getTo().getBlockZ()) {
				boolean inKothZone = false;
				for (KoTH koths : KoTH.kothLoaded.values()) {
					if (new Cuboid(koths.getLocations()[0].getLocation(), koths.getLocations()[1].getLocation())
							.isInCube(e.getTo())) {
						inKothZone = true;
						if (kothPlayer.inKothZone == null) {
							kothPlayer.inKothZone = koths.getName();
						}

						if (!kothPlayer.inKothZone.equals(koths.getName())) {
							kothPlayer.inKothZone = koths.getName();
						}

						koths.enterZone(player);
						break;
					}
				}

				if (!inKothZone) {
					String kothName = kothPlayer.inKothZone;
					if (kothName != null) {
						KoTH koth = KoTH.getKoTHLoaded(kothName);
						if (koth != null) {
							koth.exitZone(player);
						}
					}
					kothPlayer.inKothZone = null;
				}
			}
			lastPlayerLocation.put(player, e.getTo());
		} else {
			lastPlayerLocation.put(player, e.getTo());
		}
	}
}
