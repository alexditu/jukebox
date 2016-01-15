package sac.juke.model;

import java.io.IOException;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;

public class GlobalData {
	public String currentSong = "vdB-8eLEW8g";
	public Songs songs;
	public Users users;
	public SseBroadcaster broadcaster;
	
	/* current iteration, it increases when song changes */
	private int iter = 0;
	
	public GlobalData() {
		songs = new Songs(true);
		users = new Users(true);
		broadcaster = new SseBroadcaster();
	}
	
	public synchronized void broadcast(OutboundEvent outEvent) {
		broadcaster.broadcast(outEvent);
	}
	
	public synchronized void addListener(EventOutput eventOutput) {
		broadcaster.add(eventOutput);
	}
	
	public synchronized void removeListener(EventOutput eventOutput) {
		broadcaster.remove(eventOutput);
		try {
			eventOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
