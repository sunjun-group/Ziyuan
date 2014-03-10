package tzuyu.engine.experiment;

import java.util.List;

import tzuyu.engine.TzConfiguration;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Statement;
import tzuyu.engine.model.Variable;

/**
 * This class prints a sequence with all variables renamed
 * */
class SequenceDumper {
	/**
	 * The sequence to be printed
	 * */
	public final Sequence sequenceToPrint;
	/**
	 * The class for renaming all output variables
	 * */
	private JWriterFactory junitWriterFactory;

	public SequenceDumper(Sequence sequenceToPrint, VariableRenamer renamer,
			TzConfiguration config) {
		this.sequenceToPrint = sequenceToPrint;
		this.junitWriterFactory = new JWriterFactory(config.getJunitConfig(),
				renamer);
	}

	/**
	 * Print a sequence statement by statement, with variables renamed.
	 * */
	public String printSequenceAsCodeString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.sequenceToPrint.size(); i++) {
			Statement statement = this.sequenceToPrint.getStatement(i);
			Variable outputVar = this.sequenceToPrint.getVariable(i);
			List<Variable> inputVars = this.sequenceToPrint.getInputs(i);
			// store the code text of the current statement
			StringBuilder oneStatement = new StringBuilder();
			// print the current statement
			appendCode(oneStatement, statement, outputVar, inputVars);

			sb.append(oneStatement);

		}
		return sb.toString();
	}

	/**********************************************************
	 * The following code prints different types of statements.
	 **********************************************************/
	private void appendCode(StringBuilder sb, Statement stmt, Variable newVar,
			List<Variable> inputVars) {
		AbstractJWriter jwriter = junitWriterFactory.getJWriter(stmt, newVar, inputVars);
		if (jwriter != null) {
			jwriter.writeCode(sb);
		}
	}
}