/**
 * 
 * @author opatut
 *
 */
public class FastFoodListener extends PluginListener {
	FastFood p;

	public FastFoodListener(FastFood plugin) {
		p = plugin;
	}
	
	public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item){
		if (item.itemType == Item.Type.Apple ||
				item.itemType == Item.Type.Bread ||
				item.itemType == Item.Type.MushroomSoup ||
				item.itemType == Item.Type.GoldenApple  ||
				item.itemType == Item.Type.CookedFish ||
				item.itemType == Item.Type.RawFish ||
				item.itemType == Item.Type.GrilledPork ||
				item.itemType == Item.Type.Pork) {
			//Inventory inv = player.getInventory();
		}
		return false;
	}

}