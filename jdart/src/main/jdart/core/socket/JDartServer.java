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

import org.antlr.grammar.v3.CodeGenTreeWalker.element_action_return;
import org.eclipse.ui.internal.progress.WorkbenchSiteProgressService.SiteUpdateJob;

import com.sun.tools.classfile.StackMapTable_attribute.append_frame;

import jdart.core.JDartParams;

public class JDartServer {
	public List<String> run(JDartParams jDartParams) {
		ServerSocket s = null;
		Socket socket = null;
		BufferedReader br = null;
		List<String> list = null;
		Thread work = null;
		try {
			s = new ServerSocket(JDartProcess.JDART_INTER_PORT);
			System.out.println("InterSocket Start:"+s);
//			Runtime.getRuntime().exec("java -cp E:/hairui/git/Ziyuan/jdart/bin jdart.core.socket.JdartClient");

			s.setSoTimeout(40*1000);
			work = new Thread() {				
				@Override
				public void run() {
					new JDartClient().run(jDartParams);					
				}
			};
			work.start();
			System.err.println("inter server Start......");
			Thread.sleep(1000);
			socket = s.accept();
			System.out.println("InterConnection accept socket:"+socket);
			list = new ArrayList<>();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String str = br.readLine();
                if(str.equals("END")){  
                    break;  
                }  
				list.add(str);
			}
			System.err.println("jdart result size :"+list.size());
		}catch (Exception e) {
			work.stop();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			System.err.println("InterServer Close.....");
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
		
	public static void main(String[] args) {
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
			System.out.println("Socket=" + socket);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())));
			if (result != null) {
				 for (int i = 0; i < result.size(); i++) {  
		                pw.println(result.get(i));  
		                pw.flush();  
		        }	
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
		System.exit(0);
	}
	
	public static JDartParams constructJDartParams() {
		String  classpathStr = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
				app = "libs/jdart/jpf.properties",
				site = "libs/jpf.properties",
				mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain",
				className = "org.apache.commons.math.distribution.ZipfDistributionImpl",
				methodName = "cumulativeProbability",
				paramString = "(e:int)";
		
		/** simple return result */
		className = "org.apache.commons.math.util.FastMath";
		mainEntry = className;
		methodName = "scalb2";
		paramString = "(a:float, i:int)";
		
		/** run time out */
//		mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain";
//		className = "org.apache.commons.math.distribution.ZipfDistributionImpl";
//		methodName = "cumulativeProbability";
//		paramString = "(e:int)";
//
		mainEntry = "testdata.l2t.test.init.poissondistributionimpl.probability.PoissonDistributionImplMain";
		className = "org.apache.commons.math.distribution.PoissonDistributionImpl";
		methodName = "probability";
		paramString = "(e:int)";
		
//		/** long return result */
//		mainEntry = "testdata.l2t.test.init.bigmatriximpl.getsubmatrix.BigMatrixImplMain";
//		className = "org.apache.commons.math.linear.BigMatrixImpl";
//		methodName = "getSubMatrix";
//		paramString = "(r:int[], c:int[])";
		
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
