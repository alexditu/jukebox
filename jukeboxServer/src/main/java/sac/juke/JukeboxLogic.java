package sac.juke;

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
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import sac.juke.model.Songs;
import sac.juke.model.User;
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
			log.debug(username + " " + i + "has voted " + user.hasVoted(i));
			arr.add(song.build());
		}
		
		JsonObjectBuilder ret = Json.createObjectBuilder();
		ret.add("songs", arr.build());
		
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
		
		if (user == null) {
			/* should not get here */
			log.debug("Error: user not found: " + username);
			return "FAIL";
		}
		
		if (user.hasVoted(id)) {
			if (!checked) {
				user.unvote(id);
			}
		} else {
			//log.debug(username + " " + id + " not checked, checking");
			if (checked) {
				//log.debug(username + " " + id + " now checked");
				user.vote(id);
				log.debug(username + " " + id + user.hasVoted(id));
			}
		}
		
		return "OK";
	}
	
}
