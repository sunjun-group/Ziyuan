package jdart.core.socket2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.core.util.ByteConverter;
import jdart.model.TestInput;

public class JDartOnDemandClient {
	private static Logger log = LoggerFactory.getLogger(JDartOnDemandClient.class);
	int javaPathLimit = 500; /*** to constraint the number of returned result, which may cause java error : The filename or extension is too long*/
	int solveCount = 1;
	
	public static void main(String[] args) throws IOException {
		String classpathStr, mainEntry, className, methodName, paramString, app, site, onDemandSite;
		int idx = 0;
		classpathStr = args[idx++];
		mainEntry = args[idx++];
		className = args[idx++];
		methodName = args[idx++];
		paramString = args[idx++];
		app = args[idx++];
		site = args[idx++];
		onDemandSite = args[idx++];
		int node = Integer.parseInt(args[idx++]);
		int branch = Integer.parseInt(args[idx++]);
		String jdartInitTc = args[idx++];
		int port = Integer.parseInt(args[idx++]);
		new JDartOnDemandClient().run(JDartParams.constructOnDemandJDartParams(classpathStr, mainEntry, className,
				methodName, paramString, app, site, onDemandSite, node, branch), port, jdartInitTc);
	}
	
	public void run(JDartParams jdartParams, int port, String jdartInitTc) {
		log.debug("JDart begin : " + jdartParams.getClassName() + "." + jdartParams.getMethodName());
		List<TestInput> result = null;
		try {
			result = new JDartCore().run_on_demand(jdartParams, jdartInitTc);
		} catch (Exception e) {
//			e.printStackTrace();
			log.debug("Jdart error");
		}

//		for (int i = 0; i < result.size(); i++) {
//			try {
//				byte[] bytes = ByteConverter.convertToBytes(result.get(i));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				//			e.printStackTrace();
//			}
//		}

		log.debug("JDart over");
		Socket socket = null;
		DataOutputStream pw = null;
		try {
			socket = new Socket("127.0.0.1", port);
			log.debug("Connect InterSocket=" + socket);
			pw = new DataOutputStream(socket.getOutputStream());
			pw.writeInt(solveCount);
			if (result != null) {
				for (int i = 0; i < result.size() && i < javaPathLimit; i++) {
					log.debug("JDart result : " + result.get(i));
					byte[] bytes = ByteConverter.convertToBytes(result.get(i));
					log.debug("send byte[] :" + bytes.length);
					pw.writeInt(bytes.length);
					pw.write(bytes);
					pw.flush();
				}
			}
			pw.writeInt(-1);
			pw.flush();
		} catch (Exception e) {
			log.debug(e.toString());
//			e.printStackTrace();
		} finally {
			try {
				log.debug("Disconnect InterSocket......");
				pw.close();
				socket.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	}


}
