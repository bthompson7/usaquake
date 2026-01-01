package sound;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySound {
	private Clip clip;
	
	public PlaySound() {
		
	}
	
	
	
	/* Play a sound when a new earthquake is detected
	 * 
	 */
	
	public void playNewEarthquakeSound() {
		URL url = getClass().getResource("/Shindo1.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}

	
	public void playTsunamiAlertSound() {
		URL url = getClass().getResource("/Tsunami.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}
	
	public void playStrongEarthquakeSound() {
		URL url = getClass().getResource("/EEW2.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}
	
	public void playMag4Sound() {
		URL url = getClass().getResource("/Mag4.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}
	
	public void playMag5Sound() {
		URL url = getClass().getResource("/Mag5.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}
	public void playMag6Sound() {
		URL url = getClass().getResource("/Mag6.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}
	public void playMag7Sound() {
		URL url = getClass().getResource("/Mag7Up.wav");
		loadSoundEffect(url);
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0); 
		clip.start();

	}
	private void loadSoundEffect(URL url) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


    }
}

