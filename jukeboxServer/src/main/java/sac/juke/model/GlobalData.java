package sac.juke.model;

public class GlobalData {
	public static String currentSong = "ZtFUX4Y2U84";
	public static SongList songs;
	
	public static synchronized Song getCurrentSong() {
		return songs.getSong(currentSong);
	}
	
	public static synchronized void setCurrentSong(String id) {
		currentSong = id;
	}
}
