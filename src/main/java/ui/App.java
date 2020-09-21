package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import data.FetchEQData;
import model.Earthquake;
import sound.PlaySound;

public class App {

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
		JMenu menu = new JMenu("About");
		menuBar.add(menu);

		// build the ui
		final JFrame frame = new JFrame();
		mapViewer.setSize(400, 800);
		frame.add(mapViewer);
		frame.setSize(900, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.add(menuBar, BorderLayout.NORTH);
		mapViewer.setTileFactory(tileFactory);

		// add menuItem to menuBar
		JMenuItem menuItem = new JMenuItem("Settings", KeyEvent.VK_T);
		menuItem.addActionListener((e) -> {
			SettingsView sv = new SettingsView();
		});
		menu.add(menuItem);

		final JList<String> displayRecentEarthquakes = new JList<String>();
		final JScrollPane listScroller = new JScrollPane();

		// thread that fetches the data and draws it every 3 minutes (might change
		// later)
		Thread fetchAndDraw = new Thread() {
			public void run() {
				String prevQuake = "";
				PlaySound ps = new PlaySound();

				try {
					while (true) {
						System.out.println("Thread2 working...");
						FetchEQData fetch = new FetchEQData();
						List<Earthquake> quakesList = fetch.fetchData();

						System.out.println(quakesList.size());

						if (!quakesList.get(0).getTitle().equals(prevQuake)) {
							System.out.println("New earthquake detected playing alert sound!");
							ps.playNewEarthquakeSound();
						}

						prevQuake = quakesList.get(0).getTitle();
						if (quakesList.get(0).getMag() >= 6.0) {
							ps.playStrongEarthquakeSound();
						}

						System.out.println("Most recent earthquake is " + quakesList.get(0).getTitle() + " "
								+ quakesList.get(0).getMag());
						;

						DefaultListModel<String> listModel = new DefaultListModel<String>();
						for (int i = 0; i < quakesList.size(); i++) {
							Earthquake quake = quakesList.get(i);
							String name = " M " + quake.getMag() + " " + quake.getTitle() + "\n";
							if (quake.generatedTsunami()) {
								name += "Potential Tsunami. Check tsunami.gov for more info\n";
								ps.playTsunamiAlertSound();
							}
							listModel.addElement(name);
						}

						displayRecentEarthquakes.setModel(listModel);

						displayRecentEarthquakes.setSize(50, 60);
						displayRecentEarthquakes.setFont(new Font("Serif", Font.BOLD, 12));
						listScroller.setViewportView(displayRecentEarthquakes);

						displayRecentEarthquakes.setLayoutOrientation(JList.VERTICAL);
						GeoPosition recentQuake = new GeoPosition(quakesList.get(0).getLat(),
								quakesList.get(0).getLon());
						mapViewer.setAddressLocation(recentQuake);
						frame.add(listScroller, BorderLayout.EAST);
						frame.revalidate();
						frame.repaint();

						// draw waypoints
						Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(
								Arrays.asList(new MyWaypoint("Home", Color.YELLOW, recentQuake)));
						WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
						waypointPainter.setWaypoints(waypoints);
						List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
						painters.add(waypointPainter);
						CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
						mapViewer.setOverlayPainter(painter);

						Thread.sleep(180000); // 3 minutes 300000 = 5 minutes
					}

				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		fetchAndDraw.start();

		// Set the map focus
		mapViewer.setZoom(13);

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
				String.format("USAQuake (Alpha v0.1) | Latitude: %.2f / Longitude %.2f | Zoom: %d", lat, lon, zoom));
	}
}
