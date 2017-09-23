/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core.value.generator;

/**
 * @author LLT
 *
 */
public class EnumContainer {

	 public enum RoundingMode {

	        /** Rounds toward zero (truncation). */
	        ROUND_DOWN,

	        /** Rounds away from zero if discarded digit is non-zero. */
	        ROUND_UP,

	        /** Rounds towards nearest unless both are equidistant in which case it rounds away from zero. */
	        ROUND_HALF_UP,

	        /** Rounds towards nearest unless both are equidistant in which case it rounds toward zero. */
	        ROUND_HALF_DOWN,

	        /** Rounds towards nearest unless both are equidistant in which case it rounds toward the even neighbor.
	         * This is the default as  specified by IEEE 854-1987
	         */
	        ROUND_HALF_EVEN,

	        /** Rounds towards nearest unless both are equidistant in which case it rounds toward the odd neighbor.  */
	        ROUND_HALF_ODD,

	        /** Rounds towards positive infinity. */
	        ROUND_CEIL,

	        /** Rounds towards negative infinity. */
	        ROUND_FLOOR;

	    }
}
