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
import java.util.LinkedList;
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
import learntest.plugin.handler.filter.methodfilter.TestableMethodFilter;

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
	private List<String> allPTValidMethods = new LinkedList<>(); // a list of valid methods whose all parameters and fields are primitive type
	private List<String> somePTValidMethods = new LinkedList<>();// a list of valid methods who has any parameters or fields that is not primitive type

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
				MethodInfo candidate = TargetMethodConverter.toTargetMethod(cu, md);
				validMethods.add(candidate);
				if (TestableMethodFilter.containsAllPrimitiveTypeParam(md.parameters())
						&& TestableMethodFilter.containsAllPrimitiveTypeField(cu)) {
					allPTValidMethods.add(candidate.getMethodFullName() + "." + candidate.getLineNum());
				}else {
					somePTValidMethods.add(candidate.getMethodFullName() + "." + candidate.getLineNum());
				}
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

	public List<String> getAllPTValidMethods() {
		return allPTValidMethods;
	}

	public List<String> getSomePTValidMethods() {
		return somePTValidMethods;
	}
	
}