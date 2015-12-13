package sac.juke.timer;

import java.util.Date;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import sac.juke.model.GlobalData;
import sac.juke.model.Song;
import sac.juke.model.Songs;
import sac.juke.model.Users;
import sac.juke.util.Constants;
import sac.juke.util.Utils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SongChooserJob implements Job {
	private final static Logger log = LogManager.getLogger(SongChooserJob.class);
	
	/**
	 * Method which will execute when the timer fires. Will compute the next playing song, and
	 * rearms the trigger to fire after the song ends (now() + duration).
	 */
	@Override
	public void execute(JobExecutionContext jobContext) {

		log.debug("Computing next song");

		/* dataMap: persistently keeps the jobs data in form of HashMap
		 * currently it contains: servletContext and triggerTime */
		JobDataMap dataMap = jobContext.getJobDetail().getJobDataMap();
		ServletContext servletContext = (ServletContext)dataMap.get("servletContext");

		Song nextSong = chooseNextSong(servletContext);
		String nextSongId = nextSong.getId();
		
		int duration = nextSong.getDuration();
		
		Date triggerTime = updateTrigger(jobContext.getScheduler(), duration);
		dataMap.put("triggerTime", triggerTime);
		
		/* Update current playing song */
		log.debug("Next song is: " + nextSong);
		GlobalData data = Utils.getGlobalData(servletContext);
		data.setCurrentSong(nextSongId);
	}
	
	/**
	 * Sets the timer to fire after song duration expires. It returns the Date(time) when the
	 * song will end (useful for computing the elapsed time since song started, a.k.a. seekTime)
	 * 
	 * @param sched		current scheduler
	 * @param duration	current song duration in seconds
	 * @return			Date when song ends (the trigger will fire)
	 */
	public Date updateTrigger(Scheduler sched, int duration) {
		log.debug("Updating trigger");
		Trigger oldTrigger;
		try {
			// retrieve the trigger
			oldTrigger = sched.getTrigger(Constants.TRIGGER_KEY);
			
			// obtain a builder that would produce the trigger
			TriggerBuilder tb = oldTrigger.getTriggerBuilder();

			// update the schedule associated with the builder, and build the new trigger
			Date triggerTime = DateBuilder.futureDate(duration, IntervalUnit.SECOND);
			Trigger newTrigger = tb
	                .startAt(triggerTime)            
	                .build();

			sched.rescheduleJob(oldTrigger.getKey(), newTrigger);
			
			log.debug("Job scheduled for execution at: " + triggerTime.toString());
			
			return triggerTime;
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
			return null;
		}
	}
	
	private void updateServerState(Songs songs, Users users, Song nextSong) {
		users.updateVotingPower(nextSong.getId());
		users.flushVotedSongs();
		users.updateState();
		songs.flushSongVotes();
	}
	
	public Song chooseNextSong(ServletContext ctx) {
		Songs songs = Utils.getSongs(ctx);
		Users users = Utils.getUsers(ctx);
		
		Utils.incIter(ctx);
		int iter = Utils.getIter(ctx);
		
		/* Request next song according to its score */
		Song nextSong = songs.getNextSong();
		
		log.debug("Next song was chosen: " + nextSong.getId() + " " + (nextSong.getScore()));
		
		/* Update server internal state and maximum score for users */
		updateServerState(songs, users, nextSong);
		
		/* update age */
		nextSong.setAge(iter);
		
		return nextSong;
	}
}
