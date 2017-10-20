/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class EvosuitDataTransfer {

	@Test
	public void transfer() throws Exception {
		MappingExcelHandler handler = new MappingExcelHandler("/Users/lylytran/Projects/Ziyuan-branches/learntest-eclipse/evosuitor/evaluation/apache-common-math-2.2_merge.xlsx");
		handler.map("/Users/lylytran/Projects/Ziyuan-branches/learntest-eclipse/evosuitor/evaluation/apache-common-math-evo.xlsx");
	}
}
