package mutation.mutator;

import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.WhileStmt;

import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by hoangtung on 4/5/15.
 */
public class CompilationUnitMutator {
	private static void loadMutationOperator() {

	}

	static {
		loadMutationOperator();
	}

	/**
	 * mutate expression statement
	 */
	public List<Statement> mutate(ExpressionStmt stmt, Map<String, Object> args) {
		return null;
	}

	/**
	 * mutate only the condition of while statement
	 */
	public List<Statement> mutate(WhileStmt stmt, Map<String, Object> args) {
		return null;
	}

	/**
	 * mutate only the condition of for statement
	 */
	public List<Statement> mutate(ForStmt stmt, Map<String, Object> args) {
		return null;
	}

	/**
	 * mutate only the condition of if statement
	 */
	public List<Statement> mutate(IfStmt stmt, Map<String, Object> args) {
		return null;
	}

	/**
	 * mutate break statement
	 */
	public List<Statement> mutate(BreakStmt stmt, Map<String, Object> args) {
		return null;
	}

	/**
	 * mutate continue statement
	 */
	public List<Statement> mutate(ContinueStmt stmt, Map<String, Object> args) {
		return null;
	}

	/**
	 * Mutate block statement. This is just a place holder, no implementation in
	 * this release. Mutating a block is done by mutating each statement in that
	 * block.
	 */
	@Deprecated
	public List<Statement> mutate(BlockStmt stmt, Map<String, Object> args) {
		throw new NotImplementedException();
	}

	/**
	 * mutate the expression in return statement
	 */
	public List<Statement> mutate(ReturnStmt stmt, Map<String, Object> args) {
		return null;
	}

}
