/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */
package gentest.data.statement;

import java.util.Arrays;

/**
 * 
 * @author Nguyen Phuoc Nguong Phuc
 *
 */
public class RArrayAssignment extends Statement {
	private final int arrayVarID;
	private final int index[];
	private final int localVariableID;

	public RArrayAssignment(int arrayVarID, int[] index, int localVariableID) {
		super(RStatementKind.ARRAY_ASSIGNMENT);
		this.arrayVarID = arrayVarID;
		this.index = Arrays.copyOf(index, index.length);
		this.localVariableID = localVariableID;
	}

	@Override
	public boolean hasOutputVar() {
		return false;
	}

	@Override
	public void accept(StatementVisitor visitor) throws Throwable {
		visitor.visit(this);
	}

	public int getArrayVarID() {
		return arrayVarID;
	}

	public int[] getIndex() {
		return index;
	}

	public int getLocalVariableID() {
		return localVariableID;
	}

}
