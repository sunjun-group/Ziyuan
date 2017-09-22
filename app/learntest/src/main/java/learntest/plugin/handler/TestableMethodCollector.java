/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.handler.filter.methodfilter.IMethodFilter;

/**
 * @author LLT
 * [extracted from EvaluationHandler]
 */
public class TestableMethodCollector extends ASTVisitor {
	private CompilationUnit cu;
	private Collection<IMethodFilter> methodFilters;
	private int totalMethodNum = 0;
	private int typeIdx;
	private List<TargetMethod> validMethods = new ArrayList<TargetMethod>();

	public TestableMethodCollector(CompilationUnit cu, Collection<IMethodFilter> methodFilters) {
		this.cu = cu;
		this.methodFilters = methodFilters;
		typeIdx = 0;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		typeIdx++;
		if (node.isInterface() || node.isMemberTypeDeclaration() ||
				(node.isLocalTypeDeclaration() && !Modifier.isPublic(node.getModifiers()))) {
			return false;
		} 
		if (typeIdx > 1 && !Modifier.isPublic(node.getModifiers())) {
			return false;
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
			validMethods.add(TargetMethodConverter.toTargetMethod(cu, md));
			md.parameters();
		}
		return false;
	}

	public int getTotalMethodNum() {
		return totalMethodNum;
	}
	
	public List<TargetMethod> getValidMethods() {
		return validMethods;
	}
	
}