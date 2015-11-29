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
import sac.juke.model.SongList;
import sac.juke.util.Constants;

public class SongScheduler {
	
	private final static Logger log = LogManager.getLogger(SongScheduler.class);
	
	public void init(ServletContext context) {

        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();
            
            // trebuie aleasa cumva prima melodie
            String firstSongId = "ZtFUX4Y2U84";
            SongList songList = (SongList)context.getAttribute(Constants.SONGLIST);
            Song firstSong = songList.getSong(firstSongId);
             
            Date triggerTime = DateBuilder.futureDate(11 /*firstSong.getDuration()*/, IntervalUnit.SECOND);
            
            JobDataMap data = new JobDataMap();
            data.put("triggerTime", triggerTime);
            data.put("servletContext", context);
            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(SongChooserJob.class)
                .withIdentity("SongChooserJob", "G")
                .usingJobData(data)
                .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("SongChooserTrigger", "G")
                .startAt(triggerTime)            
                .build();
            
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            
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
