package sac.juke.util;

import javax.servlet.ServletContext;

import org.quartz.Scheduler;

import sac.juke.model.GlobalData;
import sac.juke.model.Song;
import sac.juke.model.Songs;
import sac.juke.model.Users;

/**
 * Easy methods for accessing global data
 * @author alex
 *
 */
public class Utils {
	public static Songs getSongs(ServletContext ctx) {
		return ((GlobalData)ctx.getAttribute(Constants.DATA)).songs;
	}
	
	public static Users getUsers(ServletContext ctx) {
		return ((GlobalData)ctx.getAttribute(Constants.DATA)).users;
	}
	
	public static GlobalData getGlobalData(ServletContext ctx) {
		return ((GlobalData)ctx.getAttribute(Constants.DATA));
	}
	
	public static Scheduler getScheduler(ServletContext ctx) {
		return (Scheduler)ctx.getAttribute(Constants.SCHEDULER);
	}
	
	public static Song getCurrentSong(ServletContext ctx) {
		return ((GlobalData)ctx.getAttribute(Constants.DATA)).getCurrentSong();
	}
	
}
