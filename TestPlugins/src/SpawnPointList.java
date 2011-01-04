import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;


/**
 * 
 * @author opatut
 *
 */

public class SpawnPointList {
	public List<SpawnPoint> spawnPoints = new ArrayList<SpawnPoint>();
	public String filename;
	
	public SpawnPointList(String file) {
		filename = file;
		LoadFromFile();
	}
	
	public void LoadFromFile() {
		spawnPoints.clear();
		PropertiesFile f = new PropertiesFile(filename);
		try {
			for(Entry<String, String> entry: f.returnMap().entrySet()) {
				SpawnPoint p = new SpawnPoint(entry.getKey(), entry.getValue());
				spawnPoints.add(p);
			}
		} catch (Exception e) {
			Logger.getLogger("Minecraft").severe("Key/Value Map error in SpawnPointList.");
			e.printStackTrace();
		}
	}
	
	public void SaveToFile() {
		PropertiesFile f = new PropertiesFile(filename);
		for (SpawnPoint p: spawnPoints) {
			f.setString(p.getGroup(), p.getLocationString());
		}
		f.save();
	}
	
	public void setPoint(SpawnPoint p) {
		for(SpawnPoint p2: spawnPoints) {
			if(p2.getGroup() == p.getGroup()) {
				p2.location = p.location;
				SaveToFile();
				LoadFromFile();
				return;
			}
		}
		spawnPoints.add(p);
		SaveToFile();
		LoadFromFile();
	}
	
	public boolean delPoint(String grp) {
		for(SpawnPoint p: spawnPoints) {
			if(p.getGroup().equalsIgnoreCase(grp)) {
				spawnPoints.remove(p);
				//save
				PropertiesFile f = new PropertiesFile(filename);
				f.removeKey(p.getGroup());
				f.save();
				LoadFromFile();
				return true;
			}
		}
		return false;
	}
	
}
