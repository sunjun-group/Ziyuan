/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.dto;

import java.util.ArrayList;
import java.util.List;

import tzuyu.engine.model.Sequence;

/**
 * @author LLT
 *
 */
public class TesterResult {
	private List<Sequence> passSeqs;
	private List<Sequence> failSeqs;
	
	public TesterResult() {
		passSeqs = new ArrayList<Sequence>();
		failSeqs = new ArrayList<Sequence>();
	}
	
	public void add(Sequence seq, boolean pass) {
		if (pass) {
			addPass(seq);
		} else {
			addFail(seq);
		}
	}
	
	public void addPass(Sequence seq) {
		passSeqs.add(seq);
	}
	
	public void addFail(Sequence seq) {
		failSeqs.add(seq);
	}
	
	public int getTotal() {
		return passSeqs.size() + failSeqs.size();
	}
}
