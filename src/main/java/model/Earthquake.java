package model;

public class Earthquake {

	
	private double lat;
	private double lon;
	private String title;
	private double mag;
	private boolean generateTsunami;
	
	
	public Earthquake() {
		
	}
	

	

	public boolean generatedTsunami() {
		return generateTsunami;
	}

	public void setGeneratedTsunami(boolean generatedTsunami) {
		this.generateTsunami = generateTsunami;
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
	
	
}
