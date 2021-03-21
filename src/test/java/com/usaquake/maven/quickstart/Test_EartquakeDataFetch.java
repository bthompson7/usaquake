package com.usaquake.maven.quickstart;

import java.util.List;

import data.FetchEQData;
import junit.framework.TestCase;
import model.Earthquake;

public class Test_EartquakeDataFetch extends TestCase  {

	
	//test that we can fetch earthquake data
	public void testFetch() {
		FetchEQData fetch = new FetchEQData();
		try {
			List<Earthquake> quakes = fetch.fetchData();
			System.out.println(quakes.size());
			assertTrue(quakes.size() > 0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
