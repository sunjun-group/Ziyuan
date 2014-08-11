/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.slicer;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.utils.SignatureUtils;

import java.util.List;

import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;

/**
 * @author LLT
 *
 */
public class BkpEntrypointMaker extends EntrypointMaker<BreakPoint> {

	public BkpEntrypointMaker(ClassLoaderReference loaderRef,
			IClassHierarchy cha, List<BreakPoint> classEntryPoints) {
		super(loaderRef, cha, classEntryPoints);
	}

	@Override
	protected String getMethodName(BreakPoint entry) {
		String methodSign = entry.getMethodSign();
		return SignatureUtils.extractMethodName(methodSign);
	}

	@Override
	protected Descriptor getMethodDescriptor(BreakPoint entry) {
		String methodSign = entry.getMethodSign();
		return createDescriptor(methodSign);
	}

	@Override
	protected String getClassRef(BreakPoint entry) {
		return SignatureUtils.getSignature(entry.getClassCanonicalName());
	}

}
