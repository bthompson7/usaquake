package ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.configuration.ConfigurationException;

import sound.PlaySound;
import util.Constants;

public class SettingsView {

	
	public SettingsView() throws IOException, URISyntaxException, ConfigurationException {
		initUI();
	}
	
	
	public void initUI() throws IOException, URISyntaxException, ConfigurationException {
        JTabbedPane tabbedPane = new JTabbedPane();
        JFrame frame = new JFrame("Settings");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(tabbedPane);
        
        //tab1 panel handles general information
        JLabel label = new JLabel();
        label.setText("USAQuake " + Constants.getVersion());
        JLabel label2 = new JLabel();
        label2.setText("About: USAQuake displays recent earthquakes on a map.");
        JLabel label3 = new JLabel();
        label3.setText("App Navigation: Use left mouse button to pan, mouse wheel to zoom");
        JPanel tab1Panel = new JPanel();
        tab1Panel.setLayout(new GridLayout(3,1));
        tab1Panel.add(label);
        tab1Panel.add(label2);
        tab1Panel.add(label3);
        tabbedPane.addTab("About", null, tab1Panel,
                "About");
        
        //tab 2 panel handles audio settings
        JPanel tab2Panel = new JPanel();
        tab2Panel.setLayout(new BoxLayout(tab2Panel, BoxLayout.PAGE_AXIS));
        PlaySound ps = new PlaySound();
        
        JButton testSoundBtn = new JButton();
        testSoundBtn.setText("Test Sound");
        testSoundBtn.addActionListener((e) -> {
        	ps.playNewEarthquakeSound();
        });
        tab2Panel.add(testSoundBtn);

      
        tabbedPane.addTab("Audio", null, tab2Panel,
                "Audio");
       
        
	}
	
	
}
