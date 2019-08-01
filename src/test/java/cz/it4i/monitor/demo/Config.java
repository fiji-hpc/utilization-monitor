
package cz.it4i.monitor.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

	public static final String FIJI_EXECUTABLE_PATH = "Fiji.executable.path";

	public static final String JPG_SUFFIX = ".jpg";

	public static final String PNG_SUFFIX = ".png";

	public static final Logger log = LoggerFactory.getLogger(Config.class);

	static Properties properties;

	static final String CONFIG_FILE_NAME = "configuration.properties";
	static {
		properties = new Properties();
		try {
			final InputStream resource = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
			if (resource == null)
				throw new RuntimeException("Configuration file " + CONFIG_FILE_NAME + " needs to be set up correctly.");
			properties.load(resource);
		} catch (final IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static String getOutputFilesPattern() {
		return properties.getProperty("output_file_patterns");
	}

	public static String getInputDirectory() {
		return properties.getProperty("input_directory");
	}

	public static String getFijiExecutable() {
		return properties.getProperty(FIJI_EXECUTABLE_PATH);
	}
}
