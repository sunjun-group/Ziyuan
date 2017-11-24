package jdart.core.socket2;

import java.io.BufferedReader;
import java.io.DataInputStream;
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

import config.IJDartSettings;
import jdart.core.JDartCore;
import jdart.core.JDartParams;
import jdart.core.util.ByteConverter;
import jdart.core.util.TaskManager;
import jdart.model.TestInput;
import sav.strategies.vm.VMConfiguration;

public class JDartProcess {
	private static Logger log = LoggerFactory.getLogger(JDartProcess.class);
	private int solveCount = 0;
	private IJDartSettings settings;
	
	public JDartProcess(IJDartSettings settings) {
		this.settings = settings;
	}

	public List<TestInput> run(JDartParams jdartParams) {
		TaskManager.printCurrentJVM();
		String[] args = new String[] { jdartParams.getClasspathStr(), jdartParams.getMainEntry(),
				jdartParams.getClassName(), jdartParams.getMethodName(), jdartParams.getParamString(),
				jdartParams.getAppProperties(), jdartParams.getSiteProperties() };
		List<TestInput> list = null;
		try {
			list = exec(JDartClient.class, args);
			if (list != null) {
				log.debug("JDart return " + list.size() + " test cases");
				for (TestInput testInput : list) {
					log.debug(testInput.toString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.debug("JDart return 0 test case");
		}
		return list;
	}

	public List<TestInput> exec(Class klass, String[] args) throws IOException, InterruptedException {	
		String targetClassCP, mainEntry, targetClass, methodName, paramString, app, site;
		targetClassCP = args[0];
		mainEntry = args[1];
		targetClass = args[2];
		methodName = args[3];
		paramString = args[4];
		app = args[5];
		site = args[6];

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		
		String classpath = settings.getRuntimeCP();
//				 System.getProperty("java.class.path"); // only valid when running in single project, invalid when invoked by eclipse plug-in 
		
		String className = klass.getCanonicalName();
		ServerSocket s = null;
		Socket socket = null;
		BufferedReader br = null;
		List<TestInput> list = new ArrayList<>();
		long pid = -1;
		Process process = null;
		try {
			int tryTime = 1;
			int port = VMConfiguration.findFreePort(); //LLT: this is normal way to find free port.
			while (tryTime <= 1000) {
				try {
					s = new ServerSocket(port);
					break;
				} catch (java.net.BindException exception) {
					tryTime++;
					port = (int) (Math.random() * 1000) + 8000;
					if (tryTime++ <= 1000) {
						continue;
					} else {
						log.debug("fail to find avaiable port");
						throw exception;
					}
				}
			}
			s.setSoTimeout(JDartCore.socketWaiteTime());
			log.debug("IntraSocket Start:" + s);
			log.debug("JDart method : " + targetClass + "." + methodName);
			log.debug("JDart mainEntry : " + mainEntry);
			log.debug("JDart param : " + paramString);
			ProcessBuilder builder = new ProcessBuilder(javaBin, "-Xms1024m", "-Xmx9000m", "-cp", classpath, className,
					targetClassCP, mainEntry, targetClass, methodName, paramString, app, site, "" + port);
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
						printerInfo(shareProcess);
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
			};
			ioThread.start();
			socket = s.accept();
			log.debug("IntraConnection accept socket:" + socket);
			// br = new BufferedReader(new
			// InputStreamReader(socket.getInputStream()));
			// parseList(list, br);
			DataInputStream dIn = new DataInputStream(socket.getInputStream());
			solveCount = parseList(list, dIn);
			log.debug("receive lines size :" + list.size());
		} catch (Exception e) {
			log.debug(e.toString());
//			e.printStackTrace();
		} finally {
			log.debug("kill pid : " + pid);
//			LLT: it is too dangerous to kill process using task manager, we should only destroy process instead.
			if (process != null && process.isAlive()) {
				process.destroyForcibly();
			}
//			TaskManager.kill(pid); 
			log.debug("IntraServer Close.....");
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

	public static void printerInfo(Process process) throws IOException {
		InputStream is = process.getInputStream();
		BufferedReader ibr = new BufferedReader(new InputStreamReader(is, "utf-8"));
		InputStream iserr = process.getErrorStream();
		BufferedReader brerr = new BufferedReader(new InputStreamReader(iserr, "utf-8"));
		String string = "", err = "";
		try {
			while ((string = ibr.readLine()) != null) {
				log.debug(string);
			}

			while ((err = brerr.readLine()) != null) {
				log.debug(err);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			ibr.close();
			is.close();
			brerr.close();
			ibr.close();
		}

	}

	
//	public static void parseList(List<TestInput> list, BufferedReader br) throws IOException, ClassNotFoundException {
//		StringBuffer sb = new StringBuffer();
//		while (true) {
//			String str = br.readLine();
//			if (str.equals("END")) {
//				break;
//			} else if (str.equals("line end")) {
//				sb.delete(sb.length() - 1, sb.length());
//				byte[] bytes = sb.toString().getBytes();
//				log.debug("receive byte[] : " + bytes.length);
//				list.add((TestInput) (ByteConverter.convertFromBytes(bytes)));
//				sb = new StringBuffer();
//			} else {
//				sb.append(str);
//				sb.append("\n");
//			}
//		}
//
//	}

	public int getSolveCount() {
		return solveCount;
	}

	/**
	 * parse result into list and return solve count
	 * @param list
	 * @param dIn
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static int parseList(List<TestInput> list, DataInputStream dIn)
			throws IOException, ClassNotFoundException {
		int solveCount = dIn.readInt();
		while (true) {
			int length = dIn.readInt(); // read length of incoming message
			if (length > 0) {
				byte[] bytes = new byte[length];
				dIn.readFully(bytes, 0, bytes.length); // read the message
				log.debug("receive byte[] : " + bytes.length);
				list.add((TestInput) (ByteConverter.convertFromBytes(bytes)));
			} else if (length < 0) {
				break;
			}
		}
	return solveCount;

	}
}
