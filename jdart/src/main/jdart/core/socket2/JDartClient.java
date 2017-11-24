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
import sav.common.core.Pair;

public class JDartClient {
	private static Logger log = LoggerFactory.getLogger(JDartClient.class);
	int javaPathLimit = 500;

	public static void main(String[] args) throws IOException {
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
	}
	
	/***
	 * to constraint the number of returned result, which may cause java error :
	 * The filename or extension is too long
	 */

	public void run(JDartParams jdartParams, int port) {
		log.debug("JDart begin : " + jdartParams.getClassName() + "." + jdartParams.getMethodName());
		Pair<List<TestInput>, Integer> pair = null;
		List<TestInput> result = null;
		int solveCount = 0;
		try {
			pair = new JDartCore().run(jdartParams);
			result = pair.a;
			solveCount = pair.b == null ? 0 :pair.b;
		} catch (Exception e) {
//			e.printStackTrace();
			log.debug("Jdart error");
		}

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
				log.debug(e.getStackTrace().toString());
			}
		}
	}

	private void printByte(List<TestInput> result){
		for (int i = 0; i < result.size(); i++) {
			try {
				byte[] bytes = ByteConverter.convertToBytes(result.get(i));
				StringBuffer stringBuffer = new StringBuffer();
				for (byte b : bytes) {
					stringBuffer.append(b + "\t");
				}
				log.debug(stringBuffer.toString());
				TestInput testInput = (TestInput) ByteConverter.convertFromBytes(bytes);
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}

}
