package com.scoindia.infomax.application.player;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.scoindia.infomax.application.player.config.Constants;
import com.scoindia.infomax.application.player.playList.PlayItem;
import com.scoindia.infomax.application.player.rssfeed.RSSFeedParser;
import com.scoindia.infomax.application.player.scheduler.DayOfTheWeek;

/**
 * @author anilShatharashi
 * @throws SchedulerException
 *	Schedules the Layouts in a Player
 */
public class SchedularLayoutTrigger {
	
	private Scheduler sched;
	private String play_at;

	private int play_every;
	private int repeat_for;
	private String TODAY_IS;
	private String play_for;

	/**
	 *	schedule RSS and WEB Layouts in a Player
	 */
	public void scheduleRSSAndWEBLayouts()  throws SchedulerException {
			String dayNames[] = new DateFormatSymbols().getWeekdays();
			Calendar date2 = Calendar.getInstance();
			TODAY_IS = dayNames[date2.get(Calendar.DAY_OF_WEEK)];
			String playItemPath = null;
			
	SchedulerFactory sf = new StdSchedulerFactory();
	sched = sf.getScheduler();
	Date showAt = null;
	for (DayOfTheWeek playitem : InfoMaxSetup.calendar_play_item) {										
		if ((playitem.getType()).equalsIgnoreCase("FullScreen_ADVT")){
				if(TODAY_IS.equalsIgnoreCase(playitem.getDay())){
					playItemPath = playitem.getSrc();
					play_for = playitem.getPlayFor();
					play_at = playitem.getPlayAt();
					play_every = playitem.getPlayEvery();
					showAt = Constants.stringToDate(play_at);
				}
	    	}
		 }
	//InfoMaxPlayer.browserFRAME.setVisible(false);

	JobDetail job = newJob(FullScreenADVT_Scheduler.class)							
			.withIdentity( "ShowWEBinFullScreen_job ", "group1")
			.build();
	
	CronTrigger cronTrigger = newTrigger()
    		.withIdentity("ShowWEBinFullScreen_trigger", "group1")
    		.usingJobData("play_for", play_for)
    		.usingJobData("playItemPath", playItemPath)
    		.withSchedule(cronSchedule("0 "+54+"/"+play_every+" "+12+" * * ?"))						//0 30 10-13 ? * WED,FRI
    		.startAt(showAt)										//"0 * 13 * * ?"
    		.build();
	
	Date ft = sched.scheduleJob(job, cronTrigger);
	System.out.println(job.getKey() + " items will be played at: " + ft
				+ " and repeat: " + cronTrigger.getDescription());
	
	if(InfoMaxSetup.play_items_List != null){										// ************  RSS FEED Advertisement **************
		for (PlayItem playitem : InfoMaxSetup.play_items_List) {
			if (playitem.getType()!=null &&(playitem.getType()).equalsIgnoreCase("RSS")){
			InfoMaxPlayer.rssParser = new RSSFeedParser(playitem.getSrc());
			play_for = playitem.getPlayFor();
			play_at = playitem.getPlayAt();
			repeat_for = playitem.getRepeatFor();
			
			Date showRSSAt = Constants.stringToDate(play_at);	
			
			job = newJob(RSSPanel_Scheduler.class)														// Show&Hide RSS Panel
					.withIdentity( "ShowRSSFeed_job ", "group1")
					.build();
			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
									.withIdentity( "ShowRSSFeed_trigger", "group1")
									.usingJobData("play_for", play_for)
									.startAt(showRSSAt)
									.withSchedule(simpleSchedule()
											.withIntervalInSeconds(180)
											.withRepeatCount(repeat_for))
											.forJob(job)
											.build();
			ft = sched.scheduleJob(job, trigger);
			System.out.println(job.getKey() + " RSS will be shown at: " + ft
					+ " and repeat: " + trigger.getRepeatCount() + " times, every "
					+ trigger.getRepeatInterval() / 1000 + " seconds");

			}else if (playitem.getType()!=null &&(playitem.getType()).equalsIgnoreCase("ADVT")) {
				play_for = playitem.getPlayFor();
				play_at = playitem.getPlayAt();
				System.out.println("Play At  ******" + play_at);
				repeat_for = playitem.getRepeatFor();
				Date showWEBLayoutAt = Constants.stringToDate(play_at);
			

			job = newJob(SidePanel_Scheduler.class) 											// showing and hiding Side Pane
							.withIdentity("showWEBLayout_JOB", "group1")
							.build();
			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
							.withIdentity("showWEBLayout_trigger", "group1")
							.usingJobData("play_for", play_for)
							.startAt(showWEBLayoutAt)
							.withSchedule(simpleSchedule()
							.withIntervalInSeconds(180)
								.withRepeatCount(repeat_for))
								.forJob(job)
								.build();
			ft = sched.scheduleJob(job, trigger);
				System.out.println(job.getKey() + " SidePanel will be shown at: " + ft
						+ " and repeat: " + trigger.getRepeatCount() + " times, every "
						+ trigger.getRepeatInterval() / 1000 + " seconds");
			
			}
		}
	}
	

	sched.start();
	System.out.println("------- Started Layout Scheduler -----------------");

	}
}