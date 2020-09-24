package data;


public class EarlyEarthquakeWarning {

	/*
	 * Uses google trends to attempt to predict if an earthquake is about to happen
	 * 
	 * Because most people when they feel an earthquake go to social media first
	 * 
	 */
	
	public EarlyEarthquakeWarning() {
		
	}
	
	
	/*
	 * Parses csv file from google trends
	 * 
	 * doc for selenium : https://www.selenium.dev/documentation/en/
	 *  possible queries:
	 *  
	 * https://trends.google.com/trends/explore?date=now%207-d&geo=US&q=earthquake
	 * https://trends.google.com/trends/explore?date=now%201-d&geo=US&q=earthquake
	 * 
	 * 
	 * https://trends.google.com/trends/explore?date=now%207-d&geo=US&q=earthquake%20united%20states
	 * 
	 * https://trends.google.com/trends/explore?date=now%201-H&geo=US&q=did%20an%20earthquake%20just%20happen 
	 * 
	 * ^ thats a good one the past hour for "did an earthquake just happen"
	 * 
	 */
	public boolean earlyWarning() {
		return false;
	}
}
