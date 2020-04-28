package doryan.koth.fr.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cuboid {

	private String worldName;
	
	private int[] location1;
	private int[] location2;

	private int maxX;
	private int maxY;
	private int maxZ;

	private int minX;	
	private int minY;
	private int minZ;

	public Cuboid(Location l, Location l2) {
		this.location1 = new int[] {l.getBlockX(), l.getBlockY(), l.getBlockZ()};
		this.location2 = new int[] {l2.getBlockX(), l2.getBlockY(), l2.getBlockZ()};
		if (l.getWorld().getName().equalsIgnoreCase(l2.getWorld().getName())) {
			this.worldName = l.getWorld().getName();
			if (l.getBlockX() > l2.getBlockX()) {
				maxX = l.getBlockX();
				minX = l2.getBlockX();
			} else {
				maxX = l2.getBlockX();
				minX = l.getBlockX();
			}
			if (l.getBlockY() > l2.getBlockY()) {
				maxY = l.getBlockY();
				minY = l2.getBlockY();
			} else {
				maxY = l2.getBlockY();
				minY = l.getBlockY();
			}
			if (l.getBlockZ() > l2.getBlockZ()) {
				maxZ = l.getBlockZ();
				minZ = l2.getBlockZ();
			} else {
				maxZ = l2.getBlockZ();
				minZ = l.getBlockZ();
			}
		}
	}

	public Location getLocation2() {
		return new Location(Bukkit.getWorld(worldName), location2[0],location2[1], location2[2]);
	}

	public Location getLocation1() {
		return new Location(Bukkit.getWorld(worldName), location1[0],location1[1], location1[2]);
	}

	public boolean isInCube(Location location) {
		Block b = location.getBlock();
		if ((b.getX() <= maxX) && (b.getX() >= minX) && (b.getY() <= maxY) && (b.getY() >= minY) && (b.getZ() <= maxZ)
				&& (b.getZ() >= minZ)) {
			return true;
		}
		return false;
	}
	
	public World getWorld() {
		return Bukkit.getWorld(this.worldName);
	}
	
	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMaxZ() {
		return maxZ;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMinZ() {
		return minZ;
	}

	public int getHeight() {
		return maxY - minY;
	}
	
	public int getWidth() {
		return maxX - minX;
	}
	public int getLenght() {
		return maxZ - minZ;
	}
}