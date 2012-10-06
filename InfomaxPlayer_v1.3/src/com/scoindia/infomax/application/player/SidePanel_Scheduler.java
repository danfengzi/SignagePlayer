package com.scoindia.infomax.application.player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.Timer;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SidePanel_Scheduler implements Job {
	
	 private static Logger _log = LoggerFactory.getLogger(SidePanel_Scheduler.class);

	 private int mm, ss;
	 private String play_for;

	    /**
	     * Empty constructor for job initialization
	     */
	    public SidePanel_Scheduler() {
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
	        _log.info("Show SidePanel: " + jobKey + " at " + new Date());
	        
	        InfoMaxPlayer.cpRight.setVisible(true);
	        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
	        play_for = dataMap.getString("play_for");
	        
	          System.out.println(" *****  Show SIDE PANEL   ******  "  );
	         
		        		StringTokenizer st = new StringTokenizer(play_for, ":");
		        		while(st.hasMoreTokens()) {
		        			mm = Integer.parseInt(st.nextToken());
		        			ss = Integer.parseInt(st.nextToken());
		        			if(ss == 0)	ss = 1;
		        		}
		        	
	          System.out.println("Show ADVT Panel for  "+mm+":"+ss);
	          
	          Timer timer = new Timer((mm*60 + ss)*1000, actionListener);
	          System.out.println((mm*60 + ss)*1000);
	          timer.start();
	          
	      }
	    ActionListener actionListener = new ActionListener() {
	    	public void actionPerformed(ActionEvent actionEvent) {
	    		InfoMaxPlayer.cpRight.setVisible(false);
	    		System.out.println(" Show Side Panel ActionListener    ");
	    	}
	    };
	    

}
