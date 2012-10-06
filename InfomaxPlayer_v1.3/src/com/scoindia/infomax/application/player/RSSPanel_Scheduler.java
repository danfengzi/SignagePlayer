package com.scoindia.infomax.application.player;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSSPanel_Scheduler implements Job {

	private static Logger _log = LoggerFactory.getLogger(RSSPanel_Scheduler.class);
	private int mm, ss;
	private String play_for;
	  

	public RSSPanel_Scheduler() {
	}
	    /**
	     * <p>
	     * Called by the <code>{@link org.quartz.Scheduler}</code> when a
	     * <code>{@link org.quartz.Trigger}</code> fires that is associated with
	     * the <code>Job</code>.
	     * </p>
	     * 
	     * @throws JobExecutionException
	     *             if there is an exception while executing the job.
	     */
	    public void execute(JobExecutionContext context)
	        throws JobExecutionException {

	        JobKey jobKey = context.getJobDetail().getKey();
	        _log.info("Showing RSSPanel : " + jobKey + " at " + new Date());
	        
	        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
	        play_for = dataMap.getString("play_for");
	        
	        InfoMaxPlayer.mp.setVisible(true);
	        
	        TimerTask playADVTfor = new TimerTask() {
	        	@Override
	        	public void run() {
	              	InfoMaxPlayer.mp.setVisible(false);
	    	        System.out.println(" Show RSS Panel ActionListener    ");
	            }
	          };
		       
	          StringTokenizer st = new StringTokenizer(play_for, ":");
	          while(st.hasMoreTokens()) {
	        	  mm = Integer.parseInt(st.nextToken());
	        	  ss = Integer.parseInt(st.nextToken());
	        	  if(ss == 0)	ss = 1;
	          }
		        Timer hideAfter = new Timer();
		        hideAfter.schedule(playADVTfor, (mm*60 + ss)*1000);
	          System.out.println((mm*60 + ss)*1000);
	    }

}
