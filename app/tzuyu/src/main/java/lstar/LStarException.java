/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package lstar;

import tzuyu.engine.model.dfa.Alphabet;

/**
 * @author LLT
 * 
 */
public class LStarException extends Exception {
	private static final long serialVersionUID = 1L;
	private Type type;
	private Alphabet newSigma;
	
	public LStarException(Type type) {
		if (type == Type.RestartLearning) {
			throw new AssertionError("Restart learning need a newSigma!!");
		}
		this.type = type;
	}
	
	public LStarException(Type type, Alphabet newSigma) {
		this.type = type;
		this.newSigma = newSigma;
	}
	
	public Type getType() {
		return type;
	}
	
	public Alphabet getNewSigma() {
		return newSigma;
	}
	
	public enum Type {
		RestartLearning,
		AlphabetEmptyAction;
	}
}
