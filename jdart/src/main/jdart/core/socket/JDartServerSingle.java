package jdart.core.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jdart.core.JDartParams;
import jdart.core.util.ByteConverter;
import jdart.model.TestInput;

public class JDartServerSingle {
	@SuppressWarnings("deprecation")
	public List<TestInput> run(JDartParams jDartParams) {
		ServerSocket s = null;
		Socket socket = null;
		BufferedReader br = null;
		List<TestInput> list = null;
		Thread work = null;
		try {
			s = new ServerSocket(JDartProcess.JDART_INTER_PORT);
			System.out.println("ServerSocket Start:"+s);

			s.setSoTimeout(40*1000);
			work = new Thread() {				
				@Override
				public void run() {
					new JDartClient().run(jDartParams);					
				}
			};
			work.start();
			System.err.println("server Start......");
			Thread.sleep(1000);
			socket = s.accept();
			System.out.println("Connection accept socket:"+socket);
			list = new ArrayList<>();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			parseList(list, br);
			System.err.println("result size :"+list.size());
		}catch (Exception e) {
			work.stop();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			System.err.println("Server Close.....");
			try {
				if (br != null) {	
					br.close();
				}
				if (socket != null) {
					socket.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}
		
	public static void parseList(List<TestInput> list, BufferedReader br) throws IOException, ClassNotFoundException {
		StringBuffer sb  = new StringBuffer();
		while(true){
			String str = br.readLine();
            if(str.equals("END")){  
                break;  
            }else if (str.equals("line end")) {
				list.add((TestInput)(ByteConverter.convertFromBytes(sb.toString().getBytes())));
				sb = new StringBuffer();
			}else {
				sb.append(str);
				sb.append("\n");
			}
		}
		
	}

	public static void main(String[] args) {
		new JDartServerSingle().run(JDartServer.constructJDartParams());
	}
	
}
