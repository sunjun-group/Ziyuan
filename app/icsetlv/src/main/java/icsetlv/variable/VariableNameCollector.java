/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.BreakpointUtils;
import icsetlv.common.utils.ClassUtils;
import icsetlv.common.utils.LogUtils;
import icsetlv.common.utils.VariableUtils;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sav.common.core.utils.ObjectUtils;

/**
 * @author LLT
 *
 */
public class VariableNameCollector {
	private List<String> srcFolders;
	
	public VariableNameCollector(List<String> srcFolders) {
		this.srcFolders = srcFolders;
	}

	public void updateVariables(List<BreakPoint> brkps) throws IcsetlvException {
		Map<String, List<BreakPoint>> brkpsMap = BreakpointUtils.initBrkpsMap(brkps);
		for (String clzName : brkpsMap.keySet()) {
			try {
				List<?> lines = FileUtils.readLines(getSourceFile(clzName), "utf-8");
				int i = 0;
				int charCount = -1;
				List<BreakPoint> sortedBkps = sortByLineNum(brkpsMap.get(clzName));
				for (BreakPoint bkp : sortedBkps) {
					int bkpLine = getLineNoFromZero(bkp);
					for (; i < lines.size(); i++) {
						charCount += 1;
						String line = (String) lines.get(i);
						if (i == bkpLine) {
							bkp.setCharStart(charCount);
							StringBuilder stmtStr = new StringBuilder(line);
							// find the end of the statement (in case statement is on multiple lines)
							for (int j = i + 1; j < lines.size(); j++) {
								stmtStr.append(lines.get(j));
							}
							Node statement = parseStmt(stmtStr);
							charCount += countChar(lines, statement, bkpLine);
							bkp.addVars(extractVarNames(statement));
							bkp.setCharEnd(charCount);
							LogUtils.log("breakpoint: ", bkp);
							LogUtils.log("statement: ", statement);
							i++;
							break;
						} else {
							charCount += line.length();
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IcsetlvException e) {
				// do nothing
			}
		}
	}

	private int getLineNoFromZero(BreakPoint bkp) {
		return bkp.getLineNo() - 1;
	}

	private int countChar(List<?> lines, Node statement, int startLine) {
		int count = 0;
		int endLine = statement.getEndLine() + startLine - 1;
		for (int i = statement.getBeginLine() + startLine - 1; i <= endLine; i++) {
			count += ((String)lines.get(i)).length();
		}
		return count;
	}

	private File getSourceFile(String clzName) {
		for (String srcFolder : srcFolders) {
			File file = new File(ClassUtils.getJFilePath(srcFolder, clzName));
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	private Node parseStmt(StringBuilder stmt) throws IcsetlvException {
		Node result = null;
		try {
			result = JavaParser.parseStatement(stmt.toString());
		} catch (ParseException e) {
			try {
				result = JavaParser.parseBodyDeclaration(stmt.toString());
				return result;
			} catch (ParseException e1) {
				IcsetlvException.rethrow(e,
						"Cannot parse to the statment the block of code: " + stmt);
			}
		}
		return result;
	}

	private List<BreakPoint> sortByLineNum(List<BreakPoint> bkps) {
		Collections.sort(bkps, new Comparator<BreakPoint>() {

			public int compare(BreakPoint o1, BreakPoint o2) {
				return ObjectUtils.compare(getLineNoFromZero(o1), getLineNoFromZero(o2));
			}
		});
		return bkps;
	}
	
	/**
	 * scan statement and find the varName
	 */
	private Variable[] extractVarNames(Node statement) {
		VarNameVisitor visitor = new VarNameVisitor();
		statement.accept(visitor, null);
		return visitor.result.toArray(new Variable[visitor.result.size()]);
	}
	
	private static class VarNameVisitor extends VoidVisitorAdapter<String> {
		private List<Variable> result;
		
		public VarNameVisitor() {
			result = new ArrayList<BreakPoint.Variable>();
		}
		
		@Override
		public void visit(ExpressionStmt n, String arg) {
			n.getExpression().accept(this, arg);
		}
		
		public void visit(MethodCallExpr n, String arg) {
			if (n.getScope() != null) {
				n.getScope().accept(this, arg);
			}
			if (n.getArgs() != null) {
				for (final Expression e : n.getArgs()) {
					e.accept(this, arg);
				}
			}
		}
		
		public void visit(AssignExpr n, String arg) {
			n.getTarget().accept(this, arg);
			n.getValue().accept(this, arg);
		}
		
		@Override
		public void visit(FieldAccessExpr n, String arg) {
			result.add(VariableUtils.toBreakpointVarName(n));
		}
		
		@Override
		public void visit(NameExpr n, String arg) {
			result.add(VariableUtils.toBreakpointVarName(n));
		}
	}
}
