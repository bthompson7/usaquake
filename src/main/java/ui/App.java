package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
		boolean tsunamiMode = false;
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
		tf.setText("No warnings currently active");
		tf.setEditable(false);
		tf.setBackground(Color.GREEN);

		// add menuItem to menuBar
		JMenuItem aboutMenuItem = new JMenuItem("Settings", KeyEvent.VK_T);

		JMenuItem mapMenuItem = new JMenuItem("Reset Zoom");

		aboutMenuItem.addActionListener((e) -> {

			try {
				SettingsView sv = new SettingsView();
			} catch (IOException | URISyntaxException | ConfigurationException e1) {
				e1.printStackTrace();
			}
		});
		aboutMenu.add(aboutMenuItem);
		mapMenu.add(mapMenuItem);
		
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(menuBar);
		panel.add(tf);
		frame.add(panel, BorderLayout.NORTH);

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

						Earthquake recentQuake = quakesList.get(0);

						prevQuake = recentQuake.getTitle();
						if (recentQuake.getMag() >= 4.0 && recentQuake.getMag() <= 4.9) {
							ps.playNewEarthquakeSound();
						}

						if (recentQuake.getMag() >= 5.0 && recentQuake.getMag() <= 5.9) {
							ps.playMag5Sound();
						}
						if (recentQuake.getMag() >= 6.0 && recentQuake.getMag() <= 6.9) {
							ps.playMag6Sound();
						}
						if (recentQuake.getMag() >= 7.0) {
							ps.playMag7Sound();
							ps.playStrongEarthquakeSound();
						}

						DefaultListModel<String> listModel = new DefaultListModel<String>();
						for (int i = 0; i < quakesList.size(); i++) {
							Earthquake quake = quakesList.get(i);
							String name = " M " + quake.getMag() + " " + quake.getTitle() + "\n";
							if (quake.generatedTsunami() && quake.getMag() >= 6.5) {
								name += "Potential Tsunami. Check tsunami.gov for more info\n";
								tf.setText(name);
								tf.setBackground(Color.RED);
								frame.toFront();
								frame.revalidate();
								frame.repaint();
								ps.playTsunamiAlertSound();
							} else {
								tf.setText("No Tsunami or Earthquake warnings currently active");
								tf.setBackground(Color.GREEN);
							}
							listModel.addElement(name);
						}

						displayRecentEarthquakes.setModel(listModel);

						displayRecentEarthquakes.setSize(50, 60);
						displayRecentEarthquakes.setFont(new Font("Serif", Font.BOLD, 12));
						listScroller.setViewportView(displayRecentEarthquakes);

						displayRecentEarthquakes.setLayoutOrientation(JList.VERTICAL);
						GeoPosition recentQuakePos = new GeoPosition(quakesList.get(0).getLat(),
								quakesList.get(0).getLon());
						mapViewer.setAddressLocation(recentQuakePos);
						frame.add(listScroller, BorderLayout.EAST);
						frame.revalidate();
						frame.repaint();

						mapMenuItem.addActionListener((e) -> {
							mapViewer.setZoom(13);
							mapViewer.setAddressLocation(recentQuakePos);


						});

						
						// draw waypoints
						MyWaypoint wp = new MyWaypoint("Home", Color.orange, recentQuakePos);

						Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(Arrays.asList(wp));

						WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
						waypointPainter.setWaypoints(waypoints);

						List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
						painters.add(waypointPainter);
						CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
						mapViewer.setOverlayPainter(painter);

						Thread.sleep(180000); // 180000 = 3 minutes 300000 = 5 minutes
					}

				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				} catch (Exception e) {
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
