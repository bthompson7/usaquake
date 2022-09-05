package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {

	private Logger logger = Logger.getLogger("MyLog");
	private FileHandler fh;

	public Logging() {
		init();
	}

	private void init() {
		try {

			// true means we append to the end of the log file
			fh = new FileHandler("usaquake.log", true);
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
