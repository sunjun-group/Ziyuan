package sav.utils;

/**
 * @author LLT
 *
 */
public interface IExecutionTimer {

	boolean run(TestRunner target, long timeout);

	void shutdown();

}
