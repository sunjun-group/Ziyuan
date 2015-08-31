/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;

/**
 * @author LLT
 *
 */
public class SliceBkpByPackagesCollector extends SliceBreakpointCollector {
	private List<String> analyzedPackages; 
	private Set<String> acceptedClasses = new HashSet<String>();
	
	public SliceBkpByPackagesCollector(Collection<String> analyzedPackages) {
		this.analyzedPackages = new ArrayList<String>(analyzedPackages);
	}

	@Override
	protected boolean isAccepted(Instruction instruction) {
		String clazzName = instruction.getMethod().getReadClass().getName();
		if (acceptedClasses.contains(clazzName)) {
			return true;
		}
		for (String pkg : analyzedPackages) {
			if (clazzName.startsWith(pkg)) {
				acceptedClasses.add(clazzName);
				return true;
			}
		}
		return false;
	}
}
