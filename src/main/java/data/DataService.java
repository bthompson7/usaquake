package data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import log.AppLog;
import model.Earthquake;

public class DataService {

	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36";
	private static final Map<String, String> regions = new HashMap<>();
	
	private static final String[] supported_regions = { "CA", "California", "Alaska", "Nevada", "Hawaii", "Oregon", "Washington",
			"Montana", "Idaho", "Texas", "Wyoming", "Utah", "New Mexico", "Colorado", "Oklahoma", "OK", "Maine", "ME",
			"Kansas", "Japan", "Missouri","New Mexico", "Arizona", "Michigan","Kansas","Ohio","Puerto Rico"};

	public DataService() {
        for (String supportedRegion : supported_regions) {
            regions.put(supportedRegion, "");
        }
	}

	/**
	 *
	 * @return an empty list if the HTTP request was not successful or there was an error parsing the data. Otherwise, it returns a list of earthquakes
	 */
	public List<Earthquake> getListOfEarthquakes(AppLog logFile) {
		List<Earthquake> earthquakes = new ArrayList<>();

		try{
			// sending the http request to the usgs api
			String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getCurrentDateForApi() + "&endtime&minmagnitude=1.5";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("User-Agent", USER_AGENT);
			int responseCode = con.getResponseCode();

			if (responseCode != 200) {
				return earthquakes;
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine).append("\n");
			}
			in.close();

			// parsing the actual data starts here
			JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
			JsonArray j2Array = jsonObject.get("features").getAsJsonArray();

			for (int i = 0; i < j2Array.size(); i++) {

				JsonObject features = j2Array.get(i).getAsJsonObject();
				JsonObject properties = features.get("properties").getAsJsonObject();// info about the earthquake
				JsonObject loc = features.get("geometry").getAsJsonObject();
				String quakeLocation;

				if(properties.get("place").isJsonNull()) {
					quakeLocation = "Unknown Location";
				}else {
					quakeLocation = properties.get("place").getAsString();
				}

				if (isQuakeFromSupportedRegion(quakeLocation)) {
					Earthquake eq = new Earthquake();
					JsonArray cords = loc.get("coordinates").getAsJsonArray();

					//set the fields
					eq.setLat(cords.get(1).getAsDouble());
					eq.setLon(cords.get(0).getAsDouble());
					eq.setTimeEarthquakeHappened(eq.unixTimeToDate(properties.get("time").getAsLong()));
					eq.setTitle(quakeLocation);
					eq.setHour(eq.unixHour(properties.get("time").getAsLong()));
					eq.setDay(eq.unixDay(properties.get("time").getAsLong()));
					eq.setUnixTime(properties.get("time").getAsLong());

					eq.setGeneratedTsunami(properties.get("tsunami").getAsInt() == 1);
					eq.setMag(properties.get("mag").getAsDouble());
					earthquakes.add(eq);
				}
			}
			return earthquakes;
		}catch (Exception e){
			logFile.logError(e.getMessage());
			return earthquakes;
		}
	}

	private static boolean isQuakeFromSupportedRegion(String str) {
		String[] words = str.split(" ");
		return regions.containsKey(words[words.length - 1]);
	}

	private static String getCurrentDateForApi() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.now().format(formatter);
	}
}
