package com.usaquake.maven.quickstart;

import java.util.List;

import data.DataService;
import junit.framework.TestCase;
import log.AppLog;
import model.Earthquake;
import ui.App;

public class Test_EartquakeDataFetch extends TestCase  {

	
	//test that we can fetch earthquake data
	public void testFetch() {
		DataService fetch = new DataService();
		AppLog logFile = new AppLog();

		try {
			List<Earthquake> quakes = fetch.getListOfEarthquakes(logFile);
			assertFalse(quakes.isEmpty());
		} catch (Exception e) {
			logFile.logError(e.getMessage());
		}
		
	}
}
