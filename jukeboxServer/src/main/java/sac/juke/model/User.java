package sac.juke.model;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	String username;
	HashMap<String, Integer> votedSongs;
	int votingPower;
	
	public User() {
		this.votedSongs = new HashMap<>();
		this.votingPower = 100;
	}
	
	public User(String username) {
		this();
		this.username = username;
		this.votingPower = 100;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void flushSongs() {
		votedSongs.clear();
	}
	
	public HashMap<String, Integer> getVotedSongs() {
		return this.votedSongs;
	}
	
	public void vote(String id) {
		int newScore = this.votingPower / (this.votedSongs.size() + 1);
		this.votedSongs.put(id, newScore);
		for (String key : this.votedSongs.keySet()) {
			this.votedSongs.put(key, newScore);
		}
	}
	
	public void unvote(String id) {
		votedSongs.remove(id);
		if (this.votedSongs.size() == 0) {
			return;
		} else {
			int newScore = this.votingPower / this.votedSongs.size();
			for (String key : this.votedSongs.keySet()) {
				this.votedSongs.put(key, newScore);
			}
		}
	}
	
	public int getVotingPower() {
		return this.votingPower;
	}
	
	public void setVotingPower(int power) {
		this.votingPower = power;
	}
	
	@Override
	public String toString() {
		return username;
	}
	
	public boolean hasVoted(String songId) {
		return votedSongs.keySet().contains(songId);
	}
	
	/**
	 * @return	{"username": username, "votedSongs": ["s1", "s2", ..]}
	 */
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("username", username);
		
		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		for (String i : votedSongs.keySet()) {
			arrBuilder.add(i);
		}
		builder.add("votedSongs", arrBuilder.build());
		
		return builder.build();
	}
}
