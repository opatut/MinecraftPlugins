import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author opatut
 * 
 */
public class SensorBlockListener extends BlockListener {
	String mMessageColor = Colors.Gold;
	String mMessagePrefix = mMessageColor + "[TOWNDEFENSE] ";

	TownDefense p;
	List<SensorBlock> mSensorList = new ArrayList<SensorBlock>();
	
	PropertiesFile mPropertiesFile;

	public SensorBlockListener(TownDefense plugin, String properties_file) {
		p = plugin;
		mPropertiesFile = new PropertiesFile(properties_file);
		Load();
	}
	
	public void Save() {
		String numkey = "sign-amount";
		String signprefix = "sign-";
		// clear properties file
		int num = mPropertiesFile.getInt(numkey, 0);
		for (int i = 0; i < num; ++i) {
			mPropertiesFile.removeKey(signprefix + i);
		}
		
		// save
		num = mSensorList.size();
		mPropertiesFile.setInt(numkey, num);
		for(int i=0; i < mSensorList.size(); ++i) {
			mPropertiesFile.setString(signprefix + i, mSensorList.get(i).serialize());
		}
	}
	
	public void Load() {
		String numkey = "sign-amount";
		String signprefix = "sign-";
		// clear list
		mSensorList.clear();
		
		// save
		int num = mPropertiesFile.getInt(numkey, 0);
		for(int i=0; i < num; ++i) {
			SensorBlock s = SensorBlock.deserialize(mPropertiesFile.getString(signprefix + i, ""));
			if(s != null) {
				mSensorList.add(s);
			} else {
				Logger.getLogger("Minecraft").warning("[TOWNDEFENSE] Sensor at line "+
						i+" could not be deserialized or is invalid. Deleting.");
			}
		}
		TownDefense.Broadcast("Loaded "+mSensorList.size()+" of " +num +" Sensors.");
	}

	public void onTick() {
		for (SensorBlock s : mSensorList) {
			s.Update();
		}
	}

	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		SensorBlock s = GetSensorAt(new Location(blockClicked.getX(),
				blockClicked.getY(), blockClicked.getZ()));
		if (s != null) {
			s.OnRightClickSign();
		}
	}

	private SensorBlock GetSensorAt(Location loc) {
		for (SensorBlock s : mSensorList) {
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
			SensorBlock s = GetSensorAt(new Location(block.getX(), block.getY(),block.getZ()));
			if (s != null) {
				mSensorList.remove(s);
				player.sendMessage(mMessagePrefix + "You removed a sensor.");
				Save();
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

				SensorBlock.SignOrientation o = SensorBlock.SignOrientation.SOUTH;
				int data = sign.getBlock().getData();
				if (data == 0x2)
					o = SensorBlock.SignOrientation.EAST;
				else if (data == 0x3)
					o = SensorBlock.SignOrientation.WEST;
				else if (data == 0x4)
					o = SensorBlock.SignOrientation.NORTH;
				else if (data == 0x5)
					o = SensorBlock.SignOrientation.SOUTH;
				else {
					Logger.getLogger("Minecraft").severe(
							"Invalid sign data: " + data);
					throw new RuntimeException("Invalid sign data.");
				}
				String extra = sign.getText(1) + sign.getText(2)
						+ sign.getText(3);
				SensorBlock s = new SensorBlock(new Location(sign.getX(), sign.getY(),
						sign.getZ()), o, length, duration, extra);
				mSensorList.add(s);

				player.sendMessage(mMessagePrefix + "You created a sensor.");
				if (etc.getServer().getBlockAt(sign.getX(), sign.getY() - 1,
						sign.getZ()).blockType != Block.Type.Lever) {
					player.sendMessage(mMessagePrefix + "Now place a §fLever "
							+ mMessageColor + "underneath the sign post.");
				}
				sign.setText(0, "[SENSOR] " + length + " " + duration);
				if (s.IsTriggeringAny())
					sign.setText(1, "*");
				sign.update();
				Save();
			} catch (NumberFormatException e) {
				player.sendMessage(mMessagePrefix
						+ "Invalid sign format. See §f/towndefense help sensor "
						+ mMessageColor + " for help.");
			}
		}
		return false;
	}
}