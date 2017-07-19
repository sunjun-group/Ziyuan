package jdart.core.socket2;

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
			s = new ServerSocket(8989);
			System.out.println("ServerSocket Start:"+s);

			s.setSoTimeout(40*1000);
			work = new Thread() {				
				@Override
				public void run() {
					new JDartClient().run(jDartParams, 8989);					
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
		new JDartServerSingle().run(JDartServerSingle.constructJDartParams());
	}
	
	public static JDartParams constructJDartParams() {
		String  classpathStr = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
				app = "libs/jdart/jpf.properties",
				site = "libs/jpf.properties",
				mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain",
				className = "org.apache.commons.math.distribution.ZipfDistributionImpl",
				methodName = "cumulativeProbability",
				paramString = "(e:int)";

		mainEntry = "testdata.l2t.init.chisquaretestimpl.chisquare.ChiSquareTestImplMain";
		className = "org.apache.commons.math.stat.inference.ChiSquareTestImpl";
		methodName = "chiSquare";
		paramString = "(expected:double[], oberservd:long[])";

		mainEntry = "testdata.l2t.init.onewayanovaimpl.anovastats.OneWayAnovaImplMain";
		className = "org.apache.commons.math.stat.inference.OneWayAnovaImpl";
		methodName = "anovaStats";
		paramString = "(expected:double[], oberservd:long[])";

		mainEntry = "testdata.l2t.init.onewayanovaimpl.anovastats.OneWayAnovaImplMain";
		className = "org.apache.commons.math.util.OpenIntToFieldHashMap";
		methodName = "findInsertionIndex";
		paramString = "(keys:int[], states:byte[], key:int, mask:int)";
		
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
