import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author opatut
 * 
 */
public class SensorBlock extends CustomBlock {
	public enum SignOrientation {
		NORTH, EAST, SOUTH, WEST;

		public static SignOrientation FromId(int id) {
			switch (id) {
			case 0:
				return NORTH;
			case 1:
				return EAST;
			case 2:
				return SOUTH;
			case 3:
				return WEST;
			default:
				return null;
			}
		}
		public int ToId() {
			switch (this) {
			case NORTH:
				return 1;
			case EAST:
				return 2;
			case SOUTH:
				return 3;
			case WEST:
				return 4;
				default:
					return 0;
			}
		}
	}

	/**
	 * Remember sign post orientation for sensor direction.
	 */
	public SignOrientation mSignOrientation;

	/**
	 * Number of blocks behind sensor block where living entities are triggered;
	 * maximum value specified in properties file by the key "max-length".
	 */
	public int mLength;

	/**
	 * Duration the power is kept when triggered: -2: until right click on sign
	 * -1: until triggered next time 0: (default) as long as triggered > 0:
	 * duration in seconds.
	 */
	public int mDuration = 0;

	/**
	 * Location of the sign post defining the sensor.
	 */
	public Location mSignLocation;

	// PRIVATE MEMBERS
	private int mTicksLeft = 0;
	private boolean mEnabled = false;
	private boolean mWasTriggeredLastTick = false;
	private String mExtra = ""; // keep for serialization
	
	private List<SensorTriggerCondition> mConditions = new ArrayList<SensorTriggerCondition>();

	public SensorBlock() {
		mSignLocation = new Location();
		mSignOrientation = SignOrientation.NORTH;
		mLength = 0;
		mDuration = 0;
	}
	public SensorBlock(Location sign_location, SignOrientation sign_orientation,
			int length, int duration, String extra) {
		mSignLocation = sign_location;
		mSignOrientation = sign_orientation;
		mLength = length;
		mDuration = duration;

		CreateConditionsFromString(extra);
	}
	
	public void CreateConditionsFromString(String extra) {
		mConditions.clear();
		
		// set default extra value
		if (extra == "")
			extra = "*";

		mExtra = extra;
		
		// split extra at conditions
		String[] conditions = extra.split(" ");
		for (String condition:conditions) {
			mConditions.add(new SensorTriggerCondition(condition));
		}
	}

	public void Update() {
		if (mTicksLeft > 0)
			mTicksLeft--;
		boolean t = IsTriggered();
		if (mDuration == 0) {
			// enabled when triggered
			mEnabled = t;
		} else if (mDuration == -1) {
			// toggle on trigger
			if (t && !mWasTriggeredLastTick) {
				// sensor was triggered this tick, toggle now
				mEnabled = !mEnabled;
			}
		} else if (mDuration == -2) {
			// reset by right click (see OnRightClickSign())
			if (t)
				mEnabled = true;
		} else if (mDuration > 0) {
			// timer
			if (t) {
				mTicksLeft = mDuration * 20;

			}
			mEnabled = mTicksLeft > 0;
		}

		SetOutput(mEnabled);

		mWasTriggeredLastTick = t;
	}

	public void OnRightClickSign() {
		if (mDuration == -2 && mEnabled)
			mEnabled = false;
	}

	public boolean IsEnabled() {
		return mEnabled;
	}

	public boolean IsTriggered() {
		for (LivingEntity e : etc.getServer().getLivingEntityList()) {
			if (!IsTriggeringEntity(e))
				continue;
			
			double x = e.getX();
			double y = e.getY();
			double z = e.getZ();

			double width = 1;
			double height = 1;
			double length = mLength;

			// entity size
			double e_s = 1;
			double e_h = 2;

			double minx, maxx, miny, maxy, minz, maxz;
			minx = maxx = miny = maxy = minz = maxz = 0;

			miny = mSignLocation.y - e_h;
			maxy = mSignLocation.y + height;

			if (mSignOrientation == SignOrientation.NORTH) {
				// +x
				minx = mSignLocation.x + 2 - e_s / 2.0;
				maxx = mSignLocation.x + 2 + length + e_s / 2.0;
				minz = mSignLocation.z + 0.5 - width / 2.0 - e_s / 2.0;
				maxz = mSignLocation.z + 0.5 + width / 2.0 + e_s / 2.0;
			} else if (mSignOrientation == SignOrientation.SOUTH) {
				// -x
				maxx = mSignLocation.x - 1 + e_s / 2.0;
				minx = mSignLocation.x - 1 - length - e_s / 2.0;
				minz = mSignLocation.z + 0.5 - width / 2.0 - e_s / 2.0;
				maxz = mSignLocation.z + 0.5 + width / 2.0 + e_s / 2.0;
			} else if (mSignOrientation == SignOrientation.EAST) {
				// +z
				minx = mSignLocation.x + 0.5 - width / 2.0 - e_s / 2.0;
				maxx = mSignLocation.x + 0.5 + width / 2.0 + e_s / 2.0;
				minz = mSignLocation.z + 2 - e_s / 2.0;
				maxz = mSignLocation.z + 2 + length + e_s / 2.0;
			} else if (mSignOrientation == SignOrientation.WEST) {
				// -z
				minx = mSignLocation.x + 0.5 - width / 2.0 - e_s / 2.0;
				maxx = mSignLocation.x + 0.5 + width / 2.0 + e_s / 2.0;
				maxz = mSignLocation.z - 1 + e_s / 2.0;
				minz = mSignLocation.z - 1 - length - e_s / 2.0;
			}

			if (etc.getServer().getTime() % 20 == 0 && e.isPlayer()) {
				// e.getPlayer().sendMessage("---------------------");
			}

			if (x >= minx && x <= maxx && y >= miny && y < maxy && z >= minz
					&& z <= maxz) {
				return true;
			}
		}
		return false;
	}

	public boolean IsTriggeringEntity(LivingEntity e) {
		boolean found_one = false;
		for (SensorTriggerCondition c:mConditions) {
			SensorTriggerCondition.ScanResult r = c.ScanFor(e);
			if (r == SensorTriggerCondition.ScanResult.RETURN_FALSE) {
				return false;
			} else if (r == SensorTriggerCondition.ScanResult.CONTINUE_FALSE) {
				continue;
			} else if (r == SensorTriggerCondition.ScanResult.CONTINUE_TRUE) {
				found_one = true;
				continue;
			}
			
		}
		return found_one;
	}
	
	public boolean IsTriggeringAny() {
		return mConditions.size() == 1 && 
			mConditions.get(0).mType == SensorTriggerCondition.ConditionType.ANY;
	}

	private void SetOutput(boolean on) {
		int lever_x = (int) mSignLocation.x;
		int lever_y = (int) mSignLocation.y - 1;
		int lever_z = (int) mSignLocation.z;

		Block lever = etc.getServer().getBlockAt(lever_x, lever_y, lever_z);
		if (lever.blockType == Block.Type.Lever) {
			// 0x8 = 1 0 0 0
			// 0x7 = 0 1 1 1
			if (on) {
				lever.setData(lever.getData() | 0x8); // set the bit to 1
			} else {
				lever.setData(lever.getData() & 0x7); // set the bit to 0
			}

			// save the changes
			etc.getServer().setBlock(lever);
			etc.getServer().updateBlockPhysics(lever);
		}

	}

	public String serialize() {
		// x:y:z:orientation:length:duration:extra
		return mSignLocation.x + ":" +
			mSignLocation.y + ":" +
			mSignLocation.z + ":" +
			mSignOrientation.ToId() + ":" +
			mLength + ":" +
			mDuration + ":" +
			mExtra;
	}
	
	public SensorBlock deserialize(String string) {
		try {
			String[] s = string.split(":",7);		
			int x = new Integer(s[0]);
			int y = new Integer(s[1]);
			int z = new Integer(s[2]);
			if (etc.getServer().getBlockAt(x, y, z).blockType != Block.Type.WallSign)
				return null;
			
			Location sign_location = new Location(x,y,z);
			SignOrientation sign_orientation = SignOrientation.FromId(new Integer(s[3]));
			int length = new Integer(s[2]);
			int duration = new Integer(s[5]);
			return new SensorBlock(sign_location, sign_orientation, length, duration, s[6]);
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
