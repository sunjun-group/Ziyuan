/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutanbug.commons.utils;

import java.io.File;
import java.io.IOException;

import sav.common.core.SavRtException;
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

	public static File createTempFolder(String string) {
		try {
			File file;
			file = File.createTempFile("mutatedSource", "");
			file.delete();
			file.mkdir();
			return file;
		} catch (IOException e) {
			throw new SavRtException("cannot create temp dir");
		}
	}
}
