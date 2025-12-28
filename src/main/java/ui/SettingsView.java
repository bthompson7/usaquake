package ui;


import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import sound.PlaySound;

public class SettingsView {

	
	public SettingsView() throws IOException, URISyntaxException {
		initUI();
	}

	public void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JFrame frame = new JFrame("Settings");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(tabbedPane);
        
        JLabel label = new JLabel();
        label.setText("USAQuake " + App.getVersion());
        JLabel label2 = new JLabel();
        label2.setText("About: USAQuake displays recent earthquakes on a map.");
        JLabel label3 = new JLabel();
        label3.setText("App Navigation: Use left mouse button to pan, mouse wheel to zoom");
        JPanel tab1Panel = new JPanel();
        tab1Panel.setLayout(new BoxLayout(tab1Panel,BoxLayout.PAGE_AXIS));
        tab1Panel.add(label);
        tab1Panel.add(label2);
        tab1Panel.add(label3);
        tabbedPane.addTab("About", null, tab1Panel,
                "About");
        
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
