package sac.juke.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Retine informatiile despre un cantec
 * @author alex
 *
 */
public class Song {

	private String id;		// id-ul melodiei, recunoscut de youtube
	private int duration;	// durata in secunde
	private int votes;		// numarul de voturi
	private String name;
	private String singer;

	public Song(String id, int duration, int votes, String name, String singer) {
		super();
		this.id = id;
		this.duration = duration;
		this.votes = votes;
		this.name = name;
		this.singer = singer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public Song() {
		super();
	}

	public Song(String id, int duration) {
		super();
		this.id = id;
		this.duration = duration;
	}

	public Song(String id, int duration, int votes) {
		super();
		this.id = id;
		this.duration = duration;
		this.votes = votes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
	
//	private String id;		// id-ul melodiei, recunoscut de youtube
//	private int duration;	// durata in secunde
//	private int votes;		// numarul de voturi
//	private String name;
//	private String singer;
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder .add("id", id)
				.add("duration", duration)
				.add("votes", votes)
				.add("name", name)
				.add("singer", singer);
		return builder.build();
	}
}
