package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

import model.Earthquake;
import ui.base.SelectionAdapter;
import ui.base.SelectionPainter;
import log.AppLog;

public class App extends Frame {
	private static int DEFAULT_ZOOM = 11;
	private static long ONE_UNIX_HOUR = 3600000;
	private static final String version = "v0.5.0";

	public static void main(String[] args) throws Exception {

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);

		// Setup local file cache
		File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
		tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

		// Setup JXMapViewer
		final JXMapViewer mapViewer = new JXMapViewer();

		// Create the menu bar.
		JMenuBar menuBar = new JMenuBar();
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		JMenu mapMenu = new JMenu("Map");
		menuBar.add(mapMenu);

		// init logging
		AppLog logFile = new AppLog();

		// build the ui
		final JFrame frame = new JFrame();
		mapViewer.setSize(400, 800);

		frame.add(mapViewer);
		frame.setSize(900, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		mapViewer.setTileFactory(tileFactory);

		JTextField tf = new JTextField();
		tf.setEditable(false);
		tf.setFont(new Font(null, Font.BOLD, 16));

		// add menuItem to menuBar
		JMenuItem aboutMenuItem = new JMenuItem("Settings", KeyEvent.VK_T);

		JMenuItem resetMapLoc = new JMenuItem("Reset Location & Zoom to default");
		JMenuItem exportEarthquakesItem = new JMenuItem("Export Earthquakes to File");

		aboutMenuItem.addActionListener((e) -> {

			try {
				new SettingsView();
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		aboutMenu.add(aboutMenuItem);
		mapMenu.add(resetMapLoc);
		mapMenu.add(exportEarthquakesItem);

		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(menuBar);
		panel.add(tf);
		frame.add(panel, BorderLayout.NORTH);

		// recent earthquakes side bar
		JList<Earthquake> recentEarthquakesList = new JList<Earthquake>();
		JScrollPane listScroller = new JScrollPane();

		Thread fetchNewEarthquakes = new Thread(() -> {
            UIManager.update(logFile, exportEarthquakesItem, frame, panel, tf, mapViewer, recentEarthquakesList, listScroller, resetMapLoc);
        });
		fetchNewEarthquakes.start();

		// Set the map focus
		mapViewer.setZoom(DEFAULT_ZOOM);

		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));

		// Add a selection painter
		SelectionAdapter sa = new SelectionAdapter(mapViewer);
		SelectionPainter sp = new SelectionPainter(sa);
		mapViewer.addMouseListener(sa);
		mapViewer.addMouseMotionListener(sa);
		mapViewer.setOverlayPainter(sp);

		mapViewer.addPropertyChangeListener("zoom", new PropertyChangeListener()

		{
			public void propertyChange(PropertyChangeEvent evt) {
				updateWindowTitle(frame, mapViewer);
			}
		});

		mapViewer.addPropertyChangeListener("center", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateWindowTitle(frame, mapViewer);
			}
		});

		updateWindowTitle(frame, mapViewer);
	}

	protected static void updateWindowTitle(JFrame frame, JXMapViewer mapViewer) {
		double lat = mapViewer.getCenterPosition().getLatitude();
		double lon = mapViewer.getCenterPosition().getLongitude();
		int zoom = mapViewer.getZoom();

		frame.setTitle(
		 		String.format("USAQuake " + App.getVersion() + " | Latitude: %.2f / Longitude %.2f | Zoom: %d",
						lat, lon, zoom));
	}

	protected static String getCurrentDay() {
		long unixTime = System.currentTimeMillis();
		Date date = new Date(unixTime);
		DateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String day = formatter.format(date);
		return day;
	}

	protected static long getCurrentUnixTime() {
		return System.currentTimeMillis();
	}

	protected static long getOneUnixHour() {
		return ONE_UNIX_HOUR;
	}
	protected static int getDefaultZoom() {
		return DEFAULT_ZOOM;
	}

	public static String getVersion() {
		return version;
	}
}
