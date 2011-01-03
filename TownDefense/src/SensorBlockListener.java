import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
	
	PropertiesFile mPropertiesFile;

	public SensorBlockListener(TownDefense plugin, String properties_file) {
		p = plugin;
		mPropertiesFile = new PropertiesFile(properties_file);
	}
	
	public void Save() {
		// clear properties file
		for(int i=0; i < mSensorList.size(); ++i) {
			mPropertiesFile.setString(i, mSensorList.get(i).serialize());
		}
	}

	public void onTick() {
		for (Sensor s : mSensorList) {
			s.Update();
		}
	}

	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		Sensor s = GetSensorAt(new Location(blockClicked.getX(),
				blockClicked.getY(), blockClicked.getZ()));
		if (s != null) {
			s.OnRightClickSign();
		}
	}

	private Sensor GetSensorAt(Location loc) {
		for (Sensor s : mSensorList) {
			if ((int) s.mSignLocation.x == (int) loc.x
					&& (int) s.mSignLocation.y == (int) loc.y
					&& (int) s.mSignLocation.z == (int) loc.z) {
				return s;
			}
		}
		return null;
	}

	public boolean onBlockBreak(Player player, Block block) {
		if (block.blockType == Block.Type.WallSign) {
			// remove sensor with that sign location
			Sensor s = GetSensorAt(new Location(block.getX(), block.getY(),block.getZ()));
			if (s != null) {
				mSensorList.remove(s);
				player.sendMessage(mMessagePrefix + "You removed a sensor.");
			}
		}
		return false;
	}

	public boolean onSignChange(Player player, Sign sign) {
		int max_length = 10;

		String[] line0split = sign.getText(0).split(" ");
		if (line0split.length >= 1 && line0split.length <= 3
				&& line0split[0].equalsIgnoreCase("[SENSOR]")) {
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

				Sensor.SignOrientation o = Sensor.SignOrientation.SOUTH;
				int data = sign.getBlock().getData();
				if (data == 0x2)
					o = Sensor.SignOrientation.EAST;
				else if (data == 0x3)
					o = Sensor.SignOrientation.WEST;
				else if (data == 0x4)
					o = Sensor.SignOrientation.NORTH;
				else if (data == 0x5)
					o = Sensor.SignOrientation.SOUTH;
				else {
					Logger.getLogger("Minecraft").severe(
							"Invalid sign data: " + data);
					throw new RuntimeException("Invalid sign data.");
				}
				String extra = sign.getText(1) + sign.getText(2)
						+ sign.getText(3);
				Sensor s = new Sensor(new Location(sign.getX(), sign.getY(),
						sign.getZ()), o, length, duration, extra);
				mSensorList.add(s);

				player.sendMessage(mMessagePrefix + "You created a sensor.");
				if (etc.getServer().getBlockAt(sign.getX(), sign.getY() - 1,
						sign.getZ()).blockType != Block.Type.Lever) {
					player.sendMessage(mMessagePrefix + "Now place a §fLever "
							+ mMessageColor + "underneath the sign post.");
				}
				sign.setText(0, "[SENSOR] " + length + " " + duration);
				sign.update();
			} catch (NumberFormatException e) {
				player.sendMessage(mMessagePrefix
						+ "Invalid sign format. See §f/towndefense help sensor "
						+ mMessageColor + " for help.");
			}
		}
		return false;
	}
}