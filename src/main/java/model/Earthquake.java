package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Earthquake {

	
	private double lat;
	private double lon;
	private String title;
	private double mag;
	private boolean generatedTsunami;
	private String timeEarthquakeHappened;
	
	
	public String getTimeEarthquakeHappened() {
		return timeEarthquakeHappened;
	}

	public void setTimeEarthquakeHappened(String timeEarthquakeHappened) {
		this.timeEarthquakeHappened = timeEarthquakeHappened;
	}

	public Earthquake() {
		
	}
	
	public boolean generatedTsunami() {
		return generatedTsunami;
	}

	public void setGeneratedTsunami(boolean generatedTsunami) {
		this.generatedTsunami = generatedTsunami;
	}

	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getMag() {
		return mag;
	}
	public void setMag(double mag) {
		this.mag = mag;
	}
	
	public String unixTimeToDate(long epoch) {
		   Date date = new Date(epoch);
		   DateFormat formatter = new SimpleDateFormat("MM:dd:YYYY:HH:mm:ss");
		   formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		   String dateFormatted = formatter.format(date);
		   return dateFormatted;
	
		
	}
	
	
}
