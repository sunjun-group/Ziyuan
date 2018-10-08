package sav.utils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author LLT
 *
 */
public class TimeUtils {
	private TimeUtils() {
	}

	public static String getCurrentTimeStamp() {
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		return ts.toString();
	}
}
