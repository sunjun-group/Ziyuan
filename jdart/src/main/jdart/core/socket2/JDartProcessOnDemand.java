package jdart.core.socket2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartParams;
import jdart.core.util.ByteConverter;
import jdart.core.util.TaskManager;
import jdart.model.TestInput;

public class JDartProcessOnDemand {

	private static Logger log = LoggerFactory.getLogger(JDartProcessOnDemand.class);

	public List<TestInput> run(JDartParams jdartParams) {
		TaskManager.printCurrentJVM();
		String[] args = new String[] { jdartParams.getClasspathStr(), jdartParams.getMainEntry(),
				jdartParams.getClassName(), jdartParams.getMethodName(), jdartParams.getParamString(),
				jdartParams.getAppProperties(), jdartParams.getSiteProperties(),
				""+jdartParams.getExploreNode(), ""+jdartParams.getExploreBranch()};
		List<TestInput> list = null;
		try {
			list = JDartProcessOnDemand.exec(JDartOnDemandClient.class, args);
			if (list != null) {
				log.info("JDart return " + list.size() +" test cases");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("JDart return 0 test case");
		}
		return list;
	}

	public static List<TestInput> exec(Class klass, String[] args) throws IOException, InterruptedException {
		String classpathStr, mainEntry, targetClass, methodName, paramString, app, site, node, branch;
		classpathStr = args[0];
		mainEntry = args[1];
		targetClass = args[2];
		methodName = args[3];
		paramString = args[4];
		app = args[5];
		site = args[6];
		node = args[7];
		branch = args[8];

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath =  System.getProperty("java.class.path");
//		"E:\\hairui\\git\\Ziyuan\\jdart\\bin;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.junit_4.12.0.v201504281640\\junit.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.hamcrest.core_1.3.0.v201303031735.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\aima-core.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\antlr-3.4-complete.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\automaton.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\bcel.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\choco-1_2_04.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\choco-solver-2.1.1-20100709.142532-2.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\classloader_specific_tests.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\com.microsoft.z3.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\commons-lang-2.4.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\commons-math-1.2.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\coral.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\grappa.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\green.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\gson-2.2.4.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\guava-14.0.1.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\hampi.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\iasolver.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jaxen.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jedis-2.0.0.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jpf-symbc-annotations.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jpf-symbc-classes.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jpf-symbc.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\JSAP-2.1.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\libcvc3.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\opt4j-2.4.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\org.sat4j.core.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\org.sat4j.pb.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\proteus.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\scale.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\solver.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\ST-4.0.5.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\Statemachines.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\STPJNI.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\string.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\yicesapijava.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jconstraints-z3-0.9.1-SNAPSHOT.jar;E:\\hairui\\git\\Ziyuan\\jdart\\libs\\jconstraints-0.9.2-SNAPSHOT.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.ui_3.107.0.v20150507-1945.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.swt_3.104.1.v20150825-0743.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.swt.win32.win32.x86_64_3.104.1.v20150825-0743.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.jface_3.11.0.v20150602-1400.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.core.commands_3.7.0.v20150422-0725.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.ui.workbench_3.107.0.v20150825-2206.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.e4.ui.workbench3_0.13.0.v20150422-0725.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.core.runtime_3.11.1.v20150903-1804.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\javax.annotation_1.2.0.v201401042248.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\javax.inject_1.0.0.v20091030.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.osgi_3.10.101.v20150820-1432.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.osgi.compatibility.state_1.0.100.v20150402-1551.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.equinox.common_3.7.0.v20150402-1709.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.core.jobs_3.7.0.v20150330-2103.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.core.runtime.compatibility.registry_3.6.0.v20150318-1505\\runtime_registry_compatibility.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.equinox.registry_3.6.0.v20150318-1503.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.equinox.preferences_3.5.300.v20150408-1437.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.core.contenttype_3.5.0.v20150421-2214.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.equinox.app_1.3.300.v20150423-1356.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\target\\classes;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\target\\test-classes;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\javailp-1.2a.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\junit-4.12.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\commons-logging-1.1.3.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\commons-collections-3.2.1.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\javaparser-1.0.11.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\commons-cli-1.2.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\commons-csv-1.0.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\commons-io-1.3.2.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\commons-lang-2.6.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\hamcrest-core-1.3.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\log4j-1.2.17.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\slf4j-api-1.7.12.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\slf4j-log4j12-1.7.12.jar;E:\\hairui\\git\\Ziyuan\\app\\sav.commons\\lib\\tools.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.jdt.core_3.11.1.v20150902-1521.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.jdt.compiler.apt_1.2.0.v20150514-0146.jar;E:\\hairui\\eclipse-java-mars-clean\\eclipse\\plugins\\org.eclipse.jdt.compiler.tool_1.1.0.v20150513-2007.jar";// System.getProperty("java.class.path");
		String className = klass.getCanonicalName();
		ServerSocket s = null;
		Socket socket = null;
		BufferedReader br = null;
		List<TestInput> list = new ArrayList<>();
		long pid = -1;
		try {
			int tryTime = 1;
			int port = 8989;
			while(tryTime <= 1000){
				try{
					s = new ServerSocket(port);
					break;
				}catch(java.net.BindException exception){
					tryTime++;
					port = (int) (Math.random()*1000)+8000;
					if (tryTime++ <= 1000) {
						continue;
					}else{
						log.info("fail to find avaiable port");
						throw exception;
					}
				}
			}
			s.setSoTimeout(40 * 1000);
			log.info("IntraSocket Start:" + s);
			log.info("JDart method : "+targetClass+"."+methodName);
			log.info("JDart mainEntry : "+mainEntry);
			log.info("JDart param : "+paramString);
			ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms1024m", "-Xmx9000m", "-cp", classpath, className, classpathStr, mainEntry,
					targetClass, methodName, paramString, app, site, node, branch, ""+port);
//			builder.directory(new File("E:\\hairui\\git\\Ziyuan\\jdart"));
			Process process = builder.start();
			pid = TaskManager.getPid(process);
			log.info("" + pid);
			log.info(ManagementFactory.getRuntimeMXBean().getName());
			Thread ioThread = new Thread() {
				@Override
				public void run() {
					try {
						printerInfo(process);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			ioThread.start();
			socket = s.accept();
			log.info("IntraConnection accept socket:" + socket);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			parseList(list, br);
			log.info("receive lines size :" + list.size());
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		} finally {
			log.info("kill pid : " + pid);
			TaskManager.kill(pid);
			log.info("IntraServer Close.....");
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
		return list.size() == 0 ? null : list;

		// return process.exitValue();
	}

	private static void printerInfo(Process process) throws IOException {
		InputStream is = process.getInputStream();
		BufferedReader ibr = new BufferedReader(new InputStreamReader(is, "utf-8"));
		InputStream iserr = process.getErrorStream();
		BufferedReader brerr = new BufferedReader(new InputStreamReader(iserr, "utf-8"));
		String string = "", err = "";
		try {
			while ((string = ibr.readLine()) != null) {
				System.out.println(string);
			}

			while ((err = brerr.readLine()) != null) {
				System.err.println(err);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ibr.close();
			is.close();
			brerr.close();
			ibr.close();
		}

	}

	public static void parseList(List<TestInput> list, BufferedReader br) throws IOException, ClassNotFoundException {
		StringBuffer sb  = new StringBuffer();
		while(true){
			String str = br.readLine();
            if(str.equals("END")){  
                break;  
            }else if (str.equals("line end")) {
            	sb.delete(sb.length()-1, sb.length());
            	byte[] bytes = sb.toString().getBytes();
            	log.info("receive byte[] : "+bytes.length);
				list.add((TestInput)(ByteConverter.convertFromBytes(bytes)));
				sb = new StringBuffer();
			}else {
				sb.append(str);
				sb.append("\n");
			}
		}
		
	}
}
