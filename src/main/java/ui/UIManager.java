package ui;

import data.FetchEQData;
import model.Earthquake;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import sound.PlaySound;
import ui.base.CustomCellRenderer;
import ui.base.FancyWaypointRenderer;
import ui.base.MyWaypoint;
import log.AppLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UIManager {
    private static boolean resetMapLocationListener = false;
    private static boolean mouseListener = false;
    private static Earthquake recentQuake = new Earthquake();
    private static final FetchEQData fetch = new FetchEQData();
    private static final PlaySound ps = new PlaySound();


    /**
     *
     * Updates the UI with new earthquakes
     *
     * @param logFile the log file
     * @param exportEarthquakesItem the "Export Earthquakes to File" JMenuItem
     * @param frame the JFrame
     * @param panel the JPanel
     * @param textField the most recent earthquakes textfield
     * @param mapViewer the jxmaperview map
     * @param recentEarthquakesList the recent earthquakes list JList item
     * @param listScroller the recent earthquakes list scroll pane
     */
    public static void update(AppLog logFile, JMenuItem exportEarthquakesItem, JFrame frame, JPanel panel, JTextField textField, JXMapViewer mapViewer, JList<Earthquake> recentEarthquakesList, JScrollPane listScroller, JMenuItem resetMapLoc){
        try {
            while (true) {
                logFile.logInfo("fetchAndDraw Thread updating map...");

                // waypoints to render
                Set<MyWaypoint> waypoints = new HashSet<>();

                List<Earthquake> quakesList = fetch.fetchData();

                // export list of earthquakes to text file
                exportEarthquakesItem.addActionListener((e) -> {
                    try {

                        FileWriter file = new FileWriter("Earthquakes " + App.getCurrentDay() + ".txt");
                        for (Earthquake quake : quakesList) {
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

                if (!quakesList.isEmpty()) {
                    recentQuake = quakesList.get(0);
                    logFile.logInfo("Most Recent Quake is " + recentQuake.getTitle());

                    GeoPosition recentQuakePos = new GeoPosition(quakesList.get(0).getLat(),
                            quakesList.get(0).getLon());
                    MyWaypoint wp = new MyWaypoint("M " + recentQuake.getMag() + " \n" + recentQuake.getTitle(), Color.RED, recentQuakePos);
                    waypoints.add(wp);

                    // check if we need to play a sound
                    logFile.logInfo("Got Recent Earthquake data quakesList size is " + quakesList.size());
                    if (!recentQuake.hasAlertPlayed() && recentQuake.getMag() >= 4.0 && recentQuake.getMag() <= 4.9) {
                        ps.playMag4Sound();
                        logFile.logInfo("An Earthquake between Mag 4.0 to 4.9 occurred!");
                        recentQuake.setAlertPlayed(true);
                    }

                    if (!recentQuake.hasAlertPlayed() && recentQuake.getMag() >= 5.0 && recentQuake.getMag() <= 5.9) {
                        ps.playMag5Sound();
                        logFile.logInfo("An Earthquake between Mag 5.0 to 5.9 occurred!");
                        recentQuake.setAlertPlayed(true);
                    }
                    if (!recentQuake.hasAlertPlayed() && recentQuake.getMag() >= 6.0 && recentQuake.getMag() <= 6.9) {
                        logFile.logInfo("An Earthquake between Mag 6.0 to 6.9 occurred!");
                        ps.playMag6Sound();
                        recentQuake.setAlertPlayed(true);
                    }
                    if (!recentQuake.hasAlertPlayed() && recentQuake.getMag() >= 7.0) {
                        ps.playMag7Sound();
                        logFile.logInfo("An Earthquake between Mag 7.0 or higher occurred!");
                        recentQuake.setAlertPlayed(true);
                    }

                    DefaultListModel<Earthquake> listModel = new DefaultListModel<>();

                    for (int i = 1; i < quakesList.size(); i++) {
                        Earthquake quake = quakesList.get(i);
                        String name = quake.getTimeEarthquakeHappened() + "\n M" + quake.getMag() + " "
                                + quake.getTitle() + "\n";

                        if (quake.generatedTsunami() && quake.getMag() >= 6.5) {

                            name += " - Possible Tsunami Detected";
                            textField.setText("Most Recent Earthquake: " + name);
                            textField.setBackground(Color.RED);
                            logFile.logInfo("Possible Tsunami Detected!!!");

                        } else if (recentQuake.getMag() >= 5.0) {
                            textField.setText("Most Recent Earthquake: " + recentQuake.getTimeEarthquakeHappened()
                                    + " M" + recentQuake.getMag() + " " + recentQuake.getTitle());
                            textField.setBackground(Color.ORANGE);
                        } else {
                            textField.setText("Most Recent Earthquake: " + recentQuake.getTimeEarthquakeHappened()
                                    + " M" + recentQuake.getMag() + " " + recentQuake.getTitle());
                            textField.setBackground(Color.WHITE);
                        }
                        listModel.addElement(quake);

                        GeoPosition quakePos = new GeoPosition(quakesList.get(i).getLat(),
                                quakesList.get(i).getLon());

                        if (App.getCurrentUnixTime() - quake.getUnixTime() <= App.getOneUnixHour()
                                && quake.getDay().equals(App.getCurrentDay())) {
                            MyWaypoint wp2 = new MyWaypoint("M " + quake.getMag() + " \n" + quake.getTitle(), Color.YELLOW, quakePos);
                            waypoints.add(wp2);

                        } else {
                            MyWaypoint wp2 = new MyWaypoint("M " + quake.getMag() + " \n" + quake.getTitle(), Color.WHITE, quakePos);
                            waypoints.add(wp2);
                        }
                    }

                    recentEarthquakesList.setModel(listModel);

                    if(!mouseListener){
                        mouseListener = true;
                        recentEarthquakesList.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent evt) {
                                JList<?> list = (JList<?>)evt.getSource();
                                if (evt.getClickCount() == 2) {
                                    int index = list.locationToIndex(evt.getPoint());
                                    ListModel<?> listModel = list.getModel();
                                    Earthquake quake = (Earthquake) listModel.getElementAt(index);
                                    mapViewer.setZoom(9);
                                    GeoPosition pos = new GeoPosition(quake.getLat(),quake.getLon());
                                    mapViewer.setAddressLocation(pos);
                                }
                            }
                        });
                    }

                    CustomCellRenderer cell = new CustomCellRenderer();
                    recentEarthquakesList.setCellRenderer(cell);
                    recentEarthquakesList.setSize(30, 60);
                    recentEarthquakesList.setFont(new Font("Serif", Font.BOLD, 12));
                    listScroller.setViewportView(recentEarthquakesList);
                    recentEarthquakesList.setLayoutOrientation(JList.VERTICAL);
                    mapViewer.setAddressLocation(recentQuakePos);
                    frame.add(listScroller, BorderLayout.EAST);

                    // reset map location to the most recent earthquake
                    if(!resetMapLocationListener){
                        resetMapLocationListener = true;
                        resetMapLoc.addActionListener((e) -> {
                            mapViewer.setZoom(App.getDefaultZoom());
                            GeoPosition pos = new GeoPosition(recentQuake.getLat(),recentQuake.getLon());
                            mapViewer.setAddressLocation(pos);
                            logFile.logInfo(recentQuake.getTitle() + " " + recentQuake.getLat() + " " + recentQuake.getLon());

                        });
                    }

                    // draw waypoints
                    WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<>();
                    waypointPainter.setWaypoints(waypoints);

                    waypointPainter.setRenderer(new FancyWaypointRenderer());
                    mapViewer.setOverlayPainter(waypointPainter);

                    panel.revalidate();
                    panel.repaint();
                    frame.revalidate();
                    frame.repaint();

                    logFile.logInfo("Finished updating map. Sleeping.");
                } else {
                    logFile.logError("Unable to fetch recent earthquake data! Trying again in 2 minutes");
                    JOptionPane.showMessageDialog(frame, "Unable to fetch data. Trying again in 2 minutes",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                Thread.sleep(120000); // 120000 = 2 minutes 180000 = 3 minutes 300000 = 5 minutes
            }

        } catch (Exception e) {
            logFile.logError(
                    "Unable to fetch recent earthquake data! Trying again in 2 minutes" +  e.getMessage());
            JOptionPane.showMessageDialog(frame, "Unable to fetch Earthquake data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }

}
