/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface IDividerProcessor<R> {
	
	R process(Divider divider, List<String> labels, boolean round);
	
	R process(Divider divider, List<String> labels, boolean round, int num);
	
}
