import java.util.logging.Logger;
/**
*
* @author opatut
*/

public class FastFood extends Plugin {
	
	private FastFoodListener l = new FastFoodListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	//public PropertiesFile properties = new PropertiesFile("fastfood.txt");
	private String name = "FastFood";
	private String version = "0.1 - Alpha";
	private String author = "opatut";

	public void enable() {}
	public void disable() {}

	public void initialize() {
		log.info(name + " " + version + " initialized. Author: " + author);
		//etc.getLoader().addListener( PluginLoader.Hook.HEALTH_CHANGE, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( PluginLoader.Hook.ITEM_USE, l, this, PluginListener.Priority.MEDIUM);
	}

	// Sends a message to all players!
	public void broadcast(String message) {
		for (Player p : etc.getServer().getPlayerList()) {
			p.sendMessage(message);
		}
	}
	
}