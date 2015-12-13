package sac.juke.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.glassfish.jersey.media.sse.EventOutput;

import sac.juke.JukeboxLogic;

@XmlRootElement
public class User {
	private static final Logger log = LogManager.getLogger(JukeboxLogic.class);
	String username;
	HashMap<String, Integer> votedSongs;
	ArrayList<String> followedUsers;
	int votingPower;
	int basePower;
	
	/* SSE connection */
	EventOutput eventOutput;
	
	public User() {
		this.votedSongs = new HashMap<>();
		this.followedUsers = new ArrayList<>();
		this.votingPower = 100;
		this.basePower = 100;
		eventOutput = new EventOutput();
	}
	
	public User(String username) {
		this();
		this.username = username;
	}
	
	public EventOutput getEventOutput() {
		return eventOutput;
	}

	public void setEventOutput(EventOutput eventOutput) {
		this.eventOutput = eventOutput;
	}
	
	public boolean follows(String user) {
		return this.followedUsers.contains(user);
	}
	
	public void followUser(String user) {
		if (this.followedUsers.contains(user)) {
			this.followedUsers.remove(user);
		} else {
			this.followedUsers.add(user);
		}
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
	
	public void updateVotedSongs() {
		if (!this.votedSongs.isEmpty()) {
			int score = this.votingPower / this.votedSongs.size();
			for (String key : this.votedSongs.keySet()) {
				this.votedSongs.put(key, score);
			}
		}
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
	
	public int getTransferrablePower() {
		//log.debug("Transferrable power for " + username + "is " + (this.votingPower / this.followedUsers.size()));
		return this.votingPower / this.followedUsers.size();
	}
	
	public int getBasePower() {
		return this.basePower;
	}
	
	public void setBasePower(int value) {
		this.basePower = value;
	}
	
	public ArrayList<String> getFollowedUsers() {
		return this.followedUsers;
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
		builder.add("power", votingPower);
		
		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		for (String i : votedSongs.keySet()) {
			arrBuilder.add(i);
		}
		builder.add("votedSongs", arrBuilder.build());
		
		return builder.build();
	}
	
	/**
	 * Useful for adding extra info without altering the class
	 * @return
	 */
	public JsonObjectBuilder toJsonBuilder() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder .add("username", username);
		return builder;
	}
}
