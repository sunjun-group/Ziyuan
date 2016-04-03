package microbat.codeanalysis.runtime;

/**
 * This class is used to locate all the executor classes in this project
 * @author "linyun"
 *
 */
public abstract class Executor {
	protected String[] stepWatchExcludes = { "java.*", "javax.*", "sun.*", "com.sun.*", "org.junit.*", "junit.*", "junit.framework.*"};
}
