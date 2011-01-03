import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author opatut
 *
 */
public class GroupSpawnListener extends PluginListener {
	GroupSpawn p;
	SpawnPointList list = new SpawnPointList("groupspawns.txt");
	List<String> ignorePlayers = new ArrayList<String>();
	List<String> deadPlayersToTeleport = new ArrayList<String>();

	public GroupSpawnListener(GroupSpawn plugin) {
		p = plugin;
	}
	
	public boolean onTeleport(Player player, Location from,Location to) {
		if (deadPlayersToTeleport.contains(player.getName())) {
			player.sendMessage("# Teleporting respawning player.");
			deadPlayersToTeleport.remove(player.getName());
			player.sendMessage("- deadPlayersToTeleport");
			playerSpawn(player);
			return true;
		}
		player.sendMessage("# Normal Teleporting.");
		return false;
	}
	
	public Location getDefaultSpawn() {
		PropertiesFile f = new PropertiesFile(list.filename);
		return new SpawnPoint("default", 
				f.getString("default-spawn", 
						new SpawnPoint("default", etc.getServer().getSpawnLocation()).getLocationString())).location;
	}

	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		p.broadcast(player.getName() + ": " + oldValue + " > " + newValue);
		if (oldValue <= 0 && newValue == 20 && !ignorePlayers.contains(player.getName())) {
			// player will be teleported to spawn soon !!!
			deadPlayersToTeleport.add(player.getName());
			player.sendMessage("+ deadPlayersToTeleport");
			player.teleportTo(0, 0, 0, 0, 0); // trigger onTeleport
		} else if (ignorePlayers.contains(player.getName())) {
			ignorePlayers.remove(player.getName());
		}
		return false;
	}
	
	private void playerSpawn(Player player) {
		player.sendMessage("§e# playerSpawn()");
		for(SpawnPoint point:list.spawnPoints) {
			if (player.isInGroup(point.group)) {				
				String m = "";
				int x = (int) Math.floor(point.location.x);
				int y = (int) Math.floor(point.location.y);
				int z = (int) Math.floor(point.location.z);
				if(etc.getServer().getBlockAt(x,y,z).blockType != Block.Type.Air ||
						etc.getServer().getBlockAt(x,y+1,z).blockType != Block.Type.Air) {
					m = p.properties.getString("invalid-spawn-message", 
							Colors.Red + "*** The spawn of the group <group> is blocked or " +
							"invalid. Please contact a mod or admin. ***");
					m = m.replaceAll("<group>", point.group);
					if (m.length() > 0) 
						player.sendMessage(m);
				} else {
					m = p.properties.getString("group-spawn-message", 
							Colors.Rose + "* You spawn at the base of the group <group>.");
					m = m.replaceAll("<group>", point.group);
					if (m.length() > 0) 
						player.sendMessage(m);
					player.teleportTo(point.location);
					return;
				}
			}
		}
		String m = p.properties.getString("default-spawn-message", Colors.Rose + "You spawn at the default spawn.");
		if (m.length() > 0) player.sendMessage(m);
		Location l = getDefaultSpawn();
		player.teleportTo(getDefaultSpawn());
		player.sendMessage("§e# Default spawn: " +l.x + " / " + l.y + " / " + l.z);
	}

	public boolean onCommand(Player player, String[] split) {
		if (split[0].equals("/setgroupspawn") && player.canUseCommand("/setgroupspawn")) {
			if (split.length == 2 && split[1]!="") {
				SpawnPoint p = new SpawnPoint(split[1], player.getLocation());
				list.setPoint(p);
				player.sendMessage(Colors.Rose + "Group spawn for " + split[1] + " set.");
			} else {
				player.sendMessage(Colors.Rose + "Usage: /setgroupspawn <group>");
			}
			return true;
		} else if (split[0].equals("/delgroupspawn") && player.canUseCommand("/delgroupspawn")) {
			if (split.length == 2 && split[1]!="") {
				list.delPoint(split[1]);
				player.sendMessage(Colors.Rose + "Group spawn for " + split[1] + " removed.");
			} else {
				player.sendMessage(Colors.Rose + "Usage: /delgroupspawn <group>");
			}
			return true;
		} else if (split[0].equals("/spawn") && player.canUseCommand("/spawn")) {
			playerSpawn(player);
			return true;
		} else if (split[0].equals("/digdown")) {
			etc.getServer().setBlockAt(0, 
					(int)player.getLocation().x, 
					(int)player.getLocation().y - 1, 
					(int)player.getLocation().z);
		} else if (split[0].equals("/setspawn")) {
			SpawnPoint p = new SpawnPoint("default-spawn", player.getLocation());
			list.setPoint(p);
			player.sendMessage(Colors.Rose + "Default spawn set.");
			return true;
		}
		return false;
	}
	
	public void onLogin(Player player) {
		ignorePlayers.add(player.getName());
	}
}