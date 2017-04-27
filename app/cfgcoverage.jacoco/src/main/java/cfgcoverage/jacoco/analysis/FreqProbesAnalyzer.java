/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.analysis;

import org.jacoco.core.analysis.AbstractAnalyzer;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.internal.data.CRC64;
import org.jacoco.core.internal.flow.ClassProbesAdapter;
import org.objectweb.asm.ClassVisitor;

/**
 * @author LLT
 *
 */
public class FreqProbesAnalyzer extends AbstractAnalyzer {
	private final CfgCoverageBuilder coverage;

	public FreqProbesAnalyzer(ExecutionDataStore executionData, CfgCoverageBuilder coverage) {
		super(executionData);
		this.coverage = coverage;
	}

	/**
	 * Creates an ASM class visitor for analysis.
	 * 
	 * @param classid
	 *            id of the class calculated with {@link CRC64}
	 * @param className
	 *            VM name of the class
	 * @return ASM visitor to write class definition to
	 */
	@Override
	protected ClassVisitor createAnalyzingVisitor(final long classid,
			final String className) {
		final ExecutionData data = executionData.get(classid);
		final int[] probes;
		if (data == null) {
			probes = null;
			coverage.match(executionData.contains(className));
		} else {
			probes = (int[]) data.getRawProbes();
			coverage.match(false);
		}
		final FreqProbesClassAnalyzer analyzer = new FreqProbesClassAnalyzer(coverage, className, probes,
				stringPool);
		return new ClassProbesAdapter(analyzer, false);
	}
}
