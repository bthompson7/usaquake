package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	private String hour;
	private String day;
	private long unixTime;

	public boolean isGeneratedTsunami() {
		return generatedTsunami;
	}
	private boolean alertPlayed;

	public String getTimeEarthquakeHappened() {
		return timeEarthquakeHappened;
	}

	public void setTimeEarthquakeHappened(String timeEarthquakeHappened) {
		this.timeEarthquakeHappened = timeEarthquakeHappened;
	}

	public Earthquake() {

	}
	public boolean hasAlertPlayed() {
		return alertPlayed;
	}
	public void setAlertPlayed(boolean alertPlayed) {
		this.alertPlayed = alertPlayed;
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
		return round(mag,2);
	}

	public void setMag(double mag) {
		this.mag = mag;
	}
	
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public long getUnixTime() {
		return unixTime;
	}

	public void setUnixTime(long unixTime) {
		this.unixTime = unixTime;
	}
	
	public String unixTimeToDate(long epoch) {
		Date date = new Date(epoch);
		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy:HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.format(date);

	}
	
	public String unixHour(long epoch) {
		Date date = new Date(epoch);
		DateFormat formatter = new SimpleDateFormat("HH");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.format(date);
	}

	
	public String unixDay(long epoch){
		Date date = new Date(epoch);
		DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.format(date);
	}
	

	public double round(double value, int places) {
	    if (places < 0) {
	    	throw new IllegalArgumentException();
	    }

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	


}
