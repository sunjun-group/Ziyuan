/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.variable;

import icsetlv.common.exception.IcsetlvException;
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

import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.ObjectUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;


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
				String content = FileUtils.readFileToString(getSourceFile(clzName));
				int charCount = 0;
				int line = 0;
				List<BreakPoint> sortedBkps = sortByLineNum(brkpsMap.get(clzName));
				for (BreakPoint bkp : sortedBkps) {
					int bkpLine = getLineNoFromZero(bkp);
					int stmtStart = getStmtOffset(content, charCount, line, bkpLine);
					Node statement = parseStmt(getExtStmt(content, stmtStart));
					int stmtEnd = getStmtLength(statement, content, stmtStart);
					bkp.addVars(extractVarNames(statement));
					bkp.setCharStart(stmtStart);
					bkp.setCharEnd(stmtEnd);
					LogUtils.log("breakpoint: ", bkp);
					LogUtils.log("statement: ", statement);
					// update indices 
					line = bkpLine;
					charCount = bkp.getCharStart();
				}
			} catch (IOException e) {
				IcsetlvException.rethrow(e, "cannot read the source file of class "
						+ clzName);
			}
		}
	}

	private int getStmtOffset(String content, int startOffset, int startLine,
			int bkpLine) {
		int i = startOffset;
		while (startLine < bkpLine) {
			for (; i < content.length(); i++) {
				char ch = content.charAt(i);
				if (ch == '\n' || ch == '\r') {
					if (ch == '\r' && i < content.length() - 1
							&& content.charAt(i + 1) == '\n') {
						i++;
					}
					startLine++;
					i++;
					break;
				}
			}
		}
		return i;
	}
	
	private int getStmtLength(Node statement, String content,  int startOffset) {
//		return getStmtOffset(content, startOffset, statement.getBeginLine() - 1, statement.getEndLine());
		return getStmtOffset(content, startOffset, statement.getBeginLine() - 1, statement.getBeginLine());
	}

	private String getExtStmt(String content, int stmtOffset) {
		return content.substring(stmtOffset);
	}

	private int getLineNoFromZero(BreakPoint bkp) {
		return bkp.getLineNo() - 1;
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

	private Node parseStmt(String extStmt) throws IcsetlvException {
		Node result = null;
		try {
			result = JavaParser.parseStatement(extStmt);
		} catch (ParseException e) {
			try {
				result = JavaParser.parseBodyDeclaration(extStmt);
				return result;
			} catch (ParseException e1) {
				IcsetlvException.rethrow(e,
						"Cannot parse to the statment the block of code: " + extStmt);
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
