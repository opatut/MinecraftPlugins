import java.util.logging.Logger;

/**
 * 
 * @author opatut
 */
public class TownDefense extends Plugin {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private String name = "TownDefense";
	private String version = "0.1 - Alpha";
	private String author = "opatut";

	// PluginListeners
	private SensorBlockListener mSensorBlockListener = new SensorBlockListener(
			this);

	public void enable() {
	}

	public void disable() {
	}

	public void initialize() {
		log.info(name + " " + version + " by " + author + " initialized");
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE,
				mSensorBlockListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED,
				mSensorBlockListener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN,
				mSensorBlockListener, this, PluginListener.Priority.MEDIUM);

		// Tick Patch
		TickPatch.applyPatch();
		Runnable onTick = new Runnable() {
			public void run() {
				mSensorBlockListener.onTick();
			}
		};
		TickPatch.addTask(TickPatch.wrapRunnable(this, onTick));
	}

	public static void Broadcast(String message) {
		for (Player p : etc.getServer().getPlayerList()) {
			p.sendMessage(message);
		}
	}

}