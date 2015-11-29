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
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import sac.juke.model.GlobalData;
import sac.juke.model.SongList;
import sac.juke.util.Constants;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SongChooserJob implements Job {
	private final static Logger log = LogManager.getLogger(SongChooserJob.class);
	
	@Override
	public void execute(JobExecutionContext jobContext) {
		

		log.debug("Computing next song");
		
		String nextSongId = "ZtFUX4Y2U84";
		JobDataMap dataMap = jobContext.getJobDetail().getJobDataMap();
		ServletContext servletContext = (ServletContext)dataMap.get("servletContext");
		SongList songList = GlobalData.songs; //(SongList) servletContext.getAttribute(Constants.SONGLIST);
		
		int duration = songList.getSong(nextSongId).getDuration();
		
		log.debug("Before updateTrigger");
		Date triggerTime = updateTrigger(jobContext.getScheduler(), duration);
		log.debug("After updateTrigger");
		dataMap.put("triggerTime", triggerTime);

	}
	
	public Date updateTrigger(Scheduler sched, int duration) {
		// retrieve the trigger
		log.debug("Updateing trigger");
		Trigger oldTrigger;
		try {
			oldTrigger = sched.getTrigger(Constants.TRIGGER_KEY);
			
			// obtain a builder that would produce the trigger
			TriggerBuilder tb = oldTrigger.getTriggerBuilder();

			// update the schedule associated with the builder, and build the new trigger
			//Date triggerTime = DateBuilder.futureDate(duration, IntervalUnit.SECOND);
			Date triggerTime = DateBuilder.futureDate(10, IntervalUnit.SECOND);
			Trigger newTrigger = tb
//					.withIdentity("SongChooserTrigger", "G")
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

}
