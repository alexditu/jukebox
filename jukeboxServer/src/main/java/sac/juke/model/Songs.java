package sac.juke.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO: generate songs from youtube or other source
 * @author alex
 *
 */
public class Songs {
	private final static Logger log = LogManager.getLogger(Songs.class);
	
	public HashMap<String, Song> songs;
	private ArrayList<String> ids;
	
	public Songs() {
		songs = new HashMap<>();
		ids = new ArrayList<>();
	}
	
	public Songs(boolean useDefaultSongs) {
		this();
		if (useDefaultSongs == true) {
			addDefaultSongs();
		}
	}
	
	public Set<String> getSongsKeySet() {
		return songs.keySet();
	}
	
	public Song getSongAt(int index) {
		if (index > ids.size() - 1) {
			log.error("Invalid index!!!");
			return songs.get(ids.get(0));
		}
		return songs.get(ids.get(index));
	}
	
	public int getTotalSongs() {
		return ids.size();
	}
	
	public synchronized void vote(String id, User u) {
		HashMap<String, Integer> votedSongs = u.getVotedSongs();
		for(String songId : votedSongs.keySet()) {
			this.songs.get(songId).update(u.getUsername(), votedSongs.get(songId));
		}
	}
	
	public synchronized void unvote(String id, User u) {
		HashMap<String, Integer> votedSongs = u.getVotedSongs();
		this.songs.get(id).update(u.getUsername(), 0);
		for(String songId : votedSongs.keySet()) {
			this.songs.get(songId).update(u.getUsername(), votedSongs.get(songId));
		}
	}
	
	//TODO: remove synchronized if the list is readOnly
	public synchronized Song getSong(String id) {
		return songs.get(id);
	}
	
	public synchronized void  addSong(String id, Song song) {
		songs.put(id, song);
		ids.add(id);
	}
	
	public void addDefaultSongs() {
		addSong("ZtFUX4Y2U84", new Song("ZtFUX4Y2U84", 5 * 60 + 21, 0, "Joe Cocker", "Unchain my heart"));
		addSong("d27gTrPPAyk", new Song("d27gTrPPAyk", 4 * 60 + 27, 0, "Sting", "Englishman in New York"));
		addSong("Q_L-0Ryhmic", new Song("Q_L-0Ryhmic", 4 * 60 + 46, 0, "Eric Clapton", "Layla"));
	}
}
