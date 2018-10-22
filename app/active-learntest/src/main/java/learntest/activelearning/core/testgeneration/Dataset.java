/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.testgeneration;

import java.util.List;

/**
 * @author LLT
 *
 */
public class Dataset {
	private String id;
	private List<double[]> coveredData;
	private List<double[]> uncoveredData;

	public Dataset(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<double[]> getCoveredData() {
		return coveredData;
	}

	public void setCoveredData(List<double[]> coveredData) {
		this.coveredData = coveredData;
	}

	public List<double[]> getUncoveredData() {
		return uncoveredData;
	}

	public void setUncoveredData(List<double[]> uncoveredData) {
		this.uncoveredData = uncoveredData;
	}

}
