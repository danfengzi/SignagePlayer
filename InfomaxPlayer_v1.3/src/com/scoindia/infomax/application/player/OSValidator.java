package com.scoindia.infomax.application.player;

import java.io.File;

public class OSValidator {
	
	public static String HOME_DIRECTORY;
	 
	public static boolean isWindows() {
 		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}
 
	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}
 
	public static boolean isSolaris() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("sunos") >= 0);
	}
	
	public static File getSettingsDirectory() {
	    String userHome = System.getProperty("user.home");
	    if(userHome == null) {
	        throw new IllegalStateException("user.home==null");
	    }
	    File home = new File(userHome);
	    File settingsDirectory = new File(home, ".myappdir");
	    if(!settingsDirectory.exists()) {
	        if(!settingsDirectory.mkdir()) {
	            throw new IllegalStateException(settingsDirectory.toString());
	        }
	    }
	    return settingsDirectory;
	}
 
}