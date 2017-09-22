/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.gentest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.plugin.handler.filter.classfilter.ITypeFilter;
import learntest.plugin.handler.filter.methodfilter.IMethodFilter;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 */
public class MethodCollector extends ASTVisitor {
	private int totalMethodNum = 0;
	private List<IMethodFilter> methodFilters;
	private List<ITypeFilter> typeFilters;
	
	private CompilationUnit cu;
	private List<MethodDeclaration> result;

	public MethodCollector(List<ITypeFilter> typeFilters, List<IMethodFilter> methodFilters) {
		this.typeFilters = CollectionUtils.nullToEmpty(typeFilters);
		this.methodFilters = CollectionUtils.nullToEmpty(methodFilters);
		this.result = new ArrayList<MethodDeclaration>();
	}
	
	public void reset(boolean resetCounter) {
		this.result.clear();
		if (resetCounter) {
			totalMethodNum = 0;
		}
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		for (ITypeFilter filter : typeFilters) {
			if (!filter.isValid(node)) {
				return false;
			}
		}
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		for (ITypeFilter filter : typeFilters) {
			if (!filter.isValid(node)) {
				return false;
			}
		}
		return super.visit(node);
	}
	
	public boolean visit(MethodDeclaration md) {
		totalMethodNum++;
		boolean testable = true;
		for (IMethodFilter filter : methodFilters) {
			if (!filter.isValid(cu, md)) {
				testable = false;
				break;
			}
		}
		if (testable) {
			result.add(md);
		}
		return false;
	}
	
	public int getTotalMethodNum() {
		return totalMethodNum;
	}

	public List<MethodDeclaration> getResult() {
		return result;
	}
}