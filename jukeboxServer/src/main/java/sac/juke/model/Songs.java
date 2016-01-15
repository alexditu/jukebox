package sac.juke.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jersey.repackaged.com.google.common.collect.Iterables;

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
	
	public void flushSongVotes() {
		for (Song s : this.songs.values()) {
			s.flushVotes();
		}
	}
	
	public void removeUser(User u) {
		HashMap<String, Integer> votedSongs = u.getVotedSongs();
		for(String songId : votedSongs.keySet()) {
			this.songs.get(songId).update(u.getUsername(), 0);
		}
	}
	
	public synchronized void update(Users users) {
		for (String u : users.getUsersKeySet()) {
			User currentUser = users.get(u);
			HashMap<String, Integer> votedSongs = currentUser.getVotedSongs();
			for(String songId : votedSongs.keySet()) {
				this.songs.get(songId).update(u, votedSongs.get(songId));
			}
		}
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
	
	public Song getNextSong() {
		int maxScore = -1;
		Song nextSong = this.songs.values().iterator().next();
		
		for (Song s : this.songs.values()) {
			if (s.getScore() > maxScore) {
				nextSong = s;
				maxScore = s.getScore();
			}
		}
		
		if (maxScore == 0) {
			log.debug("No song was votted, choosing random");
			Collection<Song> c = songs.values();
			Song songsArray[] = c.toArray(new Song[] {});
			
			int r = new Random(System.nanoTime()).nextInt(songsArray.length);
			nextSong = songsArray[r];
			log.debug("Random song choosed: " + nextSong.toString());
		}
		
		return nextSong;
	}
	
	public void addDefaultSongs() {
		addSong("ZtFUX4Y2U84", new Song("ZtFUX4Y2U84", 5 * 60 + 21, 0, "Joe Cocker", "Unchain my heart"));
		addSong("d27gTrPPAyk", new Song("d27gTrPPAyk", 4 * 60 + 27, 0, "Sting", "Englishman in New York"));
		addSong("Q_L-0Ryhmic", new Song("Q_L-0Ryhmic", 4 * 60 + 46, 0, "Eric Clapton", "Layla"));		
		addSong("Z1dIRjazpBw", new Song("Z1dIRjazpBw", 4 * 60 + 12, 0, "Soha", "Mil Pasos"));
		addSong("IEVow6kr5nI", new Song("IEVow6kr5nI", 6 * 60 + 6, 0, "Leonard Cohen", "Dance me to the end of love"));		
		addSong("Lle_GA1cg20", new Song("Lle_GA1cg20", 2 * 60 + 54, 0, "Pink Martini", "Je ne veux pas travailler"));
		addSong("8IJzYAda1wA", new Song("8IJzYAda1wA", 3 * 60 + 25, 0, "Louis Armstrong", "La vie en rose"));
		addSong("CHekNnySAfM", new Song("CHekNnySAfM", 3 * 60 + 56, 0, "Bob Marley", "Is this love"));		
		addSong("vdB-8eLEW8g", new Song("vdB-8eLEW8g", 2 * 60 + 45, 0, "Bob Marley", "One love"));		
		addSong("L3HQMbQAWRc", new Song("L3HQMbQAWRc", 4 * 60 + 49, 0, "Bob Marley", "Don't worry be happy"));
		addSong("tzkG6Xu6lUE", new Song("tzkG6Xu6lUE", 7 * 60 + 12, 0, "Bob Marley", "No woman, no cry"));
		addSong("W7tdPoRLDtc", new Song("W7tdPoRLDtc", 0 * 60 + 30, 0, "Sue Paparude", "Pentru inimi"));
		addSong("fkhH9b-7R-4", new Song("fkhH9b-7R-4", 0 * 60 + 25, 0, "Sue Paparude", "A fost o data"));
		addSong("YTPp-n7XgGE", new Song("YTPp-n7XgGE", 7 * 60 + 12, 0, "El Negro", "Ploaia"));
		
		addSong("tzEZntbH2zw", new Song("tzEZntbH2zw", 0 * 60 + 22, 0, "Demo", "Short song 1"));
		addSong("XxWTmw9MkzE", new Song("XxWTmw9MkzE", 0 * 60 + 20, 0, "Demo", "Short song 2"));		
		addSong("t-Z4m1BONbM", new Song("t-Z4m1BONbM", 0 * 60 + 18, 0, "Demo", "Short song 3"));		
		addSong("dcEDqx-CEcY", new Song("dcEDqx-CEcY", 0 * 60 + 7, 0, "Demo", "Short song 4"));
		
		addSong("ytDuslteiyU", new Song("ytDuslteiyU", 0 * 60 + 11, 0, "Demo", "Short song 5"));
		
		addSong("jHGKM-gTioY", new Song("jHGKM-gTioY", 0 * 60 + 19, 0, "Demo", "Short song 6"));
		addSong("7SVpOeiqiRI", new Song("7SVpOeiqiRI", 1 * 60 + 52, 0, "Demo", "Short song 7"));
		
		
		
	}
}







