package sac.juke.util;

import org.quartz.TriggerKey;

public class Constants {
	public static final String USERS = "connectedUsersKey";
	public static final String SONGLIST = "songListKey";
	public static final String SCHEDULER = "schedulerKey";
	public static final TriggerKey TRIGGER_KEY = new TriggerKey("SongChooserTrigger", "G");
}
