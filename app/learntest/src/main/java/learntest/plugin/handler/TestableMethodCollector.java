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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.plugin.handler.filter.methodfilter.IMethodFilter;

/**
 * @author LLT
 * [extracted from EvaluationHandler]
 */
public class TestableMethodCollector extends ASTVisitor {
	private Logger log = LoggerFactory.getLogger(TestableMethodCollector.class);
	private CompilationUnit cu;
	private Collection<IMethodFilter> methodFilters;
	private int totalMethodNum = 0;
	private int typeIdx;
	private List<MethodInfo> validMethods = new ArrayList<MethodInfo>();

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
			try {
				validMethods.add(TargetMethodConverter.toTargetMethod(cu, md));
				md.parameters();
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
		}
		return false;
	}

	public int getTotalMethodNum() {
		return totalMethodNum;
	}
	
	public List<MethodInfo> getValidMethods() {
		return validMethods;
	}
	
}