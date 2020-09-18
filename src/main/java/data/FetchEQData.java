package data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Earthquake;

public class FetchEQData {

    private final static String USER_AGENT = "Mozilla/5.0";
    
	public FetchEQData() {
		
	}
	
	public JsonObject fetchData() throws Exception {

		    //sending the http get request to the usgs api
	        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&endtime&minmagnitude=3";

	        URL obj = new URL(url);
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	        con.setRequestProperty("User-Agent", USER_AGENT);
	        int responseCode = con.getResponseCode();
	        System.out.println("\nSending GET request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);
	        
	        if(responseCode != 200) {
	        	System.err.println("The api return an error!");
	        }
	        
	        
	        System.out.println(con.getContentType());
	        
	        
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        Gson g = new Gson();

	        
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine + "\n");
	        }
	        in.close();

	        
	        //parsing the actual data starts here
			JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
			JsonArray j2Array = jsonObject.get("features").getAsJsonArray();
			
			List<Earthquake> quakes = new ArrayList<Earthquake>();

		
			for(int i =0; i < j2Array.size(); i++) {
				
				JsonObject features = j2Array.get(i).getAsJsonObject();
				JsonObject properties = features.get("properties").getAsJsonObject();//info about the earthquake
				JsonObject loc = features.get("geometry").getAsJsonObject();
				
				String quakeLocation = properties.get("place").toString();
				if(isUSAQuake(quakeLocation)) {
					Earthquake eq = new Earthquake();
					System.out.println(quakeLocation);

					/*
					 * cords example because it confused me:
					 * 
					 * we get -> [161.3486,55.7276,81.56]
					 * latitude / longitude: 55.7276°N / 161.3486°E
					 */
					JsonArray cords = loc.get("coordinates").getAsJsonArray();
					eq.setLat(cords.get(1).getAsDouble());
					eq.setLon(cords.get(0).getAsDouble());
					eq.setTitle(quakeLocation);
					quakes.add(eq);

				}
			
				
				
			}
						
			
	        System.out.println("Done Fetching data");

	        return jsonObject;
	    }
	   
	   private static boolean isUSAQuake(String str) {
		   
		   if(str.contains("CA") || str.contains("Alaska") || str.contains("Nevada") || str.contains("Hawaii")) {
			   return true;
		   }
			   
		   return false;
	   }
	
	
}
