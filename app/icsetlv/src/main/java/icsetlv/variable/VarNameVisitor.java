/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import japa.parser.ast.Node;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ThisExpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.Constants;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;

/**
 * @author LLT
 *
 */
public class VarNameVisitor extends DefaultVoidVisitor {
	private VarNameCollectionMode mode;
	private Map<Integer, List<Variable>> result;
	protected List<Integer> lines;
	
	public VarNameVisitor(VarNameCollectionMode collectionMode, List<Integer> lines) {
		this.mode = collectionMode;
		this.lines = lines;
		result = new HashMap<Integer, List<Variable>>();
		for (Integer line : lines) {
			result.put(line, new ArrayList<BreakPoint.Variable>());
		}
	}
	
	@Override
	protected boolean beforehandleNode(Node node) {
		return lines.contains(node.getBeginLine());
	}
	
	@Override
	protected boolean beforeVisit(Node node) {
		for (Integer lineNo : lines) {
			if (lineNo >= node.getBeginLine() && lineNo <= node.getEndLine()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean handleNode(FieldAccessExpr n) {
		List<String> nameFragments = new ArrayList<String>();
		Expression scope = n;
		while (scope instanceof FieldAccessExpr) {
			FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) scope;
			nameFragments.add(fieldAccessExpr.getField());
			scope = fieldAccessExpr.getScope();
		}
		scope.accept(this, true);
		VarScope varScope = VarScope.UNDEFINED;
		if (scope instanceof ThisExpr) {
			varScope = VarScope.THIS;
		} else {
			varScope = VarScope.UNDEFINED;
			nameFragments.add(scope.toString());
		}

		String name = CollectionUtils.getLast(nameFragments);
		Collections.reverse(nameFragments);
		String fullName; 
		switch (mode) {
		case FULL_NAME:
			fullName = StringUtils.join(nameFragments, Constants.DOT); 
			break;
		case HIGHEST_LEVEL_VAR:
			fullName = name;
			break;
		default:
			fullName = StringUtils.join(nameFragments, Constants.DOT);
		}
		Variable var = new Variable(name, fullName, varScope);
		add(n.getBeginLine(), var);
		
		return false;
	}
	
	@Override
	public boolean handleNode(ArrayAccessExpr n) {
		Expression index = n.getIndex();
		if (index instanceof IntegerLiteralExpr) {
			add(n.getBeginLine(), new Variable(String.format("%s[%s]", n.getName(), ((IntegerLiteralExpr)index).getValue())));
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean handleNode(NameExpr n) {
		add(n.getBeginLine(), new Variable(n.getName()));
		return false;
	}
	
	@Override
	public boolean handleNode(ThisExpr n) {
		add(n.getBeginLine(), new Variable("this"));
		return false;
	}
	
	@Override
	public boolean handleNode(VariableDeclaratorId n) {
		add(n.getBeginLine(), new Variable(n.getName()));
		return false;
	}
	
	protected void add(int lineNumber, Variable var){
		CollectionUtils.addIfNotNullNotExist(result.get(lineNumber), var);
	}

	public Map<Integer, List<Variable>> getResult() {
		return result;
	}
	
	public static enum VarNameCollectionMode {
		FULL_NAME, /* eg: with variable a.b.c, we add a variable with id a.b.c*/
		HIGHEST_LEVEL_VAR /* eg: with variable a.b.c, we only add a variable with id a */
	}
}

