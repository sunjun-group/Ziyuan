package jdart.core.socket2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
	public void run(JDartParams jdartParams, int port){
		log.info("JDart begin : "+jdartParams.getClassName()+"."+jdartParams.getMethodName());
		List<TestInput> result = new JDartCore().run(jdartParams);
		log.info("JDart over");
		Socket socket = null;
		PrintWriter pw = null;
		try {
			socket = new Socket("127.0.0.1", port);
			log.info("Connect InterSocket=" + socket);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			if (result != null) {
				 for (int i = 0; i < result.size(); i++) {  
					 log.info("JDart result : "+result.get(i));
					 byte[] bytes = ByteConverter.convertToBytes(result.get(i));
					 log.info("send byte[] :"+bytes.length);
					 pw.println(new String(bytes));
					 pw.flush();  
					 pw.println("line end");
					 pw.flush();  
		        }			 
			}
			pw.println("END"); 
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
			new JDartClient().run(JDartServerSingle.constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site), port);
		}else{
			new JDartClient().run(JDartServerSingle.constructJDartParams(), 8989);
		}
	}
	
}
