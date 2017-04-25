package cfgcoverage.jacoco.analysis;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.internal.analysis.StringPool;
import org.jacoco.core.internal.flow.ClassProbesVisitor;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.jacoco.core.internal.instr.IInstrSupport;
import org.objectweb.asm.FieldVisitor;

/**
 * Analyzes the structure of a class.
 */
public class FreqProbesClassAnalyzer extends ClassProbesVisitor {
	protected final int[] probes;
	protected final StringPool stringPool;
	private final IInstrSupport instrSupport = ExecutionData.getInstrSupport();
	private final String className;
	private String superName;
	private CfgCoverageBuilder coverage;

	/**
	 * Creates a new analyzer that builds coverage data for a class.
	 * 
	 * @param probes
	 *            execution data for this class or <code>null</code>
	 * @param stringPool
	 *            shared pool to minimize the number of {@link String} instances
	 */
	public FreqProbesClassAnalyzer(CfgCoverageBuilder coverageBuilder, final String className, final int[] probes,
			final StringPool stringPool) {
		this.coverage = coverageBuilder;
		this.className = className;
		this.probes = probes;
		this.stringPool = stringPool;
	}
	
	@Override
	public void visit(final int version, final int access, final String name,
			final String signature, String superName,
			final String[] interfaces) {
		superName = stringPool.get(superName);
		coverage.startClass(name, signature);
	}

	@Override
	public MethodProbesVisitor visitMethod(final int access, final String name,
			final String desc, final String signature, final String[] exceptions) {
		instrSupport.assertNotInstrumented(name, className);
		return new FreqProbesMethodAnalyzer(coverage, className, superName,
				 probes);
	}

	@Override
	public FieldVisitor visitField(final int access, final String name,
			final String desc, final String signature, final Object value) {
		instrSupport.assertNotInstrumented(name, className);
		return super.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public void visitEnd() {
		super.visitEnd();
		coverage.endClass();
	}

	@Override
	public void visitTotalProbeCount(final int count) {
		// nothing to do
	}

}
