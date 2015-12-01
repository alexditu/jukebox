package sac.juke.model;

import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	String username;
	ArrayList<String> votedSongs;
	
	public User() {
		this.votedSongs = new ArrayList<>();
	}
	
	public User(String username) {
		this();
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void vote(String id) {
		votedSongs.add(id);
	}
	
	public void unvote(String id) {
		votedSongs.remove(id);
	}
	
	@Override
	public String toString() {
		return username;
	}
	
	public boolean hasVoted(String songId) {
		return votedSongs.contains(songId);
	}
	
	/**
	 * @return	{"username": username, "votedSongs": ["s1", "s2", ..]}
	 */
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("username", username);
		
		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		for (String i : votedSongs) {
			arrBuilder.add(i);
		}
		builder.add("votedSongs", arrBuilder.build());
		
		return builder.build();
	}
}
