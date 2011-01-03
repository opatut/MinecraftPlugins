import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author opatut
 *
 */
public class SensorBlockListener extends PluginListener {
	String mMessageColor = Colors.Gold;
	String mMessagePrefix = mMessageColor + "[TOWNDEFENSE] ";
	
	TownDefense p;
	List<Sensor> mSensorList = new ArrayList<Sensor>();

	public SensorBlockListener(TownDefense plugin) {
		p = plugin;
	}
	
	public void onTick() {
		for(Sensor s:mSensorList) {
			s.Update();
		}
	}
	
	private Sensor GetSensorAt(Location loc) {
		for(Sensor s:mSensorList) {
			if ((int)s.mSignLocation.x == (int)loc.x &&
					(int)s.mSignLocation.y == (int)loc.y &&
					(int)s.mSignLocation.z == (int)loc.z) {
				return s;
			}
		}
		return null;
	}
	
	public boolean onBlockBreak(Player player, Block block) {
		if (block.blockType == Block.Type.SignPost) {
			// remove sensor with that sign location
			for(Sensor s:mSensorList) {
				if ((int)s.mSignLocation.x == (int)block.getX() &&
						(int)s.mSignLocation.y == (int)block.getY() &&
						(int)s.mSignLocation.z == (int)block.getZ()) {
					mSensorList.remove(s);
					player.sendMessage(mMessagePrefix + "You removed a sensor.");
				}
			}
		}
		return false;
	}
	
	public boolean onSignChange(Player player, Sign sign) {
		int max_length = 10;
		
		String[] line0split = sign.getText(0).split(" "); 
		if (line0split.length >= 1 &&
				line0split.length <= 3 && 
				line0split[0].equalsIgnoreCase("[SENSOR]")) {
			// interpret sign
			try {
				// read params
				int length = max_length;
				if (line0split.length >= 2) {
					int l = new Integer(line0split[1]);
					if (l < max_length && l > 0) {
						length = l;
					}
				}
				
				int duration = 0;
				if (line0split.length >= 3) {
					int d = new Integer(line0split[2]);
					if (d >= -2)
						duration = d;
				}
				
				Sensor s = new Sensor(new Location(sign.getX(), sign.getY(), sign.getZ()), 
						length, duration);
				mSensorList.add(s);
				
				player.sendMessage(mMessagePrefix + "You created a sensor.");
				if (etc.getServer().getBlockAt(sign.getX(), sign.getY()-1, sign.getZ()).blockType != Block.Type.Lever) {
					player.sendMessage(mMessagePrefix+"Now place a §fLever " + mMessageColor +"underneath the sign post.");
				}
				sign.setText(0, "[SENSOR] " + length + " " + duration);
				sign.update();
			} catch (NumberFormatException e) {
				
			}
		}
		return false;
	}
}