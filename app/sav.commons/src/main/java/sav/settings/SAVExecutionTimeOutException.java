package sav.settings;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;

/**
 * When some process executes more than a limit time (see SAVSettings.exeuctionTimeout), 
 * such an exception should be thrown.
 * @author Yun Lin
 *
 */
public class SAVExecutionTimeOutException extends SavException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7458090025653213056L;
	
	public SAVExecutionTimeOutException(){
		super(ModuleEnum.UNSPECIFIED);
	}
	
	public SAVExecutionTimeOutException(String msg){
		super(ModuleEnum.UNSPECIFIED, msg);
	}
}
