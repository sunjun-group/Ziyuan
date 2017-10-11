package jdart.core.socket2;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.core.util.TaskManager;
import jdart.model.TestInput;

public class JDartProcessOnDemand {

	private static Logger log = LoggerFactory.getLogger(JDartProcessOnDemand.class);


	public static void main(String[] args) {
		new JDartProcessOnDemand().run(JDartParams.defaultOnDemandJDartParams());
	}

	public List<TestInput> run(JDartParams jdartParams) {
		TaskManager.printCurrentJVM();
		String[] args = new String[] { jdartParams.getClasspathStr(), jdartParams.getMainEntry(),
				jdartParams.getClassName(), jdartParams.getMethodName(), jdartParams.getParamString(),
				jdartParams.getAppProperties(), jdartParams.getSiteProperties(),
				""+jdartParams.getExploreNode(), ""+jdartParams.getExploreBranch()};
		List<TestInput> list = null;
		try {
			list = JDartProcessOnDemand.exec(JDartOnDemandClient.class, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("JDart return 0 test case");
		}
		if (list != null) {
			log.info("JDart return " + list.size() +" test cases");
		}else {
			log.info("JDart return null");
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
		
		String classpath = JDartCore.getRuntimeCP();
					// System.getProperty("java.class.path"); // only valid when running in single project, invalid when invoked by eclipse plug-in 

		String className = klass.getCanonicalName();
		ServerSocket s = null;
		Socket socket = null;
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
						JDartProcess.printerInfo(process);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			ioThread.start();
			socket = s.accept();
			log.info("IntraConnection accept socket:" + socket);
			DataInputStream dIn = new DataInputStream(socket.getInputStream());
			JDartProcess.parseList(list, dIn);
			log.info("receive lines size :" + list.size());
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		} finally {
			log.info("kill pid : " + pid);
			TaskManager.kill(pid);
			log.info("IntraServer Close.....");
			try {
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
	
}
