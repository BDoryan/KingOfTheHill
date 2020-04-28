package doryan.koth.fr.utils.koth.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class KoTHLocation {

	public String worldName;
	
	public int x;
	public int y;
	public int z;
	
	public KoTHLocation(String worldName, int x, int y, int z){
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toBukkitString(){
		return "§f"+worldName+"§7, §f"+x+"§7, §f"+y+"§7, §f"+x;
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}
	
	public static KoTHLocation getKoTHLocationToBukkitLocation(Location location){
		return new KoTHLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
}
