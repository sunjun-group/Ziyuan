/**
 * Copyright TODO
 */
package gentest.core.data.statement;


/**
 * @author LLT
 *
 */
public interface StatementVisitor {

	boolean visit(RAssignment stmt);

	boolean visitRmethod(Rmethod stmt);

	boolean visit(RConstructor stmt);

	boolean visit(REvaluationMethod stmt);

	boolean visit(RArrayConstructor stmt);

	boolean visit(RArrayAssignment stmt);
	
}
