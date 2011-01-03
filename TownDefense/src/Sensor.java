/**
 * 
 * @author opatut
 *
 */
public class Sensor {
	/**
	 * Number of blocks behind sensor block where living entities are 
	 * triggered; maximum value specified in properties file by 
	 * the key "max-length".
	 */
	public int mLength;
	
	/**
	 * Duration the power is kept when triggered: 
	 * -2: until right click on sign
	 * -1: until triggered next time
	 * 0: (default) as long as triggered
	 * > 0: duration in seconds.
	 */
	public int mDuration = -1;
	
	/**
	 * Location of the sign post defining the sensor.
	 */
	public Location mSignLocation;
	
	
	// PRIVATE MEMBERS
	private int mDurationLeft;
	
	private boolean mEnabled = false;
	private boolean mWasEnabledLastTime = false;
	private boolean mWasEnabledLastTrigger = false;
	
	/**
	 * Constructor without extra options
	 * @param length
	 * @param duration
	 */
	public Sensor(Location sign_location, int length, int duration) {
		mSignLocation = sign_location;
		mLength = length;
		mDuration = duration;
		mEnabled = false;
	}
	
	public void Update() {
		boolean t = IsTriggered();
		mEnabled = IsTriggered();
		/*
		if (t) {
			if (mDuration == -1) {
				if (!mWasEnabledLastTime) {
					// we triggered just now, toggle output
					mEnabled = !mWasEnabledLastTrigger;
					mWasEnabledLastTrigger = mEnabled;
				}
			} else if (mDuration > 0) {
				mDurationLeft = mDuration;
				mEnabled = true;
			} else {
				mEnabled = true;
			}
				
		} else if (mDuration == 0) {
			mEnabled = false;
		}
		
		if (mEnabled && mDuration > 0 && mDurationLeft <= 0)
			mEnabled = false;
		*/
		SetOutput(mEnabled);
		
		mWasEnabledLastTime = mEnabled;
	}
	
	public void OnRightClickSign() {
		if (mDuration == -2 && mEnabled)
			mEnabled = false;
	}
	
	public boolean IsEnabled() {
		return mEnabled; 
	}
	
	public boolean IsTriggered() {
		for(LivingEntity e:etc.getServer().getLivingEntityList()) {
			double x = e.getX();
			double y = e.getY();
			double z = e.getZ();
			
			double width = 1;
			double height = 1;
			double length = mLength;
			
			// entity size
			double e_w = 1;
			double e_l = 1;
			double e_h = 2;
			
			
			
			
			
		}
		return true;
	}
	
	private void SetOutput(boolean on) {
		int lever_x = (int)mSignLocation.x;
		int lever_y = (int)mSignLocation.y;
		int lever_z = (int)mSignLocation.z;
		
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
}
