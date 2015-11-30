package sac.juke.timer;

import static org.quartz.JobBuilder.newJob;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import sac.juke.model.Song;
import sac.juke.model.Songs;
import sac.juke.util.Constants;
import sac.juke.util.Utils;

public class SongScheduler {
	
	private final static Logger log = LogManager.getLogger(SongScheduler.class);
	
	public void init(ServletContext context) {

        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();
            
            //TODO: trebuie aleasa cumva prima melodie
            String firstSongId = "ZtFUX4Y2U84";
            
            Songs songList = Utils.getSongs(context);
            Song firstSong = songList.getSong(firstSongId);
             
            Date triggerTime = DateBuilder.futureDate(firstSong.getDuration(), IntervalUnit.SECOND);
            
            JobDataMap data = new JobDataMap();
            data.put("triggerTime", triggerTime);
            data.put("servletContext", context);

            JobDetail job = newJob(SongChooserJob.class)
                .withIdentity("SongChooserJob", "G")
                .usingJobData(data)
                .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("SongChooserTrigger", "G")
                .startAt(triggerTime)            
                .build();
            
            scheduler.scheduleJob(job, trigger);
            log.debug("Scheduled job with init triggerTime: " + triggerTime);
            
            context.setAttribute(Constants.SCHEDULER, scheduler);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
	
	public static void destroyScheduler(Scheduler scheduler) {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public static int getElapsedTime(Scheduler scheduler) {
		long milis = 0;
		JobKey jk = new JobKey("SongChooserJob", "G");
		
		try {
			JobDetail jd = scheduler.getJobDetail(jk);
			Date triggerTime = (Date) jd.getJobDataMap().get("triggerTime");
			
			Instant t2 = triggerTime.toInstant();
			Instant t1 = Instant.now();
			
			milis = Duration.between(t1, t2).toMillis();
			log.debug("t1: " + t1.toString() + "; t2: " + t2.toString() + "; millis: " + milis + "; " +
						" seconds: " + (milis / 1000));
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return (int)(milis / 1000);
	}

}
