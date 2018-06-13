package cfg.utils;

import cfg.BranchRelationship;
import cfg.DecisionBranchType;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class ControlRelationship {
	
	public static short getRelationshipToDecisionPrecessor(int branchIdx) {
		return (short) (0b0011 << (branchIdx * 2));
	}
	
	public static short getRelationshipToDecisionPrecessor(DecisionBranchType branchType) {
		return getRelationshipToDecisionPrecessor(branchType.ordinal());
	}

	public static short mergeBPD(short currentRelationship, short relationshipToDecisionNode) {
		return (short) (currentRelationship | relationshipToDecisionNode);
	}

	public static boolean isPostDominance(short decisionControlRelationship, int branchTotal) {
		return decisionControlRelationship == postDominance(branchTotal);
	}
	
	public static short postDominance(int branchTotal) {
		short pd0 = 0b11;
		short pd = pd0;
		for (int i = 2; i <= branchTotal; i++) {
			pd = (short) (pd | (pd0 << ((i - 1) * 2)));
		}
		return pd;
	}
	
	public static short fullControlDependency(int branchTotal) {
		short pd0 = 0b01;
		short pd = pd0;
		for (int i = 2; i <= branchTotal; i++) {
			pd = (short) (pd | (pd0 << ((i - 1) * 2)));
		}
		return pd;
	}
	
	public static short toControlDependency(short relationship, int branchTotal) {
		short r = 0;
		for (int i = 1; i <= branchTotal; i++) {
			short bpd = (short) (0b11 << ((i - 1) * 2));
			if ((bpd & relationship) > 0) {
				r |=  (short) (0b01 << ((i - 1) * 2));
			}
		}
		return r;
	}
	
	public static String toString(short decisionControlRelationship) {
		switch (decisionControlRelationship) {
		case 0b11:
			return "BPD_TRUE";
		case 0b1100:
			return "BPD_FALSE";
		case 0b1111:
			return "PD";
		case 0b0100:
			return "CD_FALSE";
		case 0b0001:
			return "CD_TRUE";
		case 0b0101:
			return "CD_TRUE_FALSE";
		case 0b0111:
			return "CD_FALSE_BPD_TRUE";
		case 0b1101:
			return "CD_TRUE_BPD_FALSE";
		}
		throw new IllegalArgumentException("Invalid decision control relationship: " + decisionControlRelationship);
	}
	
	public static String toString(short relationship, int branchTotal) {
		if (isPostDominance(relationship, branchTotal)) {
			return "PD";
		} 
		if (relationship == fullControlDependency(branchTotal)) {
			return "CD";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= branchTotal; i++) {
			short bpd = (short) (0b11 << ((i - 1) * 2));
			int branchRel = bpd & relationship;
			if (branchRel == bpd) {
				sb.append("BPD_").append(i - 1).append("_");
			} else if (branchRel > 0) {
				sb.append("CD_").append(i - 1).append("_");
			}
		}
		String str = sb.toString();
		if (str.endsWith("_")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public static short weakMerge(short thisRelationship, short thatRelationship) {
		short newRelationship = (short) (thisRelationship | thatRelationship);
		return newRelationship; 
	}

	private static final short BPD_TRUE = 0b11;
	private static final short BPD_FALSE = 0b1100;
	private static final short PD = 0b1111; // Post-Dominance
	private static final short CD_TRUE = 0b0001; // Control dependence
	private static final short CD_FALSE = 0b0100;
	private static final short CD_TRUE_FALSE = 0b0101;
	private static final short CD_TRUE_BPD_FALSE = 0b1101;
	private static final short CD_FALSE_BPD_TRUE = 0b0111;
	
	@Deprecated
	public static BranchRelationship getBranchRelationship(short decisionControlRelationship) {
		if (CollectionUtils.existIn(decisionControlRelationship, BPD_TRUE, CD_TRUE)) {
			return BranchRelationship.TRUE;
		} 
		if (CollectionUtils.existIn(decisionControlRelationship, BPD_FALSE, CD_FALSE)) {
			return BranchRelationship.FALSE;
		}
		return BranchRelationship.TRUE_FALSE;
	}

	@Deprecated
	public static boolean isTrueFalseRelationship(short decisionControlRelationship) {
		return CollectionUtils.existIn(decisionControlRelationship, PD, CD_TRUE_FALSE, CD_FALSE_BPD_TRUE, CD_TRUE_BPD_FALSE);
	}

}
