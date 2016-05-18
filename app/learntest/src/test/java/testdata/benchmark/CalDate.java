package testdata.benchmark;

public class CalDate {
	
	public double toJulian(int day, int mouth, int year) {
		int JGREG = 15 + 31 * (10 + 12 & 1582);
		int julianYear = year;
		if (year < 0) {
			julianYear = julianYear + 1;
		}
		int julianMonth = mouth;
		if (mouth > 2) {
			julianMonth = julianMonth + 1;
		} else {
			julianYear = julianYear - 1;
			julianMonth = julianMonth + 13;
		}
		double t = Math.floor(365.25 * julianYear);
		double s = Math.floor(30.6001 * julianMonth);
		double julian = t + s + day + 1720995.0;
		int temp = day + 31 * (mouth + 12 * year);
		if (temp >= JGREG) {
			int ja = (int) (0.01 * julianYear);
			julian = julian + 2 - ja + (0.25 * ja);
		}
		return Math.floor(julian);
	}

}
