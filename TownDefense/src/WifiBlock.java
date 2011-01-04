/**
 * 
 * @author opatut
 * 
 */
public class WifiBlock extends CustomBlock {
	public enum WifiBlockType {
		SENDER, RECEIVER;
		
		public int ToId(){
			switch(this) {
			case SENDER:
				return 1;
			case RECEIVER:
				return 2;
			default:
				return 0;
			}
		}
		public static WifiBlockType FromId(int id) {
			switch(id) {
			case 1:
				return SENDER;
			case 2:
				return RECEIVER;
			default:
				return null;
			}
		}
	}

	public WifiBlockType mType;
	public Location mSignLocation, mBlockLocation;
	public String mNetworkName;

	public WifiBlock(WifiBlockType type, Location sign_location,
			Location block_location, String network_name) {
		mType = type;
		mSignLocation = sign_location;
		mBlockLocation = block_location;
		mNetworkName = network_name;
	}

	@Override
	public String serialize() {
		// type:sx:sy:sz:bx:by:bz:network
		return null;
	}

	@Override
	public CustomBlock deserialize(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
