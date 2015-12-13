package sac.juke;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import sac.juke.exceptions.UserExistsException;
import sac.juke.model.GlobalData;
import sac.juke.model.Songs;
import sac.juke.model.User;
import sac.juke.model.Users;
import sac.juke.util.Utils;

public class JukeboxLogic {
	private static final Logger log = LogManager.getLogger(JukeboxLogic.class);
	
	/**
	 * Computes how much time passed since the song started
	 * @param ctx	used to access globalData
	 * @return		seekTime in seconds
	 */
	public static int getSeekTime(ServletContext ctx) {
		long remainingTime = 0;
		JobKey jk = new JobKey("SongChooserJob", "G");
		
		Scheduler scheduler = Utils.getScheduler(ctx);
		try {
			JobDetail jd = scheduler.getJobDetail(jk);
			Date triggerTime = (Date) jd.getJobDataMap().get("triggerTime");
			
			Instant t2 = triggerTime.toInstant();
			Instant t1 = Instant.now();
			
			remainingTime = Duration.between(t1, t2).toMillis();
			log.debug("t1: " + t1.toString() + "; t2: " + t2.toString() + "; millis: " + remainingTime + "; " +
						" seconds: " + (remainingTime / 1000));
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}
		/*TODO: ar trebui sincronizata cu scheduller-ul */
		int duration = Utils.getCurrentSong(ctx).getDuration();
		int seekTime = duration - ((int)(remainingTime / 1000));
		
		log.debug("duration, remaining, seek: " + duration + ", " + remainingTime + ", " + seekTime);
		
		return seekTime;
	}
	
	/**
	 * Returns the list with all songs and sets the voted flag for each one if user has voted that
	 * song
	 * @param ctx
	 * @param username
	 * @return			{"songs": [song1, song2, ...]}
	 */
	public static JsonObject getSongsForUser(ServletContext ctx, String username) {
		Songs songs = Utils.getSongs(ctx);
		User user = Utils.getUsers(ctx).get(username);
		
		if (user == null) {
			/* should not get here */
			log.debug("Error: user not found: " + username);
			return Json.createObjectBuilder().build();
		}
		
		JsonArrayBuilder arr = Json.createArrayBuilder();
		for (String i : songs.getSongsKeySet()) {
			JsonObjectBuilder song = songs.getSong(i).toJsonBuilder();
			song.add("voted", user.hasVoted(i));
			arr.add(song.build());
		}
		
		JsonObjectBuilder ret = Json.createObjectBuilder();
		ret.add("songs", arr.build());
		
		return ret.build();
	}
	
	/**
	 * Returns the list with all songs and sets the voted flag for each one if user has voted that
	 * song
	 * @param ctx
	 * @param username
	 * @return			{"songs": [song1, song2, ...]}
	 */
	public static JsonObject getUsersForUser(ServletContext ctx, String username) {
		Users users = Utils.getUsers(ctx);
		User user = Utils.getUsers(ctx).get(username);
		
		if (user == null) {
			/* should not get here */
			log.debug("Error: user not found: " + username);
			return Json.createObjectBuilder().build();
		}
		
		JsonArrayBuilder arr = Json.createArrayBuilder();
		for (String i : users.getUsersKeySet()) {
			JsonObjectBuilder userInfo = users.get(i).toJsonBuilder();
			userInfo.add("followed", user.follows(i));
			arr.add(userInfo.build());
		}
		
		JsonObjectBuilder ret = Json.createObjectBuilder();
		ret.add("users", arr.build());
		
		return ret.build();
	}
	
	/**
	 * Sets a song checked/not checked after user input
	 * @param ctx
	 * @param username
	 * @param id
	 * @param checked
	 * @return			{OK/FAIL}
	 */
	public static String markChecked(ServletContext ctx, String username, String id, boolean checked) {
		User user = Utils.getUsers(ctx).get(username);
		Songs songs = Utils.getSongs(ctx); 
		
		if (user == null) {
			/* should not get here */
			log.debug("Error: user not found: " + username);
			return "FAIL";
		}
		
		if (user.hasVoted(id)) {
			if (!checked) {
				user.unvote(id);
				songs.unvote(id, user);
			}
		} else {
			//log.debug(username + " " + id + " not checked, checking");
			if (checked) {
				//log.debug(username + " " + id + " now checked");
				user.vote(id);
				songs.vote(id, user);
			}
		}
		
		Utils.getUsers(ctx).updateState();
		Utils.getSongs(ctx).update(Utils.getUsers(ctx));
		
		/* send notification to other users */
    	updateSongNotification(ctx);
		
		return "OK";
	}
	
	/**
	 * Broadcast to others that a new user has connected.
	 * Set new user to listen to broadcasts.
	 * @param ctx
	 * @param user	new connected user
	 */
	public static void sendUserNotification(ServletContext ctx, User user) {
		
		/* Generate event */
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name("addUser");
        eventBuilder.data(String.class, user.getUsername());
        OutboundEvent event = eventBuilder.build();
        
        /* Send notification to all users */
    	GlobalData data = Utils.getGlobalData(ctx);
    	data.broadcast(event);
    	log.debug("Broadcast: " + event.getName() + " - " + event.getData());
    	
    	/* register user for broadcasts */
    	data.addListener(user.getEventOutput());
	}
	
	/**
	 * Broadcast to others that the current song has changed
	 * Set new user to listen to broadcasts.
	 * @param ctx
	 * @param
	 */
	public static void newSongNotification(ServletContext ctx, String song) {
		
		/* Generate event */
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name("changeSong");
        eventBuilder.data(String.class, song);
        OutboundEvent event = eventBuilder.build();
        
        /* Send notification to all users */
    	GlobalData data = Utils.getGlobalData(ctx);
    	data.broadcast(event);
    	log.debug("Broadcast: " + event.getName() + " - " + event.getData());
	}
	
	/**
	 * Broadcast to others that the power has changed
	 * Set new user to listen to broadcasts.
	 * @param ctx
	 * @param
	 */
	public static void updatePowerNotification(ServletContext ctx) {
		
		/* Generate event */
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name("updatePower");
        eventBuilder.data(String.class, "Updating power");
        OutboundEvent event = eventBuilder.build();
        
        /* Send notification to all users */
    	GlobalData data = Utils.getGlobalData(ctx);
    	data.broadcast(event);
    	log.debug("Broadcast: " + event.getName());
	}
	
	/**
	 * Broadcast to others that a user has logged out and close the SSE connection.
	 * @param ctx
	 * @param user	the user that logged out
	 */
	public static void removeUserNotification(ServletContext ctx, User user) {
		
		/* Generate event */
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name("removeUser");
        eventBuilder.data(String.class, user.getUsername());
        OutboundEvent event = eventBuilder.build();
        
    	GlobalData data = Utils.getGlobalData(ctx);
    	
    	/* remove SSE connection */
    	data.removeListener(user.getEventOutput());
    	
    	/* Send notification to all users */
    	data.broadcast(event);
    	log.debug("Broadcasted: " + event);
	}
	
	/**
	 * Broadcast to others that a song has changed its parameters
	 * @param user	the user that logged out
	 */
	public static void updateSongNotification(ServletContext ctx) {
		
		/* Generate event */
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name("updateSong");
        eventBuilder.data(String.class, "Updating song list");
        OutboundEvent event = eventBuilder.build();
        
    	GlobalData data = Utils.getGlobalData(ctx);
    	
    	/* Send notification to all users */
    	data.broadcast(event);
    	log.debug("Broadcasted: " + event);
	}
	
	public static User createUser(ServletContext ctx, String username) throws UserExistsException {
		Users users = Utils.getUsers(ctx);
		
		User user = new User(username);
		users.add(username, user);
		return user;
	}
	
	public static EventOutput openSseConn(ServletContext ctx, String username) {
    	User user = Utils.getUser(ctx, username);
    	return user.getEventOutput();
	}
	
	
}
