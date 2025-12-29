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

        JLabel aboutLabel = new JLabel();
        aboutLabel.setText("<html>" +
                "<h3>About:</h3><br>" +
                "<ul>" +
                "<li>USAQuake " + App.getVersion() + "</li>" +
                "<li>Plots earthquakes in real time using data from the USGS.</li>" +
                "<li>Audio alerts for large earthquakes and possible tsunamis.</li>" +
                "</ul></html>");
        JLabel navigationLabel = new JLabel();
        navigationLabel.setText("<html>" +
                "<h3>App Navigation:</h3>" +
                "<br>" +
                "<ul>" +
                "<li>Use left mouse button to pan, mouse wheel to zoom</li>" +
                "</ul></html>");

        JLabel knownIssuesLabel = new JLabel();
            knownIssuesLabel.setText("<html>" +
                "<h3>Known Issues:</h3>" +
                "<br>" +
                "<ul>" +
                "<li>Some tiles not displaying when zooming. Try zooming out to 12/13</li>" +
                "</ul></html>");

        JPanel tab1Panel = new JPanel();
        tab1Panel.setLayout(new BoxLayout(tab1Panel,BoxLayout.PAGE_AXIS));
        tab1Panel.add(aboutLabel);
        tab1Panel.add(navigationLabel);
        tab1Panel.add(knownIssuesLabel);
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
