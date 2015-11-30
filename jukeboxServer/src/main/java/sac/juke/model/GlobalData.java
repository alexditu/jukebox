package sac.juke.model;

public class GlobalData {
	public String currentSong = "ZtFUX4Y2U84";
	public Songs songs;
	public Users users;
	
	/* current iteration, it increases when song changes */
	private int iter = 0;
	
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
	
	public int getIter() {
		return iter;
	}
	
	public void incIter() {
		iter++;
	}
	
	public void setIter(int iter) {
		this.iter = iter;
	}
}
