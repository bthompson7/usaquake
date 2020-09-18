package data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FetchEQData {

    private final static String USER_AGENT = "Mozilla/5.0";
    
	public FetchEQData() {
		
	}
	
	//fetch data using usgs api from 
	//using url https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&endtime&minmagnitude=3
	   @SuppressWarnings("unused")
	public JsonObject fetchData() throws Exception {

		    //sending the actual request
	        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&endtime&minmagnitude=3";

	        URL obj = new URL(url);
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	   
	        con.setRequestProperty("User-Agent", USER_AGENT);

	        int responseCode = con.getResponseCode();
	        System.out.println("\nSending GET request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);

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
			
			List<String> quakes = new ArrayList<String>();

		
			for(int i =0; i < j2Array.size(); i++) {
				JsonObject temp = j2Array.get(i).getAsJsonObject();
				JsonObject temp2 = temp.get("properties").getAsJsonObject();//info about the earthquake
				JsonObject loc = temp.get("geometry").getAsJsonObject();
				System.out.println(loc.get("coordinates")); //get earthquake coords
			}
			
			System.out.println(j2Array.get(0));
			
			
	        System.out.println("Done");

	        return jsonObject;
	    }
	   
	   private static boolean isUSAQuake(String str) {
		   
		   if(str.contains("CA") || str.contains("Alaska") || str.contains("Nevada") || str.contains("Hawaii")) {
			   return true;
		   }
			   
		   return false;
	   }
	
	
}
