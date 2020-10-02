package util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {

	Logger logger = Logger.getLogger("MyLog");  
    FileHandler fh;  

    
	public Logging() {
		init();
	}
	
	
	private void init() {
	    try {  
	    	
	        SimpleDateFormat format = new SimpleDateFormat("MdHHmmss");
	        fh = new FileHandler("USAQuake "  + format.format(Calendar.getInstance().getTime()) + ".log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.info("Log initalized in " + System.getProperty("user.dir"));  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
	}
	
	public void logInfo(String message) {
	    logger.info(message);  
	}
	
	public void logWarn(String message) {
		logger.warning(message);
	}
	
	public void logError(String message) {
	    logger.severe(message);
	}
	
}
