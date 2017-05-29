/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.testdata;

import sav.common.core.ModuleEnum;

/**
 * @author LLT
 *
 */
public class SwitchSample {

	public String getName(ModuleEnum module) {
		String name = "unknown";
		while (true) {
			switch (module == null ? module : ModuleEnum.FALT_LOCALIZATION) {
			case FALT_LOCALIZATION:
				name = "fault localization";
				break;
			case SLICING:
				name = "slicing";
				break;
			default:
				break;
			}
			if (name != null) {
				break;
			}
		}
		return name;
	}
}
