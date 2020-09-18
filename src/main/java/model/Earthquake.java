package model;

public class Earthquake {

	
	private double lat;
	private double lon;
	private String title;
	private boolean tsunami;
	
	
	public Earthquake() {
		
	}
	

	
	public boolean isTsunami() {
		return tsunami;
	}

	public void setTsunami(boolean tsunami) {
		this.tsunami = tsunami;
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
	
	
	
}
