package com.scoindia.infomax.application.player;

import java.awt.Frame;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author anilShatharashi
 *
 */
public class FullScreenADVT_Scheduler implements Job{

	int mm, ss;
	private String play_for;
	String playItemPath;
	
	public FullScreenADVT_Scheduler() {
		System.out.println(" FullScreenADVT_Scheduler  ");
	}
		
	/** 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	        String dayNames[] = new DateFormatSymbols().getWeekdays();
			Calendar date2 = Calendar.getInstance();
			String TODAY_IS = dayNames[date2.get(Calendar.DAY_OF_WEEK)];
			System.out.println("Today is a " + TODAY_IS);


	        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
	        play_for = dataMap.getString("play_for");
	        playItemPath = dataMap.getString("playItemPath");
	        
	        System.out.println(playItemPath);
	        			
					
	        		StringTokenizer st = new StringTokenizer(play_for, ":");
	        		while(st.hasMoreTokens()) {
	        			mm = Integer.parseInt(st.nextToken());
	        			ss = Integer.parseInt(st.nextToken());
	        			if(ss == 0)	ss = 1;
	        			
	        		}						
					System.out.println("MM "+mm +"SS "+ss);
	        
			SwingUtilities.invokeLater(new Runnable() {
	            @Override
				public void run() {
	            	InfoMaxPlayer.mediaPlayer.pause();
	            	InfoMaxPlayer.browserFRAME.setVisible(true);
	            	InfoMaxPlayer.webBrowser.navigate(playItemPath);

	            	TimerTask playADVTfor = new TimerTask() {
	            		@Override
	            		public void run() {
	            			System.out.println("FullScreenADVT ActionListener " );
	            			InfoMaxPlayer.mediaPlayer.play();
	            			InfoMaxPlayer.mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
	            			InfoMaxPlayer.browserFRAME.setVisible(false);
	            		}
	            	}; 
	            	
	            	Timer hideAfter = new Timer();
	            	hideAfter.schedule(playADVTfor, (mm*60 + ss)*1000);
	            	System.out.println("playItemPath" + playItemPath);
	        
	            }
			});
			
	        
	          
		}
		
}
