/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class VariablesExtractorResult {
	private List<BreakpointResult> result;

	public VariablesExtractorResult() {
		result = new ArrayList<VariablesExtractorResult.BreakpointResult>();
	}

	public VariablesExtractorResult(Collection<BreakpointResult> values) {
		this();
		for (BreakpointResult brkr : values) {
			if (brkr != null) {
				result.add(brkr);
			}
		}
	}

	public void addBreakpointResult(BreakpointResult bkpRes) {
		result.add(bkpRes);
	}
	
	@Override
	public String toString() {
		return "VariablesExtractorResult \n\t[result=" + result + "]";
	}

	public static class BreakpointResult {
		private BreakPoint breakpoint;
		private List<BreakpointValue> failValues;
		private List<BreakpointValue> passValues;

		public BreakpointResult(BreakPoint breakPoint) {
			this.breakpoint = breakPoint;
			failValues = new ArrayList<BreakpointValue>();
			passValues = new ArrayList<BreakpointValue>();
		}

		public BreakPoint getBreakpoint() {
			return breakpoint;
		}

		public void add(List<VarValue> vals, boolean forPassTcs) {
			if (forPassTcs) {
				passValues.add(new BreakpointValue(vals));
			} else {
				failValues.add(new BreakpointValue(vals));
			}
		}
		
		public List<BreakpointValue> getFailValues() {
			return failValues;
		}
		
		public List<BreakpointValue> getPassValues() {
			return passValues;
		}

		@Override
		public String toString() {
			return "BreakpointResult \n\t[breakpoint=" + breakpoint
					+ ", \n\t\tfailValues=" + failValues + ", \n\t\tpassValues="
					+ passValues + "]";
		}
	}
	
	public List<BreakpointResult> getResult() {
		return result;
	}
	
	public static class BreakpointValue {
		private List<VarValue> values;
		
		public BreakpointValue(List<VarValue> values) {
			this.values = values;
		}
		
		public List<VarValue> getValues() {
			return values;
		}
		
		@Override
		public String toString() {
			return StringUtils.toString(values, "null");
		}
	}
	
	public static class VarValue {
		private String typeSignature;
		private String value;
		private String varCode;

		public VarValue(String code, String value) {
			this.varCode = code;
			this.value = value;
		}

		public String getVarCode() {
			return varCode;
		}

		public void setVarCode(String varCode) {
			this.varCode = varCode;
		}

		public String getTypeSignature() {
			return typeSignature;
		}

		public void setTypeSignature(String typeSignature) {
			this.typeSignature = typeSignature;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("%s = %s", varCode, value);
		}
	}
}
