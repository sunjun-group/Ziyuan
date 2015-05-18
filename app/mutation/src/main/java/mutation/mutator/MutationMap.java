/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation.mutator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sav.common.core.Logger;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.EnumUtils;

/**
 * @author LLT
 * 
 */
public class MutationMap {
	private Logger<?> log = Logger.getDefaultLogger();
	private Map<Enum<?>, List<Enum<?>>> muOpMap;
	
	public MutationMap(Map<String, List<String>> config) {
		this.muOpMap = OperatorUtils.buildMuOpMap(config);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<?>> List<T> getMutationOp(T operator) {
		List<T> muOps = new ArrayList<T>();
		for (Enum<?> muOp : CollectionUtils.nullToEmpty(muOpMap.get(operator))) {
			if (muOp.getDeclaringClass().equals(operator.getClass())) {
				muOps.add((T) muOp);
			} else {
				log.warn("Mismatch between operator and mutation operator types");
				log.warn("Operator=", EnumUtils.getFullName(operator), ", mutation operator=",
						EnumUtils.getFullName(muOp));
			}
		}
		return muOps;
	}

}
