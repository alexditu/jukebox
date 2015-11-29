package sac.juke;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;

import sac.juke.model.GlobalData;
import sac.juke.model.SongList;
import sac.juke.model.User;
import sac.juke.timer.SongScheduler;
import sac.juke.util.Constants;

@WebListener
public class ServletConfig implements ServletContextListener {
	private static final Logger log = LogManager.getLogger(ServletConfig.class);
	
	public void contextInitialized(ServletContextEvent ev) {
		log.debug("contextInitialized");
		ServletContext context = ev.getServletContext();
		
		/* init users */
		ArrayList<User> users = new ArrayList<>();
		context.setAttribute(Constants.USERS, users);
		
		/* init songs */
		SongList songList = new SongList();
		songList.addDefaultSongs();
		context.setAttribute(Constants.SONGLIST, songList);
		GlobalData.songs = songList;

		/* init scheduler */
		SongScheduler songSched = new SongScheduler();
		songSched.init(context);
	}
	
	public void contextDestroyed(ServletContextEvent ev) {
		log.debug("contextDestroyed");
		Scheduler sched = (Scheduler) ev.getServletContext().getAttribute(Constants.SCHEDULER);
		SongScheduler.destroyScheduler(sched);
	}

}
