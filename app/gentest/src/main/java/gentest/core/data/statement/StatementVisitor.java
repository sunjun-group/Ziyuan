/**
 * Copyright TODO
 */
package gentest.core.data.statement;

/**
 * @author LLT
 *
 */
public interface StatementVisitor {

	void visit(RAssignment stmt) throws Throwable;

	void visitRmethod(Rmethod stmt) throws Throwable;

	void visit(RConstructor stmt) throws Throwable;

	void visit(REvaluationMethod stmt) throws Throwable;

	void visit(RArrayConstructor stmt);

	void visit(RArrayAssignment stmt);
	
}
