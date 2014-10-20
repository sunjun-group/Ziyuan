package sav.commons.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import sav.common.core.utils.ConfigUtils;

public class TestConfigUtils extends ConfigUtils {
	private static final String TRUNK = "trunk";
	private static ResourceBundle testConfiguration;

	private TestConfigUtils() {
		// To hide the constructor
	}

	/**
	 * Try to get the trunk path from either:
	 * <ol>
	 * <li>System variable 'TZUYU_HOME'</li>
	 * <li>Environment variable 'TZUYU_HOME'</li>
	 * <li>Resource bundle 'test_configuration.properties'</li>
	 * </ol>
	 * If the value is defined in multiple locations, the first one is
	 * preferred. If no value was defined in all three locations,
	 * {@link MissingResourceException} is thrown.
	 * 
	 * @return path to trunk if defined in one of the three locations.
	 */
	public static String getTrunkPath() {
		return getConfig(TRUNK);
	}

	public static ResourceBundle getTestProperties() {
		if (testConfiguration == null) {
			testConfiguration = ResourceBundle.getBundle("test_configuration");
		}
		return testConfiguration;
	}

	public static String getConfig(String key) {
		String value = getProperty(key);
		if (StringUtils.isBlank(value)) {
			value = getTestProperties().getString(key);
		}
		return value;
	}

	public static String getJavaHome() {
		return System.getProperty("java.home");
	}

	public static String getTracerLibPath() {
		return getTrunkPath() + "/etc/javaslicer/assembly/tracer.jar";
	}

}
