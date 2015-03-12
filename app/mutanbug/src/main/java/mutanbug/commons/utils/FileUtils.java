/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.commons.utils;

import java.io.File;

import sav.common.core.utils.Assert;

/**
 * @author LLT
 *
 */
public class FileUtils {
	
	public static File createFolder(File parent, String child) {
		Assert.assertTrue(parent.isDirectory(), parent + " is not a folder");
		File folder = new File(parent, child);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}
}
