/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;

/**
 * @author LLT
 * 
 */
public class ClassFilterSliceCollector extends SliceBreakpointCollector {
	protected Set<String> analyzedClasses;

	public ClassFilterSliceCollector(Collection<String> analyzedClasses) {
		this.analyzedClasses = new HashSet<String>(analyzedClasses);
	}
	
	@Override
	protected boolean isAccepted(Instruction instruction) {
		String clazzName = instruction.getMethod().getReadClass().getName();
		return analyzedClasses.contains(clazzName);
	}
}
