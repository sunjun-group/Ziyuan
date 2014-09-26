/**
 * Copyright TODO
 */
package gentest.data.statement;

/**
 * @author LLT
 *
 */
public interface StatementVisitor {

	void visit(RAssignment stmt) throws Throwable;

	void visit(Rmethod stmt) throws Throwable;

	void visit(RConstructor stmt) throws Throwable;
	
}
