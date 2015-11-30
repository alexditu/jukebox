package sac.juke.model;

public class GlobalData {
	public String currentSong = "ZtFUX4Y2U84";
	public Songs songs;
	public Users users;
	
	public GlobalData() {
		songs = new Songs(true);
		users = new Users(true);
	}
	
	public synchronized Song getCurrentSong() {
		return songs.getSong(currentSong);
	}
	
	public synchronized void setCurrentSong(String id) {
		currentSong = id;
	}
}
