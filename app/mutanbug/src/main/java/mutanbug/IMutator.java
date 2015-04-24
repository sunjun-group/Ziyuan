/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug;

import java.io.File;
import java.util.List;
import java.util.Map;

import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 *
 */
public interface IMutator {

	/**
	 * create new mutated java files at certain lines from the source code.
	 */
	<T extends ClassLocation> Map<T, List<File>> mutate(List<T> locations, String srcFolder);

}
