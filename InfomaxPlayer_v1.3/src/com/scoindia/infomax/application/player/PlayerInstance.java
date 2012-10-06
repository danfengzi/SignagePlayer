package com.scoindia.infomax.application.player;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.logger.Logger;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaList;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

/**
 * A single player instance and associated video surface.
 */
public class PlayerInstance extends MediaPlayerEventAdapter {

  private final EmbeddedMediaPlayer mediaPlayer;
  private final MediaListPlayer mlPlayer;
  private final Canvas videoSurface;
  private MediaList mediaList;
  
  public PlayerInstance(EmbeddedMediaPlayer mediaPlayer) {
    this.mediaPlayer = mediaPlayer;
    this.mlPlayer = null;
    this.videoSurface = new Canvas();
    this.videoSurface.setBackground(Color.black);
    
    mediaPlayer.addMediaPlayerEventListener(this);
  }
 
  public PlayerInstance(EmbeddedMediaPlayer mediaPlayer, MediaListPlayer mlPlayer, MediaList mList) {
	    this.mediaPlayer = mediaPlayer;
	    this.mlPlayer = mlPlayer;
	    this.videoSurface = new Canvas();
	    this.videoSurface.setBackground(Color.black);
	    this.mediaList = mList;
	    mediaPlayer.addMediaPlayerEventListener(this);
  }

/*  public void setMediaList(MediaList mediaList){
	  this.mediaList = mediaList;
  }*/
  
  public MediaList getMediaList(){
	  return mediaList;
  }
  
  public EmbeddedMediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }
  
  public MediaListPlayer getMediaListPlayer() {
    return mlPlayer;
  }
  
  public Canvas getVideoSurface() {
    return videoSurface;
  }

  @Override
  public void mediaChanged(MediaPlayer mediaPlayer) {
    System.out.println("mediaChanged");
  }

  @Override
  public void playing(MediaPlayer mediaPlayer) {
	  Logger.debug("playing(mediaPlayer={})", mediaPlayer);
      MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
      Logger.info("mediaDetails={}", mediaDetails);
      System.out.println("MediaLength  "+mediaPlayer.getLength());
  }

  @Override
  public void paused(MediaPlayer mediaPlayer) {
    System.out.println("paused");
  }

  @Override
  public void stopped(MediaPlayer mediaPlayer) {
    System.out.println("stopped");
    Logger.debug("stopped(mediaPlayer={})", mediaPlayer);
    mediaPlayer.release();
  }

  @Override
  public void finished(MediaPlayer mediaPlayer) {
    System.out.println("finished");
    Logger.info("finished(MediaPlayer={})", mediaPlayer);
  }

  @Override
  public void error(MediaPlayer mediaPlayer) {
    System.out.println("error");
    //System.exit(1);
    Logger.debug("error(mediaPlayer={})", mediaPlayer);
  }
  
  @Override
  public void opening(MediaPlayer mediaPlayer) {
    System.out.println("opening");
  }

    @Override
    public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
      Logger.debug("videoOutput(mediaPlayer={},newCount={})", mediaPlayer, newCount);
      if(newCount == 0) {
        return;
      }
    
      MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
      //Logger.info("mediaDetails={}", mediaDetails);
      System.out.println("mediaDetails=" + mediaDetails);
      
      MediaMeta mediaMeta = mediaPlayer.getMediaMeta();
      //Logger.info("mediaMeta={}", mediaMeta);
      System.out.println("mediaMeta=" + mediaMeta);
      
      final Dimension dimension = mediaPlayer.getVideoDimension();
      Logger.debug("dimension=", dimension);
      if(dimension != null) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
           // canvas.setSize(dimension);
            //mainFrame.pack();
          }
        });
      }
    }
 
    @Override
    public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
      Logger.debug("mediaSubItemAdded(mediaPlayer={},subItem={})", mediaPlayer, subItem);
    }

   	@Override
    public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
      Logger.debug("mediaStateChanged(mediaPlayer={},newState={})", mediaPlayer, newState);
    }

    @Override
    public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
      Logger.debug("mediaMetaChanged(mediaPlayer={},metaType={})", mediaPlayer, metaType);
    }
}
