package cfg.utils;

import cfg.BranchRelationship;
import cfg.DecisionBranchType;

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
		String str = toString(decisionControlRelationship, 2);
		str = str.replace("_0", "_TRUE");
		str = str.replace("_1", "_FALSE");
		return str;
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

	@Deprecated
	public static BranchRelationship getBranchRelationship(short relationship) {
		if ((relationship & 0b0101) == 0b0101) {
			return BranchRelationship.TRUE_FALSE;
		}
		if ((relationship & 0b01) == 0b01) {
			return BranchRelationship.TRUE;
		}
		if ((relationship & 0b0100) == 0b0100) {
			return BranchRelationship.FALSE;
		}
		return null;
	}

	@Deprecated
	public static boolean isTrueFalseRelationship(short relationship) {
		return (relationship & 0b0101) == 0b0101;
	}

}
