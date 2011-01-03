

import java.util.logging.Logger;

/**
 * 
 * @author opatut
 *
 */

public class SpawnPoint {
	Location location = new Location();
	String group = "";
	
	public SpawnPoint(String grp, String loc) {
		//parse line
		String[] s = loc.split(":");
		if (s.length != 5) {
			Logger.getLogger("Minecraft").severe("Invalid Spawn Point Location String.");
		} else {
			try {
				SetLocation(new Location(
						new Double(s[0]),
						new Double(s[1]),
						new Double(s[2]),
						new Float(s[3]),
						new Float(s[4])));
			} catch (NumberFormatException e) {
				Logger.getLogger("Minecraft").severe("Invalid Spawn Point Location String.");
			}
		}
		group = grp;
	}
	
	public SpawnPoint(String grp, Location loc) {
		SetLocation(loc);
		group = grp;
	}
	
	public void SetLocation(Location l) {
		location.x = Math.floor(l.x) + 0.5;
		location.y = Math.floor(l.y) + 0.0;
		location.z = Math.floor(l.z) + 0.5;
		location.rotX = Math.round(l.rotX);
		location.rotY = Math.round(l.rotY);
	}
	
	public String getLocationString() {
		String s = location.x+":"+location.y+":"+location.z+":"+location.rotX+":"+location.rotY;
		Logger.getLogger("Minecraft").info("LocString: " +s );
		return s;
	}
}
