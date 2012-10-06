package com.scoindia.infomax.application.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.list.MediaList;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import com.scoindia.infomax.application.player.config.Constants;
import com.scoindia.infomax.application.player.playList.ManualPanel;
import com.scoindia.infomax.application.player.playList.MediaItems;
import com.scoindia.infomax.application.player.playList.PlayItem;
import com.scoindia.infomax.application.player.playList.RefreshObservable;
import com.scoindia.infomax.application.player.playList.XMLPlayListReader;
import com.scoindia.infomax.application.player.rssfeed.Feed;
import com.scoindia.infomax.application.player.rssfeed.MarqueePanel;
import com.scoindia.infomax.application.player.rssfeed.RSSFeedParser;

/**
 * InfomaxPlayer that plays a media list which includes videos and images,
 * with one Player Instance with multiple panels in a window.
 * @author anilShatharashi
 * @category InfoMaxPlayer
 * @version 1.3
 */
public class InfoMaxPlayer  implements Observer{

	private int rows;
	private int cols;

	public static Frame mainFrame;

	public static List<PlayerInstance> players ;
	public static EmbeddedMediaPlayer mediaPlayer;
	public static MediaListPlayer mlPlayer;
	
	static List<PlayItem> pathList;

	private static RefreshObservable refresh;

	static MarqueePanel mp;
	static JPanel cpRight;
	static JPanel mainPanel, contentPane;
	private String playADVTPath;
	String rss_msg = "";
	public static RSSFeedParser rssParser;
	
	static JFrame browserFRAME;
	static JPanel cpWEB;
	static JWebBrowser webBrowser;
	
	MediaPlayerFactory factory;
	private FullScreenStrategy fullScreenStrategy;
	private Dimension screenSize;

	private MediaList mediaList;

	protected List<MediaItems> mediaItemsList;
	
	public static int panelCount;
	int srcCount= 0;
	static int firstTime = 0;

	public static List<PlayItem> play_items_list;


	public InfoMaxPlayer(List<PlayItem> play_List) {
		
		play_items_list = play_List;
		
		players = new ArrayList<PlayerInstance>();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.black);

		contentPane = new JPanel();
		contentPane.setBackground(Color.black);
		for (PlayItem playitem :play_List) {
			if (playitem.getAs() != null
					&& (playitem.getAs()).equalsIgnoreCase("multi-layouts")) {
				rows = Integer.valueOf(playitem.getRows());
				cols = Integer.valueOf(playitem.getCols());
			}
		}
		contentPane.setLayout(new GridLayout(rows,cols,1,1));
	
		mainPanel.add(contentPane, BorderLayout.CENTER);

		mainFrame = new Frame();
		mainFrame.setUndecorated(true);
		mainFrame.setBackground(Color.black);
		mainFrame.add(mainPanel, BorderLayout.CENTER);
		mainFrame.setBounds(0, 0, screenSize.width, screenSize.height);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				for (PlayerInstance pi : players) {
					pi.getMediaPlayer().release();
				}
				factory.release();
				System.exit(0);
			}
			
		});

		this.createPlayerLayouts();
		this.playFullScreenADVT();

		
		factory = new MediaPlayerFactory("--no-video-title-show");
		fullScreenStrategy = new DefaultFullScreenStrategy(mainFrame);
		
		this.configureMainWindow();
		
		mainFrame.setVisible(true);
	}
		

	public void configureMainWindow(){

		for (PlayItem playitem : play_items_list) {
			if (playitem.getAs() != null
					&& (playitem.getAs()).equalsIgnoreCase("multi-layouts")) {
				System.out.println(" getPanelConfig *** " + playitem.getWindowConfig());
				ListIterator<ManualPanel> manualPaneIt = 
					playitem.getWindowConfig().listIterator();
				panelCount = 0;

				while (manualPaneIt.hasNext()) {
					ManualPanel panel = manualPaneIt.next();
					Iterator<MediaItems> mediaItemsIt= panel.getListOfItems().listIterator();
					MediaItems mediaItem = mediaItemsIt.next();
					if (mediaItem.getType().equalsIgnoreCase("image")
							| mediaItem.getType().equalsIgnoreCase("video")) {
						JPanel p = new JPanel(new BorderLayout());
						attachVideoCanvasToPanel(p);
						
						srcCount = srcCount + 1;
						panelCount = panelCount +1;
					}
												
					if (mediaItem.getType().equalsIgnoreCase("web")) {
						JPanel webSurface = new JPanel(new BorderLayout());
						if(firstTime==0){
						attachWebSurfaceToPanel(webSurface, mediaItem.getSrc());
						}
					}
				}// end of inner while
				firstTime = firstTime + 1;
			}// end of if
		}
	}
	
			
	/**
	 * attaches the Video Canvas to the main Panel
	 * @param JPanel p
	 */
	public void attachVideoCanvasToPanel(JPanel p) {
		EmbeddedMediaPlayer player = factory.newEmbeddedMediaPlayer(fullScreenStrategy);
		MediaListPlayer mlPlayer = factory.newMediaListPlayer();
		MediaList mList = factory.newMediaList();
		PlayerInstance playerInstance = new PlayerInstance(player, mlPlayer, mList);
		p.add(playerInstance.getVideoSurface());
		players.add(playerInstance);
		System.out.println("  attaching Video Convas ");
		contentPane.add(p);
	}
	
	public static void removeConvasFromPanel(int p){
		//contentPane.setLayout(new GridLayout());
		//contentPane.revalidate();
		//contentPane.validate();
		players.get(p).getMediaListPlayer().stop();
		contentPane.remove(p);
		mainPanel.validate();
		mainFrame.pack();
	}
		
	/**
	 * attaches the Web Surface to the main Panel
	 * @param webPanel, navigationPath
	 */
	public void attachWebSurfaceToPanel(JPanel webPanel, String navigationPath) {
		final JWebBrowser webBrowser = new JWebBrowser();
		webBrowser.setBarsVisible(false);
		webBrowser.setStatusBarVisible(false);
		webBrowser.navigate(navigationPath);
		webPanel.add(webBrowser);
		System.out.println("  attaching Web Convas ");
		contentPane.add(webPanel);
	}

	public void attachDocumentViewer() {

	}

	
	public void startPlaying() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			private Iterator<PlayItem> windowConfigIt;

			@Override
			public void run() {
				for (int i=0; i<panelCount; i++) {
					windowConfigIt =  play_items_list.iterator();
					players.get(i).getMediaPlayer().setVideoSurface(
														factory.newVideoSurface(players.get(i).getVideoSurface()));
					mediaList = players.get(i).getMediaList();
					mediaItemsList = windowConfigIt.next().getWindowConfig().get(i).getListOfItems();
					
						Iterator<MediaItems> temp = mediaItemsList.iterator();
						while(temp.hasNext()){
							MediaItems temp1 = temp.next();
						mediaList.addMedia(Constants.INFOMAX_DIRECTORY+ temp1.getSrc(),
								":start-time="+temp1.getPanelMediaStartTime(),
								":stop-time="+temp1.getPanelMediaStopTime(),
								"image-duration="+temp1.getPanelImageDuration());
						}
				}
						
				for (int i = 0; i <panelCount; i++) {
					mediaPlayer = players.get(i).getMediaPlayer();
					mlPlayer = players.get(i).getMediaListPlayer();
					mlPlayer.setMediaList(players.get(i).getMediaList());
					mlPlayer.setMediaPlayer(mediaPlayer);
					mlPlayer.play();
					
					
					/*Thread t = new CustomTimerForPanel(i,mediaPlayer.getLength());
					t.start();*/
				}	
				
			}
		});
	}
		          
	
	/**
	 * creates the WEB Layout and RSS Layout
	 */
	public void createPlayerLayouts() {

		if (play_items_list != null) {
			for (PlayItem playitem : play_items_list) {
				if (playitem.getType()!=null &&(playitem.getType()).equalsIgnoreCase("ADVT")) {
					playADVTPath = playitem.getSrc();
				}
			}
			cpRight = new JPanel(new BorderLayout());
			cpRight.setPreferredSize(new Dimension(250, 0));

			JPanel webBrowserPanel = new JPanel(new BorderLayout());
			final JWebBrowser webBrowser = new JWebBrowser();
			webBrowser.setBarsVisible(false);
			webBrowser.setStatusBarVisible(false);
			webBrowser.navigate(playADVTPath);
			webBrowserPanel.add(webBrowser, BorderLayout.CENTER);

			cpRight.add(webBrowserPanel, BorderLayout.CENTER);
			cpRight.setVisible(false);

			for (PlayItem playitem : play_items_list) {
				if (playitem.getType()!=null &&(playitem.getType()).equalsIgnoreCase("RSS")) {
					rssParser = new RSSFeedParser(playitem.getSrc());
					Feed feed = rssParser.readFeed();
					List<List<?>> al = new ArrayList<List<?>>();
					al.add(feed.getMessages());
					Iterator<List<?>> rssIt = al.iterator();
					while (rssIt.hasNext()) {
						rss_msg = rssIt.next().toString();
					}
				}
			}
		}

		mainPanel.add(cpRight, BorderLayout.EAST);
		mp = new MarqueePanel(rss_msg, 150);
		mp.setPreferredSize(new Dimension(0, 100));
		mainPanel.add(mp, BorderLayout.SOUTH);
		mp.start();
		mp.setVisible(true);
		 if(play_items_list != null){
		        refresh = new RefreshObservable(Constants.DEFAULT_PLAY_LIST);
		        refresh.addObserver(this);
		 }
	}
	
	
	/**
	 * it reads the data from the calendarPlaylist.xml and shows the browser window
	 * in full screen and navigates to the URL.
	 */
	public void playFullScreenADVT(){
		browserFRAME = new JFrame();
		browserFRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		browserFRAME.setBounds(0, 0, screenSize.width, screenSize.height);
		cpWEB = new JPanel(new BorderLayout());
		webBrowser = new JWebBrowser();
		webBrowser.setBarsVisible(false);
		webBrowser.setStatusBarVisible(false);
		browserFRAME.setUndecorated(true);
		
		cpWEB.add(webBrowser, BorderLayout.CENTER);
		browserFRAME.setContentPane(cpWEB);
	}

	
	/**
	 * it reads the data from the playlist.xml and adds it to the MediaList
	 * but actually it does't work when playlist.xml is modified. So the moment it is reading
	 * it has to add to the MediaList(like read one PATH and add it to MediaList) ONE by ONE
	 */
	public void update(Observable o, Object arg) {
		// reload data because data file have changed
		pathList = new XMLPlayListReader().readPlayList(Constants.DEFAULT_PLAY_LIST);

		Iterator<PlayItem> plLsIt = pathList.iterator();
		while (plLsIt.hasNext()) {
			if (play_items_list != null) {
				for (PlayItem playitem : play_items_list) {
					if (!((playitem.getType()).equalsIgnoreCase("RSS") 
							|| (playitem.getType()).equalsIgnoreCase("ADVT") 
							|| (playitem .getType()).equalsIgnoreCase("FullScreen_ADVT"))) {
						System.out.println(playitem.getSrc());
						//mList.addMedia(APPLICATIONS_DIRECTORY + playitem.getSrc());
					}//end of inner-if
				}//end of for
			}//end of outer-if
		}//end of while
	}//end of the method
	
}

	
