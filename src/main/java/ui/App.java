package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import data.DataService;
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
	private static final DataService dataService = new DataService();
	private static final int DEFAULT_ZOOM = 11;
	private static final long ONE_UNIX_HOUR = 3600000;
	private static final String version = "v0.5.1";

	public static void main(String[] args) {

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

		JTextField textField = new JTextField();
		textField.setEditable(false);
		textField.setFont(new Font(null, Font.BOLD, 16));

		// add menuItem to menuBar
		JMenuItem aboutMenuItem = new JMenuItem("Settings", KeyEvent.VK_T);

		JMenuItem resetMapLoc = new JMenuItem("Reset Location & Zoom to default");
		JMenuItem exportEarthquakesItem = new JMenuItem("Export Earthquakes to File");

		aboutMenuItem.addActionListener((e) -> {

			try {
				new SettingsView();
			} catch (IOException | URISyntaxException e1) {
				logFile.logError(e1.getMessage());
			}
		});
		aboutMenu.add(aboutMenuItem);
		mapMenu.add(resetMapLoc);
		mapMenu.add(exportEarthquakesItem);

		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(menuBar);
		panel.add(textField);
		frame.add(panel, BorderLayout.NORTH);

		// recent earthquakes side bar
		JList<Earthquake> recentEarthquakesList = new JList<>();
		JScrollPane listScroller = new JScrollPane();

		// Task to update the map every few minutes
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable updateMapTask  = () -> {
				List<Earthquake> quakesList = dataService.getListOfEarthquakes(logFile);
				SwingUtilities.invokeLater(() -> UIManager.updateUI(logFile, quakesList, exportEarthquakesItem, frame, panel, textField, mapViewer, recentEarthquakesList, listScroller, resetMapLoc));
		};

		// 120 = 2 minutes. 180 = 3 minutes. 300 = 5 minutes.
		scheduler.scheduleAtFixedRate(updateMapTask, 1, 120, TimeUnit.SECONDS);

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

		mapViewer.addPropertyChangeListener("zoom", evt -> updateWindowTitle(frame, mapViewer));
		mapViewer.addPropertyChangeListener("center", evt -> updateWindowTitle(frame, mapViewer));
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
		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
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
