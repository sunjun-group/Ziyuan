package jdart.core.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartParams;

public class JDartServer {
	private static Logger log = LoggerFactory.getLogger(JDartServer.class);
	@SuppressWarnings("deprecation")
	public List<String> run(JDartParams jDartParams) {
		ServerSocket s = null;
		Socket socket = null;
		BufferedReader br = null;
		List<String> list = null;
		Thread work = null;
		try {
			s = new ServerSocket(JDartProcess.JDART_INTER_PORT);
			log.info("InterSocket Start:"+s);
//			Runtime.getRuntime().exec("java -cp E:/hairui/git/Ziyuan/jdart/bin jdart.core.socket.JdartClient");

			s.setSoTimeout(30*1000);
			work = new Thread() {				
				@Override
				public void run() {
					new JDartClient().run(jDartParams);					
				}
			};
			work.start();
			Thread.sleep(1000);
			socket = s.accept();
			log.info("InterConnection accept socket:"+socket);
			list = new ArrayList<>();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String str = br.readLine();
                if(str.equals("END")){  
                    break;  
                }  
				list.add(str);
			}
			log.info("jdart result size :"+list.size());
		}catch (Exception e) {
			log.info(e.getMessage());
		} finally{
			log.info("InterServer Close.....");
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
		
	public static void main(String[] args) throws IOException {
		List<String> result;
		if (args.length == 7) {
			String classpathStr, mainEntry, className, methodName, paramString, app, site;
			classpathStr = args[0];
			mainEntry = args[1];
			className = args[2];
			methodName = args[3];
			paramString = args[4];
			app = args[5];
			site = args[6];			
			result = new JDartServer().run(constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site));
		}else{
			result = new JDartServer().run(constructJDartParams());
		}
		Socket socket = null;
		PrintWriter pw = null;
		try {
			socket = new Socket("127.0.0.1", JDartProcess.JDART_INTRA_PORT);
			log.info("Connect IntraSocket=" + socket);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			if (result != null) {
				log.info("JDartServer receive lines : "+result.size());
				 for (int i = 0; i < result.size(); i++) { 
					 pw.println(result.get(i));  
					 pw.flush();  
		        }	
			}		 
			pw.println("END"); 
			pw.flush();
		} catch (Exception e) {
			log.info(e.toString());
		} finally {
			try {
				log.info("Disconnect IntraSocket......");
				pw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		System.exit(0);
	}
	
	public static JDartParams constructJDartParams() {
		String  classpathStr = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
				app = "libs/jdart/jpf.properties",
				site = "libs/jpf.properties",
				mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain",
				className = "org.apache.commons.math.distribution.ZipfDistributionImpl",
				methodName = "cumulativeProbability",
				paramString = "(e:int)";

		mainEntry = "testdata.l2t.test.init.leastsquaresconverter.value.LeastSquaresConverterMain";
		className = "org.apache.commons.math.optimization.LeastSquaresConverter";
		methodName = "value";
		paramString = "(point:double[])";
		
		/** invalid type code: 64 */
		mainEntry = "testdata.l2t.test.init.graggbulirschstoerintegrator.trystep.GraggBulirschStoerIntegratorMain";
		className = "org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator";
		methodName = "tryStep";
		paramString = "(t0:double, y0:double[], s:double, k:int, scale:double[],f:double[][],yMiddle:double[],yEnd:double[],yTmp:double[] )";
		
		
		return constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site);
	}
	
	public static JDartParams constructJDartParams(String classpathStr, String mainEntry, String className, String methodName, String paramString,
			String app, String site) {
		
		long minFree = 20*(1024<<10); // min free memory
		long timeLimit = 30 * 1000;
		
		JDartParams params = new JDartParams();
		params.setAppProperties(app);
		params.setClassName(className);
		params.setClasspathStr(classpathStr);
		params.setMainEntry(mainEntry);
		params.setMethodName(methodName);
		params.setMinFree(minFree);
		params.setParamString(paramString);
		params.setSiteProperties(site);
		params.setTimeLimit(timeLimit);
		
		return params;
	}
}
