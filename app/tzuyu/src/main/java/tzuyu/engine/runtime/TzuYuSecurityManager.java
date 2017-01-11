package tzuyu.engine.runtime;

import java.io.FileDescriptor;
import java.lang.reflect.ReflectPermission;
import java.net.InetAddress;
import java.security.Permission;

public class TzuYuSecurityManager extends SecurityManager {
	public enum Status {
		ON, OFF
	}

	public Status status;

	public TzuYuSecurityManager(Status sts) {
		this.status = sts;
	}

	@Override
	public void checkAccept(String host, int port) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkAccess(Thread t) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkAccess(ThreadGroup g) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkAwtEventQueueAccess() {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkConnect(String host, int port, Object context) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkConnect(String host, int port) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkCreateClassLoader() {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkDelete(String file) {
		if (status == Status.OFF)
			return;
		if (file == null)
			throw new NullPointerException();
		throw new SecurityException("checkDelete: "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkExec(String cmd) {
		if (status == Status.OFF)
			return;
		if (cmd == null)
			throw new NullPointerException();
		throw new SecurityException("checkExec: "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkExit(int stat) {
		if (status == Status.OFF)
			return;
		throw new SecurityException("checkExit: "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkLink(String lib) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkListen(int port) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkMemberAccess(Class<?> clazz, int which) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkMulticast(InetAddress maddr, byte ttl) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkMulticast(InetAddress maddr) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkPackageAccess(String pkg) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkPackageDefinition(String pkg) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkPermission(Permission perm, Object context) {
		if (status == Status.OFF)
			return;
		if (perm instanceof ReflectPermission) {
			// TzuYU allows reflection operations.
			return;
		}
		super.checkPermission(perm, context);
	}

	@Override
	public void checkPermission(Permission perm) {
		if (status == Status.OFF)
			return;
		if (perm instanceof ReflectPermission) {
			// TzuYU allows reflection operations.
			return;
		}
		super.checkPermission(perm);
	}

	@Override
	public void checkPrintJobAccess() {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkPropertiesAccess() {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkPropertyAccess(String key) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkRead(FileDescriptor fd) {
		if (status == Status.OFF)
			return;
		if (fd == null)
			throw new NullPointerException();
		throw new SecurityException("checkRead: "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkRead(String file, Object context) {
		if (status == Status.OFF)
			return;
		if (file == null)
			throw new NullPointerException();
		throw new SecurityException("checkRead(String,Object): "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkRead(String file) {
		if (status == Status.OFF)
			return;
		if (file == null)
			throw new NullPointerException();
	}

	@Override
	public void checkSecurityAccess(String target) {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkSetFactory() {
		if (status == Status.OFF)
			return;
	}

	@Override
	public void checkSystemClipboardAccess() {
		if (status == Status.OFF)
			return;
	}

	@Override
	public boolean checkTopLevelWindow(Object window) {
		if (status == Status.OFF)
			return true;
		if (window == null)
			throw new NullPointerException();
		throw new SecurityException("checkTopLevelWindow(Object): "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkWrite(FileDescriptor fd) {
		if (status == Status.OFF)
			return;
		if (fd == null)
			throw new NullPointerException();
		throw new SecurityException("checkWrite(FileDescriptor): "
				+ "TzuYU does not allow this operation by tested code");
	}

	@Override
	public void checkWrite(String file) {
		if (status == Status.OFF)
			return;
		if (file == null)
			throw new NullPointerException();
		throw new SecurityException("checkWrite(String): "
				+ "TzuYU does not allow this operation by tested code");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected int classDepth(String name) {
		return super.classDepth(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected int classLoaderDepth() {
		return super.classLoaderDepth();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected ClassLoader currentClassLoader() {
		return super.currentClassLoader();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Class<?> currentLoadedClass() {
		return super.currentLoadedClass();
	}

	@Override
	protected Class<?>[] getClassContext() {
		return super.getClassContext();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean getInCheck() {
		return super.getInCheck();
	}

	@Override
	public Object getSecurityContext() {
		return super.getSecurityContext();
	}

	@Override
	public ThreadGroup getThreadGroup() {
		return super.getThreadGroup();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean inClass(String name) {
		return super.inClass(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean inClassLoader() {
		return super.inClassLoader();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
