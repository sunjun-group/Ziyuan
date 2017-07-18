package jdart.core.socket;

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
	public static void main(String[] args) {
		new JDartClient().run(JDartServer.constructJDartParams());
	}
	
	public void run(JDartParams jdartParams){
		log.info("JDart begin : "+jdartParams.getClassName()+"."+jdartParams.getMethodName());
		List<TestInput> result = new JDartCore().run(jdartParams);
		log.info("JDart over");
		Socket socket = null;
		PrintWriter pw = null;
		try {
			socket = new Socket("127.0.0.1", JDartProcess.JDART_INTER_PORT);
			log.info("Connect InterSocket=" + socket);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			if (result != null) {
				 for (int i = 0; i < result.size(); i++) {  
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
	
	
}
