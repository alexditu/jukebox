package sac.juke.model;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

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
    	}
		
		return sb.toString();
	}
	
	/**
	 * @return	JsonArray of User objects: [u1, u2..]
	 */
	public synchronized JsonArray toJson() {
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
    	
    	for (String i : users.keySet()) {
    		jsonBuilder.add(users.get(i).toJson());
    	}
    	
    	return jsonBuilder.build();
	}
}
