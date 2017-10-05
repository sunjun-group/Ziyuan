/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.io.IOUtils;

import jdart.model.TestInput;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;

/**
 * @author LLT
 *
 */
public class JDartResult implements Serializable {
	private static final long serialVersionUID = 6134006353166948700L;

	public static void saveToFile(List<TestInput> result, File file, int limitResult) throws IOException {
		List<TestInput> selectedResult = result;
		if (CollectionUtils.getSize(selectedResult) > limitResult) {
			selectedResult = Randomness.randomSubList(selectedResult, limitResult);
		}
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file, true);
			convertToBytes(selectedResult, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<TestInput> readFrom(File resultFile) throws IOException, ClassNotFoundException {
		FileInputStream input = null;
		try {
			input = new FileInputStream(resultFile);
			return (List<TestInput>) convertFromBytes(null, input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public static void convertToBytes(Object object, OutputStream outstr) throws IOException {
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(outstr);
			out.writeObject(object);
		} finally {
			out.close();
		}
	}

	public static Object convertFromBytes(byte[] bytes, InputStream instr) throws IOException, ClassNotFoundException {
		Object object;
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(instr);
			object = in.readObject();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return object;
	}
}
