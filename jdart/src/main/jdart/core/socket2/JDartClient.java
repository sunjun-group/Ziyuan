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

public class JDartClient {
	private static Logger log = LoggerFactory.getLogger(JDartClient.class);
	int javaPathLimit = 500;

	/***
	 * to constraint the number of returned result, which may cause java error :
	 * The filename or extension is too long
	 */

	public void run(JDartParams jdartParams, int port) {
		log.info("JDart begin : " + jdartParams.getClassName() + "." + jdartParams.getMethodName());
		List<TestInput> result = null;
		try {
			result = new JDartCore().run(jdartParams);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Jdart error");
		}

//		for (int i = 0; i < result.size(); i++) {
//			try {
//				byte[] bytes = ByteConverter.convertToBytes(result.get(i));
//				StringBuffer stringBuffer = new StringBuffer();
//				for (byte b : bytes) {
//					stringBuffer.append(b + "\t");
//				}
//				log.info(stringBuffer.toString());
//				TestInput testInput = (TestInput) ByteConverter.convertFromBytes(bytes);
//			} catch (IOException | ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		log.info("JDart over");
		Socket socket = null;
		DataOutputStream pw = null;
		try {
			socket = new Socket("127.0.0.1", port);
			log.info("Connect InterSocket=" + socket);
			pw = new DataOutputStream(socket.getOutputStream());
			if (result != null) {
				for (int i = 0; i < result.size() && i < javaPathLimit; i++) {
					log.info("JDart result : " + result.get(i));
					byte[] bytes = ByteConverter.convertToBytes(result.get(i));
					log.info("send byte[] :" + bytes.length);
					pw.writeInt(bytes.length);
					pw.write(bytes);
					pw.flush();
				}
			}
			pw.writeInt(-1);
			pw.flush();
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		} finally {
			try {
				log.info("Disconnect InterSocket......");
				pw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 8) {
			String classpathStr, mainEntry, className, methodName, paramString, app, site;
			classpathStr = args[0];
			mainEntry = args[1];
			className = args[2];
			methodName = args[3];
			paramString = args[4];
			app = args[5];
			site = args[6];
			int port = Integer.parseInt(args[7]);
			new JDartClient().run(JDartParams.constructJDartParams(classpathStr, mainEntry, className, methodName,
					paramString, app, site), port);
		} else {
			new JDartClient().run(JDartParams.defaultJDartParams(), 8989);
		}
	}

}
