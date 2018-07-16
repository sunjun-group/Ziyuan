package sav.common.core.utils;

/**
 * @author LLT
 *
 */
public interface IExecutionTimer {

	boolean run(Runnable target, long timeout);

}
