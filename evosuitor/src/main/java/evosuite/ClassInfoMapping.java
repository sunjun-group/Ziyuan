/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * @author LLT
 *
 */
public class ClassInfoMapping extends VoidVisitorAdapter<Boolean> {
	private List<Integer> lines;
	private Map<Integer, Integer> methodStartLineMap;
	private Map<Integer, Integer> methodEndLinesMap;
	private List<Integer> allMethodStartLine;
	private int packageLine;
	private int classLine;
	
	public ClassInfoMapping(File sourceFile, List<Integer> lines) throws Exception {
		CompilationUnit cu = JavaParser.parse(sourceFile);
		this.lines = new ArrayList<>(lines);
		methodStartLineMap = new HashMap<>();
		methodEndLinesMap = new HashMap<>();
		allMethodStartLine = new ArrayList<>();
		cu.accept(this, true);
	}
	
	@Override
	public void visit(PackageDeclaration n, Boolean arg) {
		packageLine = getBeginLineIdx(n);
		super.visit(n, arg);
	}
	
	private int getBeginLineIdx(Node node) {
		return node.getBeginLine() - 1;
	}
	
	private int getEndLineIdx(Node node) {
		return node.getEndLine() - 1;
	}
	
	@Override
	public void visit(CompilationUnit n, Boolean arg) {
		classLine = getBeginLineIdx(n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(MethodDeclaration n, Boolean arg) {
		mapMethod(n);
	}

	private void mapMethod(Node n) {
		allMethodStartLine.add(getBeginLineIdx(n));
		for (int line : lines) {
			if (n.getBeginLine() <= line && n.getEndLine() >= line) {
				methodStartLineMap.put(line, getBeginLineIdx(n));
				methodEndLinesMap.put(line, getEndLineIdx(n));
				break;
			}
		}
	}

	public Map<Integer, Integer> getMethodStartLineMap() {
		return methodStartLineMap;
	}

	public List<Integer> getAllMethodStartLine() {
		return allMethodStartLine;
	}

	public int getPackageLine() {
		return packageLine;
	}

	public int getClassLine() {
		return classLine;
	}
	
	public Map<Integer, Integer> getMethodEndLinesMap() {
		return methodEndLinesMap;
	}
}
