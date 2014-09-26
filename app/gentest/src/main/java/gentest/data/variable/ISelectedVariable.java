/**
 * Copyright TODO
 */
package gentest.data.variable;


import gentest.data.LocalVariable;
import gentest.data.statement.Statement;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface ISelectedVariable {
	List<Statement> getStmts();

	List<LocalVariable> getNewVariables();

	int getReturnVarId();

}
