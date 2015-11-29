package sac.juke.model;

import java.util.HashMap;

public class SongList {
	public HashMap<String, Song> songs;
	
	public SongList() {
		songs = new HashMap<>();
	}
	
	public Song getSong(String id) {
		return songs.get(id);
	}
	
	public void addSong(String id, Song song) {
		songs.put(id, song);
	}
	
	public void addDefaultSongs() {
		addSong("ZtFUX4Y2U84", new Song("ZtFUX4Y2U84", 5 * 60 + 21, 0, "Joe Cocker", "Unchain my heart"));
		addSong("d27gTrPPAyk", new Song("d27gTrPPAyk", 4 * 60 + 27, 0, "Sting", "Englishman in New York"));
		addSong("Q_L-0Ryhmic", new Song("Q_L-0Ryhmic", 4 * 60 + 46, 0, "Eric Clapton", "Layla"));
	}
}
