package com.scoindia.infomax.application.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.quartz.SchedulerException;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import com.scoindia.infomax.application.player.config.Constants;
import com.scoindia.infomax.application.player.playList.PlayItem;
import com.scoindia.infomax.application.player.playList.XMLPlayListReader;
import com.scoindia.infomax.application.player.scheduler.CalendarPlayListReader;
import com.scoindia.infomax.application.player.scheduler.DayOfTheWeek;
import com.scoindia.infomax.application.player.scheduler.SchedularPlaylistTrigger;
import com.scoindia.infomax.application.player.scheduler.SchedulerItem;
import com.scoindia.infomax.application.player.scheduler.SchedulerPlayListReader;
import com.sun.jna.Native;
/**
 * InfoMaxPlayer that plays a media list which includes videos and images,
 * with an embedded media player.
 * @author anilShatharashi
 * @category InfoMaxPlayer
 * @version 1.3
 */
public class InfoMaxSetup{
	
	static String PLAYER_LIB_PATH;
	
	public static List<PlayItem> play_items_List;
	public static List<DayOfTheWeek> calendar_play_item;
	public static List<SchedulerItem> scheduler_play_item;

	/**
	 * Creates the 'InfoMax Player' directory and sub-directories
	 * if they are not exists under the HOME_DIRECTORY
	 * and creates the config file "infomax.ini" for the Player configuration
	 */
	private static void createPlayerConfigSettings() {
		File infomaxDir = new File(Constants.INFOMAX_DIRECTORY);
		  if(!infomaxDir.exists()){
		new File(Constants.SETTINGS_DIRECTORY).mkdirs();
		new File(Constants.PLAY_LIST_DIRECTORY).mkdirs();
		new File(Constants.AUDIOS_DIRECTORY).mkdirs();
		new File(Constants.VIDEOS_DIRECTORY).mkdirs();
		new File(Constants.IMAGES_DIRECTORY).mkdirs();
		new File(Constants.DOCUMENTS_DIRECTORY).mkdirs();
		
		Properties prop = new Properties();
		try {
			prop.setProperty("scheduler_playlist", Constants.SCHEDULER_PLAY_LIST);
			prop.setProperty("PLAYER_LIB_PATH", "C:/Program Files/VideoLAN/VLC");
			prop.store(new FileOutputStream(Constants.CONFIG_FILE), "infomax_v3.4 Settings");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  }
	}
	/**
	 * Read the 'infomax.ini' file for getting the connection to the Server and 
	 * playlist information as well
	 **/	
	public static void readInfomaxConfigFile(){
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(Constants.CONFIG_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//PLAYER_LIB_PATH = prop.getProperty("PLAYER_LIB_PATH");
	}
		
	
	public static void readPlayLists(){
		play_items_List = new XMLPlayListReader().readPlayList(Constants.DEFAULT_PLAY_LIST);
		scheduler_play_item = new SchedulerPlayListReader().readPlayList(Constants.SCHEDULER_PLAY_LIST);
		calendar_play_item = CalendarPlayListReader.readPlayList(Constants.CALENDAR_PLAY_LIST);
		try {
			new SchedularPlaylistTrigger().scheduleThePlaylists();
			new SchedularLayoutTrigger().scheduleRSSAndWEBLayouts();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}	
		
	}
		
	
	public static void main(String[] args) {
		/*if(OSValidator.isWindows()){
			PLAYER_LIB_PATH = Constants.PLAYER_LIB_PATH;
		}else if(OSValidator.isUnix()){
			PLAYER_LIB_PATH = "/usr/lib";
		}else if(OSValidator.isMac()){
			PLAYER_LIB_PATH = "/usr/lib";
		}else if(OSValidator.isSolaris()){
			PLAYER_LIB_PATH = "/usr/lib";
		}*/
		
		createPlayerConfigSettings();
		readInfomaxConfigFile();
		readPlayLists();

		System.setProperty("jna.library.path", Constants.PLAYER_LIB_PATH);	
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		
		NativeInterface.open();
		UIUtils.setPreferredLookAndFeel();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new InfoMaxPlayer(play_items_List).startPlaying();
			}
		});
		NativeInterface.runEventPump();
		
	}
}