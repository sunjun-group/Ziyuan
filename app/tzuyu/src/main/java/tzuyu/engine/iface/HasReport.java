/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.iface;

import tzuyu.engine.model.dfa.Alphabet;
import lstar.ReportHandler;

/**
 * @author LLT
 * 
 */
public interface HasReport<A extends Alphabet> {

	/**
	 * report all useful output from the process. this function will we put this
	 * separate to make the flow clearer.
	 */
	public void report(ReportHandler<A> reporter);

}
