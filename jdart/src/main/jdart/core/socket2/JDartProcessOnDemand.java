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

import config.IJDartSettings;
import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.core.util.TaskManager;
import jdart.model.TestInput;
import sav.strategies.vm.VMConfiguration;

public class JDartProcessOnDemand {
	private IJDartSettings settings;
	private static Logger log = LoggerFactory.getLogger(JDartProcessOnDemand.class);

	public JDartProcessOnDemand(IJDartSettings settings) {
		this.settings = settings;
	}

	public List<TestInput> run(JDartParams jdartParams,String jdartInitTc) {
		TaskManager.printCurrentJVM();
		String[] args = new String[] { 
				jdartParams.getClasspathStr(), jdartParams.getMainEntry(),
				jdartParams.getClassName(), jdartParams.getMethodName(), jdartParams.getParamString(),
				jdartParams.getAppProperties(), jdartParams.getSiteProperties(),
				jdartParams.getOnDemandSiteProperties(),
				""+jdartParams.getExploreNode(), ""+jdartParams.getExploreBranch(),
				jdartInitTc};
		List<TestInput> list = null;
		try {
			list = exec(JDartOnDemandClient.class, args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.debug("JDart return 0 test case");
		}
		if (list != null) {
			log.debug("JDart return " + list.size() +" test cases");
		}else {
			log.debug("JDart return null");
		}
		return list;
	}

	public List<TestInput> exec(Class klass, String[] args) throws IOException, InterruptedException {
		String classpathStr, mainEntry, targetClass, methodName, paramString, app, site, onDemandSite, node, branch, jdartInitTc;
		int idx = 0;
		classpathStr = args[idx++];
		mainEntry = args[idx++];
		targetClass = args[idx++];
		methodName = args[idx++];
		paramString = args[idx++];
		app = args[idx++];
		site = args[idx++];
		onDemandSite = args[idx++];
		node = args[idx++];
		branch = args[idx++];
		jdartInitTc= args[idx++];

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		
		String classpath = settings.getRuntimeCP();
					// System.getProperty("java.class.path"); // only valid when running in single project, invalid when invoked by eclipse plug-in 

		String className = klass.getCanonicalName();
		ServerSocket s = null;
		Socket socket = null;
		List<TestInput> list = new ArrayList<>();
		long pid = -1;
		Process process = null;
		try {
			int tryTime = 1;
			int port = VMConfiguration.findFreePort();
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
						log.debug("fail to find avaiable port");
						throw exception;
					}
				}
			}
			s.setSoTimeout(JDartCore.socketWaiteTime());
			log.debug("IntraSocket Start:" + s);
			log.debug("JDart method : "+targetClass+"."+methodName);
			log.debug("JDart mainEntry : "+mainEntry);
			log.debug("JDart param : "+paramString);
			log.debug("JDart node:"+node);
			log.debug("JDart branch:"+branch);
			ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms1024m", "-Xmx9000m", "-cp", classpath, className, classpathStr, mainEntry,
					targetClass, methodName, paramString, app, site, onDemandSite, node, branch, jdartInitTc, ""+port);
			if (settings.getProcessDirectory() != null) {
				builder.directory(new File(settings.getProcessDirectory()));
			}
			process = builder.start();
			final Process shareProcess = process;
//			pid = TaskManager.getPid(process);
			log.debug("" + pid);
			log.debug(ManagementFactory.getRuntimeMXBean().getName());
			Thread ioThread = new Thread() {
				@Override
				public void run() {
					try {
						JDartProcess.printerInfo(shareProcess);
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
			};
			ioThread.start();
			socket = s.accept();
			log.debug("IntraConnection accept socket:" + socket);
			DataInputStream dIn = new DataInputStream(socket.getInputStream());
			JDartProcess.parseList(list, dIn);
			log.debug("receive lines size :" + list.size());
		} catch (Exception e) {
			log.debug(e.toString());
		} finally {
			log.debug("kill pid : " + pid);
//			LLT: it is too dangerous to kill process using task manager, we should only destroy process instead.
			if (process != null && process.isAlive()) {
				process.destroyForcibly();
			}
//			TaskManager.kill(pid);
			log.debug("IntraServer Close.....");
			try {
				if (socket != null) {
					socket.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (Exception e2) {
				log.debug(e2.getStackTrace().toString());
			}
		}
		return list.size() == 0 ? null : list;

		// return process.exitValue();
	}
	
}
