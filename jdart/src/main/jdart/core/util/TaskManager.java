package jdart.core.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;

import sun.tools.jconsole.LocalVirtualMachine;

public class TaskManager {
	private static Logger log = LoggerFactory.getLogger(TaskManager.class);
	public static long getPid(Process process){
		long pid = -1;
		if (process.getClass().getName().equals("java.lang.Win32Process") ||
				   process.getClass().getName().equals("java.lang.ProcessImpl")) {
			  /* determine the pid on windows plattforms */
			  try {
			    Field f = process.getClass().getDeclaredField("handle");
			    f.setAccessible(true);				
			    long handl = f.getLong(process);
			    
			    Kernel32 kernel = Kernel32.INSTANCE;
			    W32API.HANDLE handle = new W32API.HANDLE();
			    handle.setPointer(Pointer.createConstant(handl));
			    pid = kernel.GetProcessId(handle);
			  } catch (Throwable e) {
				  log.info(e.toString());
				  e.printStackTrace();
			  }
		}else if(process.getClass().getName().equals("java.lang.UNIXProcess")) {
			  /* get the PID on unix/linux systems */
			  try {
			    Field f = process.getClass().getDeclaredField("pid");
			    f.setAccessible(true);
			    pid = f.getInt(process);
			  } catch (Throwable e) {
				  log.info(e.toString());
				  e.printStackTrace();
			  }
		}
		return pid;
	}
	
	public static void kill(long pid) throws IOException {
		String cmd = "taskkill /F /PID " + pid;
		Runtime.getRuntime().exec(cmd);
	}
	
	public static void printCurrentJVM() {
		Map<Integer, LocalVirtualMachine> virtualMachines = LocalVirtualMachine.getAllVirtualMachines();
		for (final Entry<Integer, LocalVirtualMachine> entry : virtualMachines.entrySet()) {
	        System.out.println(entry.getKey() + " : " + entry.getValue().displayName());
	    }
		if (virtualMachines.size()>=4) {
			System.out.println("");
		}
	}
	public static void main(String[] args) {
		try {
			TaskManager.kill(20192);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
