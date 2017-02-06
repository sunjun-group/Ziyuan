package tzuyu.engine.junit;

import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.junit.printer.JStrOutputPrinter;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.Variable;

/**
 * This class prints a sequence with all variables renamed
 * */
public class SequenceJWriter {
	/**
	 * The sequence to be printed
	 * */
	public final Sequence sequenceToPrint;
	/**
	 * The class for renaming all output variables
	 * */
	private StmtJWriterFactory junitWriterFactory;

	public SequenceJWriter(Sequence sequenceToPrint, VariableRenamer renamer,
			TzConfiguration config) {
		this.sequenceToPrint = sequenceToPrint;
		this.junitWriterFactory = new StmtJWriterFactory(config, renamer);
	}

	/**
	 * Print a sequence statement by statement, with variables renamed.
	 * */
	public void printSequenceAsCodeString(JStrOutputPrinter content) {
		for (int i = 0; i < this.sequenceToPrint.size(); i++) {
			Statement statement = this.sequenceToPrint.getStatement(i);
			Variable outputVar = this.sequenceToPrint.getVariable(i);
			List<Variable> inputVars = this.sequenceToPrint.getInputs(i);
			// print the current statement
			appendCode(content, statement, outputVar, inputVars);
		}
	}

	/**********************************************************
	 * The following code prints different types of statements.
	 **********************************************************/
	private void appendCode(JStrOutputPrinter content, Statement stmt, Variable newVar,
			List<Variable> inputVars) {
		AbstractStmtJWriter jwriter = junitWriterFactory.getStmtJWriter(stmt, newVar, inputVars);
		if (jwriter != null) {
			jwriter.writeCode(content);
		}
	}
}