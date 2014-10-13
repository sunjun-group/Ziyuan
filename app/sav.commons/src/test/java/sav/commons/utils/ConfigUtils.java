package sav.commons.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

public class ConfigUtils {
	private static final String TZUYU_HOME = "TZUYU_HOME";

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
		String trunk = getProperty(TZUYU_HOME);
		if (StringUtils.isBlank(trunk)) {
			trunk = ResourceBundle.getBundle("test_configuration").getString("trunk");
		}
		return trunk;
	}

	public static String getJavaHome() {
		return System.getProperty("java.home");
	}

	public static String getTracerLibPath() {
		return getTrunkPath() + "/etc/javaslicer/assembly/tracer.jar";
	}

	/**
	 * Try to get the property identified with 'name' parameter by first check
	 * in System Properties then Environment Properties.
	 * 
	 * @param name
	 *            Name of the property
	 * @return Value of the property, or <code>null</code> if nothing found
	 */
	public static String getProperty(final String name) {
		String value = System.getProperty(name);
		if (StringUtils.isBlank(value)) {
			value = System.getenv(name);
		}
		return value;
	}
}
