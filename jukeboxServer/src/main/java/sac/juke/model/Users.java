package sac.juke.model;

import java.util.HashMap;

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
	
	public synchronized void add(String userId, User user) throws UserExistsException {
		
		if (users.containsKey(userId)) {
			log.debug("User already exists, throwing exception");
			throw new UserExistsException("User already exists, try other username!");
		} else {
			users.put(userId, user);
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
