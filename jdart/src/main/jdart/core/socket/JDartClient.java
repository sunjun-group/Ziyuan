package jdart.core.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.model.TestInput;

public class JDartClient {

	public static void main(String[] args) {
		new JDartClient().run(JDartServer.constructJDartParams());
	}
	
	public void run(JDartParams jdartParams){
		List<TestInput> result = new JDartCore().run(jdartParams);
		Socket socket = null;
		PrintWriter pw = null;
		try {
			socket = new Socket("127.0.0.1", JDartProcess.JDART_INTER_PORT);
			System.out.println("Socket=" + socket);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			 for (int i = 0; i < result.size(); i++) {  
	                pw.println(new String(ByteConverter.convertToBytes(result.get(i))));  
	                pw.flush();  
	        }			 
			pw.println("END"); 
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("client close......");
				pw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
