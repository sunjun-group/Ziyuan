package main;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

public class SimulateJPF {

	public static void runJPF (String[] args) {
		   Config.log = true;
		   Config config = JPF.createConfig( args);
		   JPF jpf = new JPF( config);
		   jpf.run();
		}
}
