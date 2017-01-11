/**
 * Copyright TODO
 */
package gentest.core.data.variable;


import gentest.core.data.LocalVariable;
import gentest.core.data.statement.Statement;

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
