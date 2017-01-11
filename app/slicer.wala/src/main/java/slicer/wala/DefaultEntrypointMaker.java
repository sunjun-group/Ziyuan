/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.wala;

import java.util.List;

import sav.common.core.utils.SignatureUtils;

import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;

/**
 * @author LLT
 *
 */
public class DefaultEntrypointMaker extends EntrypointMaker<String[]>{

	public DefaultEntrypointMaker(ClassLoaderReference loaderRef,
			IClassHierarchy cha, List<String[]> classEntryPoints) {
		super(loaderRef, cha, classEntryPoints);
	}

	@Override
	protected String getMethodName(String[] entry) {
		return SignatureUtils.extractMethodName(entry[1]);
	}

	@Override
	protected String getClassRef(String[] entry) {
		return entry[0];
	}
	
	@Override
	protected Descriptor getMethodDescriptor(String[] entry) {
		return createDescriptor(entry[1]);
	}

}
