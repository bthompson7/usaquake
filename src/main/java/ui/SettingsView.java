package ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SettingsView {

	
	public SettingsView() {
		initUI();
	}
	
	
	public void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JFrame frame = new JFrame("Settings");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(tabbedPane);
        
        //tab1 panel handles general information
        JLabel label = new JLabel();
        label.setText("Version: USAQuake v0.1");
        JLabel label2 = new JLabel();
        label2.setText("About: USAQuake displays recent earthquakes on a map.");
        
        JLabel label3 = new JLabel();
        label3.setText("App Navigation: Use left mouse button to pan, mouse wheel to zoom");
        JPanel tab1Panel = new JPanel();
        tab1Panel.add(label);
        tab1Panel.add(label2);
        tab1Panel.add(label3);
        tabbedPane.addTab("About", null, tab1Panel,
                "About");
        
        //tab 2 panel handles audio settings
        JLabel label4 = new JLabel();
        label4.setText("Hello");
        JPanel tab2Panel = new JPanel();
        tab2Panel.add(label4);
        tabbedPane.addTab("Audio", null, tab2Panel,
                "Audio");
       
        
	}
	
	
}
