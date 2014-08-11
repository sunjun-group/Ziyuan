/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.VariableUtils;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Predicate;


/**
 * @author LLT
 *
 */
public class AssertionDetector {
	
	public static List<BreakPoint> scan(Map<String, List<String>> map) throws IcsetlvException {
		List<BreakPoint> result = new ArrayList<BreakPoint>();
		for (String jfilePath : map.keySet()) {
			result.addAll(scan(jfilePath, map.get(jfilePath)));
		}
		return result;
	}
	
	public static List<BreakPoint> scan(String jfilepath, List<String> methods) throws IcsetlvException {
		try {
			CompilationUnit cu = JavaParser.parse(new File(jfilepath));
			AssertStmtToBreakpointVisitor visitor = new AssertStmtToBreakpointVisitor(methods);
			cu.accept(visitor, "");
			CollectionUtils.filter(visitor.result, new Predicate<BreakPoint>() {
				
				public boolean apply(BreakPoint val) {
					return val.valid()
							&& !CollectionUtils.isEmpty(val.getVars());
				}
			});
			return visitor.result;
		} catch (Exception e) {
			throw new IcsetlvException(e);
		}
	}
	
	static class AssertStmtToBreakpointVisitor extends VoidVisitorAdapter<String> {
		private static final String[] ASSERT_METHOD_NAMES = new String[]{"Assert"};
		private StringBuilder curClass;
		private MethodDeclaration curMethod;
		private BreakPoint curBreakpoint;
		private List<BreakPoint> result = new ArrayList<BreakPoint>();
		private List<String[]> selectedMths;
		
		public AssertStmtToBreakpointVisitor(List<String> methods) {
			if (!CollectionUtils.isEmpty(methods)) {
				selectedMths = new ArrayList<String[]>();
				for (String method : methods) {
					int idx = method.indexOf("(");
					selectedMths.add(new String[] {method.substring(0, idx - 1),
							method.substring(idx, method.length() - 1)});
				}
			}
		}

		@Override
		public void visit(PackageDeclaration n, String arg) {
			curClass = new StringBuilder(n.getName().toString());
			super.visit(n, arg);
		}
		
		@Override
		public void visit(ClassOrInterfaceDeclaration n, String arg) {
			curClass.append(".").append(n.getName());
			for (BodyDeclaration member : n.getMembers()) {
				if (member instanceof MethodDeclaration) {
					member.accept(this, arg);
				}
			}
		}
		
		@Override
		public void visit(MethodDeclaration n, String arg) {
			if (isSelectedMth(n.getName())) {
				curMethod = n;
				super.visit(n, arg);
			}
		}
		
		private boolean isSelectedMth(String name) {
			if (selectedMths == null) {
				return true;
			}
			for (String[] mth : selectedMths) {
				if (name.equals(mth[0])) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void visit(MethodCallExpr n, String arg) {
			if (n.getScope() instanceof NameExpr) {
				if (CollectionUtils.existIn(
						((NameExpr) n.getScope()).getName(),
						ASSERT_METHOD_NAMES)) {
					initBreakpoint(n);
					if (n.getArgs() != null) {
						for (final Expression e : n.getArgs()) {
							e.accept(this, arg);
						}
					}
					comitBreakpoint();
				}
			}
		}

		@Override
		public void visit(AssertStmt n, String arg) {
			initBreakpoint(n);
			super.visit(n, arg);
			comitBreakpoint();
		}

		private void comitBreakpoint() {
			result.add(curBreakpoint);
			curBreakpoint = null;
		}

		private void initBreakpoint(Node n) {
			curBreakpoint = BreakPoint.from(curClass.toString(), curMethod);
			curBreakpoint.setLineNo(n.getBeginLine());
		}
		
		@Override
		public void visit(FieldAccessExpr n, String arg) {
			if (curBreakpoint != null) {
				curBreakpoint.addVars(VariableUtils.toBreakpointVarName(n));
			}
		}
		
		@Override
		public void visit(NameExpr n, String arg) {
			if (curBreakpoint != null) {
				curBreakpoint.addVars(VariableUtils.toBreakpointVarName(n));
			}
		}
		
	}
}
