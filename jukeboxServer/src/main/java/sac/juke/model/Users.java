package sac.juke.model;

import java.util.HashMap;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sac.juke.exceptions.UserExistsException;

/**
 * Keeps track of users accessing the page.
 * It is a shared data so all methods should be synchronized!
 * @author alex
 *
 */
public class Users {
	private final static Logger log = LogManager.getLogger(Users.class);
	
	private HashMap<String, User> users;
	
	public Users() {
		this.users = new HashMap<>();
	}
	
	public Users(boolean addDefaultUsers) {
		this();
		if (addDefaultUsers == true) {
			users.put("John", new User("John"));
			users.put("Bob", new User("Bob"));
			users.put("Alice", new User("Alice"));
		}
	}
	
	public synchronized User get(String userId) {
		return users.get(userId);
	}
	
	public synchronized Set<String> getUsersKeySet() {
		return this.users.keySet();
	}
	
	public synchronized void add(String userId, User user) throws UserExistsException {
		
		if (users.containsKey(userId)) {
			log.debug("User already exists, throwing exception");
			throw new UserExistsException("User already exists, try other username!");
		} else {
			users.put(userId, user);
		}
	}
	
	public synchronized void remove(String userId) {
		
		if (users.containsKey(userId)) {
			log.debug("User " + userId + " exists, removing");
			users.remove(userId);
		}
	}
	
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		for (String i : users.keySet()) {
			sb.append(users.get(i).toString());
			sb.append(" ");
    	}
		
		return sb.toString();
	}
	
	public synchronized void follow(String from, String to) {
		User source = this.get(from);
		
		source.followUser(to);
	}
	
	public synchronized void updateState() {
		/* update voting power */
		for (String u : this.getUsersKeySet()) {
			User currentUser = this.get(u);
			currentUser.setVotingPower(currentUser.getBasePower());
		}
		for (String u : this.getUsersKeySet()) {
			User currentUser = this.get(u);
			currentUser.setVotingPower(currentUser.getBasePower());
			if (currentUser.getVotedSongs().isEmpty()) {
				for (String followed : currentUser.getFollowedUsers()) {
					log.debug("Updating " + u + "with " + followed);
					User followedUser = this.get(followed);
					followedUser.setVotingPower(followedUser.getVotingPower() + 
												currentUser.getTransferrablePower());
				}
			}
		}
		
		/* update song scores */
		for (String u : this.getUsersKeySet()) {
			User currentUser = this.get(u);
			currentUser.updateVotedSongs();
		}
	}
	
	public void flushVotedSongs() {
		for (User u : users.values()) {
			u.flushSongs();
		}
	}
	
	public void updateVotingPower(String nextSong) {
		for (User u : users.values()) {
			HashMap<String, Integer> votedSongs = u.getVotedSongs();
			if (votedSongs.containsKey(nextSong)) {
				int power = u.getBasePower();
				if (power >= 10) {
					u.setBasePower(power - 10);
				}
			} else {
				int power = u.getVotingPower();
				if (power <= 90) {
					u.setBasePower(power + 10);
				}
			}
		}
	}
	
	/**
	 * @return	JsonArray of User objects: {"users": [u1, u2..]}
	 */
	public synchronized JsonObject toJson() {
		JsonObjectBuilder jUsers = Json.createObjectBuilder();
		JsonArrayBuilder array = Json.createArrayBuilder();
    	
    	for (String i : users.keySet()) {
    		array.add(users.get(i).toJson());
    	}
    	
    	jUsers.add("users", array.build());
    	
    	return jUsers.build();
	}
}
