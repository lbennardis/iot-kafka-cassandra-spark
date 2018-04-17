package it.luigibennardis.iot.streaming.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyFileConfigurationReader {
	private static final Logger logger = Logger.getLogger(PropertyFileConfigurationReader.class);
	
	
	private static Properties prop = new Properties();
	
	public static Properties readPropertyFile() throws Exception {
		logger.info("Reading file configuration properties ");

		
		if (prop.isEmpty()) {
			InputStream input = PropertyFileConfigurationReader.class.getClassLoader().getResourceAsStream("iot-spark-config.properties");
			try {
				prop.load(input);
			} catch (IOException ex) {
				logger.error(ex);
				throw ex;
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}
		return prop;
	}
}
