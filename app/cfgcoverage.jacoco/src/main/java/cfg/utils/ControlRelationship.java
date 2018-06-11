package cfg.utils;

import cfg.BranchRelationship;
import cfg.DecisionBranchType;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class ControlRelationship {
	public static final short BPD_TRUE = 0b1000;
	public static final short BPD_FALSE = 0b0100;
	public static final short PD = 0b1100; // Post-Dominance
	public static final short CD_TRUE = 0b0010; // Control dependence
	public static final short CD_FALSE = 0b0001;
	public static final short CD_TRUE_FALSE = 0b0011;
	public static final short CD_TRUE_BPD_FALSE = 0b0110;
	public static final short CD_FALSE_BPD_TRUE = 0b1001;
	
	public static short getRelationshipToDecisionPrecessor(DecisionBranchType branchType) {
		switch (branchType) {
		case TRUE:
			return BPD_TRUE;
		case FALSE:
			return BPD_FALSE;
		}
		throw new IllegalArgumentException(StringUtils.toStringNullToEmpty(branchType));
	}

	public static short mergeBPD(short currentRelationship, short relationshipToDecisionNode) {
		if (currentRelationship == 0) {
			return relationshipToDecisionNode;
		}
		if ((currentRelationship & PD) < 0b0100) {
			throw new IllegalArgumentException("Invalid BPD relationship: " + currentRelationship);
		}
		return (short) (currentRelationship | relationshipToDecisionNode);
	}

	public static boolean isPostDominance(Short decisionControlRelationship) {
		return (PD & decisionControlRelationship) == PD;
	}

	public static String toString(short decisionControlRelationship) {
		switch (decisionControlRelationship) {
		case BPD_TRUE:
			return "BPD_TRUE";
		case BPD_FALSE:
			return "BPD_FALSE";
		case PD:
			return "PD";
		case CD_FALSE:
			return "CD_FALSE";
		case CD_TRUE:
			return "CD_TRUE";
		case CD_TRUE_FALSE:
			return "CD_TRUE_FALSE";
		case CD_FALSE_BPD_TRUE:
			return "CD_FALSE_BPD_TRUE";
		case CD_TRUE_BPD_FALSE:
			return "CD_TRUE_BPD_FALSE";
		}
		throw new IllegalArgumentException("Invalid decision control relationship: " + decisionControlRelationship);
	}

	public static short weakMerge(short thisRelationship, short thatRelationship) {
		short newRelationship = (short) (thisRelationship | thatRelationship);
		return newRelationship; 
	}

	public static BranchRelationship getBranchRelationship(short decisionControlRelationship) {
		if (CollectionUtils.existIn(decisionControlRelationship, BPD_TRUE, CD_TRUE)) {
			return BranchRelationship.TRUE;
		} 
		if (CollectionUtils.existIn(decisionControlRelationship, BPD_FALSE, CD_FALSE)) {
			return BranchRelationship.FALSE;
		}
		return BranchRelationship.TRUE_FALSE;
	}

	public static boolean isTrueFalseRelationship(short decisionControlRelationship) {
		return CollectionUtils.existIn(decisionControlRelationship, PD, CD_TRUE_FALSE, CD_FALSE_BPD_TRUE, CD_TRUE_BPD_FALSE);
	}
}
