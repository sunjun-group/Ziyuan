package gentest.data.statement;

import java.util.Arrays;


public class RArrayAssignment extends Statement {

	private String arrayVarName;
	private int[] location;
	private Object value;
	
	public RArrayAssignment(String arrayVarName, int[] location, Object value) {
		super(RStatementKind.ARRAY_ASSIGNMENT);
		this.arrayVarName = arrayVarName;
		this.location = location;
		this.value = value;
	}
	
	@Override
	public boolean hasOutputVar() {
		return false;
	}

	@Override
	public void accept(StatementVisitor visitor) throws Throwable {
		// TODO Auto-generated method stub

	}

}
