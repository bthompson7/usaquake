package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;

import org.apache.commons.configuration.ConfigurationException;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import data.FetchEQData;
import model.Earthquake;
import sound.PlaySound;
import util.Constants;
import util.CustomCellRenderer;
import util.Logging;

public class App extends Frame {

	/*
	 * Entry point for USAQuake
	 * 
	 * This app has been tested on and will work on the following Operating Systems
	 * (but should work on any device with java installed): - Windows 10 64 Bit -
	 * Ubuntu 18.04.5 LTS - Ubuntu 20.04 LTS
	 * 
	 */

	private static final int DEFAULT_ZOOM = 9;
	private static final long ONE_UNIX_HOUR = 3600000;
	private static Earthquake recentQuake = new Earthquake();


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
		Logging logFile = new Logging();

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
			} catch (IOException | URISyntaxException | ConfigurationException e1) {
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

		// thread that fetches the data and draws it every 2 minutes
		Thread fetchAndDraw = new Thread() {
			public void run() {
				PlaySound ps = new PlaySound();
				try {
					while (true) {
						logFile.logInfo("fetchAndDraw Thread updating map...");
						FetchEQData fetch = new FetchEQData();

						// waypoints to render
						Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();

						List<Earthquake> quakesList = fetch.fetchData();

						// export list of earthquakes to text file
						exportEarthquakesItem.addActionListener((e) -> {
							try {

								FileWriter file = new FileWriter("Earthquakes " + getCurrentDay() + ".txt");
								for (int i = 0; i < quakesList.size(); i++) {
									Earthquake quake = quakesList.get(i);
									String name = quake.getTimeEarthquakeHappened() + " M" + quake.getMag() + " "
											+ quake.getTitle() + "\n";
									file.write(name);
								}
								file.close();

								JOptionPane.showMessageDialog(frame, "All Earthquakes have been exported!", "Success",
										JOptionPane.INFORMATION_MESSAGE);

							} catch (IOException e1) {
								logFile.logError("Error when writing to Earthquake file\n" + e1.getMessage());

								JOptionPane.showMessageDialog(frame, "Error when writing to Earthquake file.", "Error",
										JOptionPane.ERROR_MESSAGE);
							}

						});

						if (quakesList.size() > 0) {

							recentQuake = quakesList.get(0);
							recentQuake.getTitle();
							logFile.logInfo("Most Recent Quake is " + recentQuake.getTitle());

							GeoPosition recentQuakePos = new GeoPosition(quakesList.get(0).getLat(),
									quakesList.get(0).getLon());
							MyWaypoint wp = new MyWaypoint("M " + recentQuake.getMag() + " \n" + recentQuake.getTitle(), Color.RED, recentQuakePos);
							waypoints.add(wp);

							// check if we need to play a sound
							logFile.logInfo("Got Recent Earthquake data quakesList size is " + quakesList.size());
							if (recentQuake.getMag() >= 4.0 && recentQuake.getMag() <= 4.9) {
								ps.playMag4Sound();
								logFile.logInfo("An Earthquake between Mag 4.0 to 4.9 occurred!");
							}

							if (recentQuake.getMag() >= 5.0 && recentQuake.getMag() <= 5.9) {
								ps.playMag5Sound();
								logFile.logInfo("An Earthquake between Mag 5.0 to 5.9 occurred!");

							}
							if (recentQuake.getMag() >= 6.0 && recentQuake.getMag() <= 6.9) {
								logFile.logInfo("An Earthquake between Mag 6.0 to 6.9 occurred!");

								ps.playMag6Sound();
							}
							if (recentQuake.getMag() >= 7.0) {
								ps.playMag7Sound();
								logFile.logInfo("An Earthquake between Mag 7.0 or higher occurred!");

							}

							DefaultListModel<Earthquake> listModel = new DefaultListModel<Earthquake>();

							for (int i = 1; i < quakesList.size(); i++) {
								Earthquake quake = quakesList.get(i);
								String name = quake.getTimeEarthquakeHappened() + "\n M" + quake.getMag() + " "
										+ quake.getTitle() + "\n";

								if (quake.generatedTsunami() && quake.getMag() >= 6.5) {

									name += " - Possible Tsunami Detected";
									tf.setText("Most Recent Earthquake: " + name);
									tf.setBackground(Color.RED);
									logFile.logInfo("Possible Tsunami Detected!!!");

								} else if (recentQuake.getMag() >= 5.0) {
									tf.setText("Most Recent Earthquake: " + recentQuake.getTimeEarthquakeHappened()
											+ " M" + recentQuake.getMag() + " " + recentQuake.getTitle());
									tf.setBackground(Color.ORANGE);
								} else {
									tf.setText("Most Recent Earthquake: " + recentQuake.getTimeEarthquakeHappened()
											+ " M" + recentQuake.getMag() + " " + recentQuake.getTitle());
									tf.setBackground(Color.WHITE);
								}
								listModel.addElement(quake);

								GeoPosition quakePos = new GeoPosition(quakesList.get(i).getLat(),
										quakesList.get(i).getLon());

								if (getCurrentUnixTime() - quake.getUnixTime() <= ONE_UNIX_HOUR
										&& quake.getDay().equals(getCurrentDay())) {
									MyWaypoint wp2 = new MyWaypoint("M " + quake.getMag() + " \n" + quake.getTitle(), Color.YELLOW, quakePos);
									waypoints.add(wp2);

								} else {
									MyWaypoint wp2 = new MyWaypoint("M " + quake.getMag() + " \n" + quake.getTitle(), Color.WHITE, quakePos);
									waypoints.add(wp2);
								}
							}

							recentEarthquakesList.setModel(listModel);	
							recentEarthquakesList.addMouseListener(new MouseAdapter() {
							    public void mouseClicked(MouseEvent evt) {
							        JList<?> list = (JList<?>)evt.getSource();
							        if (evt.getClickCount() == 2) {
							            int index = list.locationToIndex(evt.getPoint());
							            ListModel<?> listModel = list.getModel();
							            Earthquake quake = (Earthquake) listModel.getElementAt(index);
							            mapViewer.setZoom(5);
										GeoPosition pos = new GeoPosition(quake.getLat(),quake.getLon());
										mapViewer.setAddressLocation(pos);

							        }
							    }
							});
							CustomCellRenderer cell = new CustomCellRenderer();
							recentEarthquakesList.setCellRenderer(cell);
							recentEarthquakesList.setSize(30, 60);
							recentEarthquakesList.setFont(new Font("Serif", Font.BOLD, 12));
							listScroller.setViewportView(recentEarthquakesList);
							recentEarthquakesList.setLayoutOrientation(JList.VERTICAL);
							mapViewer.setAddressLocation(recentQuakePos);
							frame.add(listScroller, BorderLayout.EAST);

							// draw waypoints
							WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
							waypointPainter.setWaypoints(waypoints);

							waypointPainter.setRenderer(new FancyWaypointRenderer());
							mapViewer.setOverlayPainter(waypointPainter);

							panel.revalidate();
							panel.repaint();
							frame.revalidate();
							frame.repaint();

							logFile.logInfo("DONE updating map sleeping.");
						} else {
							logFile.logError("Unable to fetch recent earthquake data! Trying again in 2 minutes");
							JOptionPane.showMessageDialog(frame, "Unable to fetch data. Trying again in 2 minutes",
									"Error", JOptionPane.ERROR_MESSAGE);
						}

						Thread.sleep(120000); // 120000 = 2 minutes 180000 = 3 minutes 300000 = 5 minutes

					}

				} catch (Exception e) {
					e.printStackTrace();
					logFile.logError(
							"Unable to fetch recent earthquake data! Trying again in 2 minutes" + e.getMessage());
					JOptionPane.showMessageDialog(frame, "Unable to fetch Earthquake data: " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);

				}
			}
		};

		fetchAndDraw.start();

		// reset map location to the most recent earthquake
		resetMapLoc.addActionListener((e) -> {
			mapViewer.setZoom(DEFAULT_ZOOM);
			GeoPosition pos = new GeoPosition(recentQuake.getLat(),recentQuake.getLon());
			mapViewer.setAddressLocation(pos);

		});

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
				String.format("USAQuake " + Constants.getVersion() + " | Latitude: %.2f / Longitude %.2f | Zoom: %d",
						lat, lon, zoom));
	}

	private static String getCurrentDay() {
		long unixTime = System.currentTimeMillis();
		Date date = new Date(unixTime);
		DateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String day = formatter.format(date);
		return day;
	}

	private static long getCurrentUnixTime() {
		return System.currentTimeMillis();
	}


}
