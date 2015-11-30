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
	private int score;		// numarul de voturi
	private String name;
	private String artist;
	
	/* used for MostRecent songs sort. It increases when a song ends */
	private int age;
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Song(String id, int duration, int score, String artist, String name) {
		super();
		this.id = id;
		this.duration = duration;
		this.score = score;
		this.name = name;
		this.artist = artist;
		this.age = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Song() {
		super();
	}

	public Song(String id, int duration) {
		super();
		this.id = id;
		this.duration = duration;
	}

	public Song(String id, int duration, int score) {
		super();
		this.id = id;
		this.duration = duration;
		this.score = score;
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	public JsonObject toJson() {
		JsonObjectBuilder builder = toJsonBuilder();
		return builder.build();
	}
	
	/**
	 * Useful for adding extra info without altering the class
	 * @return
	 */
	public JsonObjectBuilder toJsonBuilder() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder .add("id", id)
				.add("duration", duration)
				.add("score", score)
				.add("name", name)
				.add("artist", artist)
				.add("age", age);
		return builder;
	}
}
