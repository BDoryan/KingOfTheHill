package doryan.koth.fr.utils.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import doryan.koth.fr.Core;

public class JsonItem {
	
	private Material material;
	
	private int amount;
	private short data;
	
	private String displayName;
	
	private Map<String, Integer> enchantments;
	private List<String> lore;
	
	public JsonItem(ItemStack itemStack) {
		this.material = itemStack.getType();
		this.data = itemStack.getData().getData();
		this.amount = itemStack.getAmount();
		this.enchantments = new HashMap<>();
		
		if(itemStack.hasItemMeta()){
			ItemMeta itemMeta = itemStack.getItemMeta();
			
			if(itemMeta.hasDisplayName()){
				this.displayName = itemMeta.getDisplayName();
			}
			
			if(itemMeta.getEnchants() != null){
				for(Entry<Enchantment, Integer> entrys : itemMeta.getEnchants().entrySet()){
					this.enchantments.put(entrys.getKey().getName(), entrys.getValue());
				}
			}
			
			if(itemMeta.hasLore()){
				this.lore = itemMeta.getLore();
			}
			
			itemStack.setItemMeta(itemMeta);
		}
	}
	
	public ItemStack getItemStack(){
		ItemStack itemStack = new ItemStack(this.material, this.amount, this.data);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemMeta.setLore(lore);
		
		for(Entry<String, Integer> entrys : this.enchantments.entrySet()){
			itemMeta.addEnchant(Enchantment.getByName(entrys.getKey()), entrys.getValue(), true);
		}
	
		itemStack.setItemMeta(itemMeta);
		
		return itemStack;
	}
	
	public String toJson(){
		return Core.gson.toJson(this);
	}
	
	public static JsonItem fromJsonHDVItem(String hdvItem){
		return Core.gson.fromJson(hdvItem, JsonItem.class);
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public int getAmout() {
		return amount;
	}
	
	public void setAmout(int amout) {
		this.amount = amout;
	}
	
	public short getData() {
		return data;
	}
	
	public void setData(short data) {
		this.data = data;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Map<String, Integer> getEnchantments() {
		return enchantments;
	}
	
	public void setEnchantments(Map<String, Integer> enchantments) {
		this.enchantments = enchantments;
	}
	
	public List<String> getLore() {
		return lore;
	}
	
	public void setLore(List<String> lore) {
		this.lore = lore;
	}
}

