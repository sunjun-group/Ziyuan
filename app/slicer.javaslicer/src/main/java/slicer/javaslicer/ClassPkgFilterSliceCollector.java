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
import java.util.List;
import java.util.Set;

import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;

/**
 * @author LLT
 *
 */
public class ClassPkgFilterSliceCollector extends ClassFilterSliceCollector {
	protected Set<String> analyzedPackages; 
	
	public ClassPkgFilterSliceCollector(Collection<String> analyzedClasses,
			List<String> analyzedPackages) {
		super(analyzedClasses);
		this.analyzedPackages = new HashSet<String>(analyzedPackages);
	}

	@Override
	protected boolean isAccepted(Instruction instruction) {
		String clazzName = instruction.getMethod().getReadClass().getName();
		if (analyzedClasses.contains(clazzName)) {
			return true;
		}
		for (String pkg : analyzedPackages) {
			if (clazzName.startsWith(pkg)) {
				analyzedClasses.add(clazzName);
				return true;
			}
		}
		return false;
	}
}
