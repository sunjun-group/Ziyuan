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
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.commons.data.classinfo.TargetClass;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.handler.filter.methodfilter.TargetMethodFilter;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * [extracted from EvaluationHandler]
 */
public class TestableMethodCollector extends ASTVisitor {
	private static final Logger log = LoggerFactory.getLogger(TestableMethodCollector.class);
	private CompilationUnit cu;
	private Collection<TargetMethodFilter> methodFilters;
	private int totalMethodNum = 0;
	private TypeDeclaration rootType;
	private int typeIdx;
	private List<TargetMethod> validMethods = new ArrayList<TargetMethod>();
	private String pkgName;

	public TestableMethodCollector(CompilationUnit cu, Collection<TargetMethodFilter> methodFilters) {
		this.cu = cu;
		this.methodFilters = methodFilters;
		typeIdx = 0;
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		pkgName = node.getName().getFullyQualifiedName();
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		typeIdx++;
		if (node.isPackageMemberTypeDeclaration()) {
			rootType = node;
		}
		if (node.isInterface() || node.isMemberTypeDeclaration() ||
				(node.isLocalTypeDeclaration() && !Modifier.isPublic(node.getModifiers()))) {
			return false;
		} 
		/* TODO-LLT: temporary only test the first type of compilation unit.
		 * still need to test before enable the second condition.
		 */
		if (typeIdx > 1 /* && !Modifier.isPublic(node.getModifiers()) */) {
			return false;
		}
		return super.visit(node);
	}
	
	public boolean visit(MethodDeclaration md) {
		totalMethodNum++;
		boolean testable = true;
		for (TargetMethodFilter filter : methodFilters) {
			if (!filter.isValid(cu, md)) {
				testable = false;
				break;
			}
		}
		if (testable) {
			validMethods.add(initTargetMethod(md));
			md.parameters();
		}
		return false;
	}
	
	protected TargetMethod initTargetMethod(MethodDeclaration method) {
		String simpleMethodName = method.getName().getIdentifier();
		int lineNumber = IMethodUtils.getStartLineNo(cu, method);
		if (!(method.getParent() instanceof TypeDeclaration)) {
			log.debug("expect: TypeDeclaration type, get: {}", method.getParent().getClass());
			throw new SavRtException(String.format("Unexpected type: %s", method.getParent().getClass()));
		}
		String className = null;
		TypeDeclaration type = (TypeDeclaration) method.getParent();
		if (type != rootType && type.isMemberTypeDeclaration()) {
			className = ClassUtils.getCanonicalName(pkgName, rootType.getName().getIdentifier(), type.getName().getIdentifier());
		} else {
			className = ClassUtils.getCanonicalName(pkgName, type.getName().getFullyQualifiedName());
		}
		TargetClass targetClass = new TargetClass(className);
		TargetMethod targetMethod = new TargetMethod(targetClass);
		targetMethod.setMethodName(simpleMethodName);
		targetMethod.setLineNum(lineNumber);
		targetMethod.setMethodLength(IMethodUtils.getLength(cu, method));
		targetMethod.setMethodSignature(LearnTestUtil.getMethodSignature(method));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(method.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for(Object obj: method.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		targetMethod.setParams(paramNames);
		targetMethod.setParamTypes(paramTypes);
		
		return targetMethod;
	}
	
	public int getTotalMethodNum() {
		return totalMethodNum;
	}
	
	public List<TargetMethod> getValidMethods() {
		return validMethods;
	}
	
	class FieldAccessChecker extends ASTVisitor {
		boolean isFieldAccess = false;

		public boolean visit(SimpleName name) {
			IBinding binding = name.resolveBinding();
			if (binding instanceof IVariableBinding) {
				IVariableBinding vb = (IVariableBinding) binding;
				if (vb.isField()) {
					if (vb.getType().isPrimitive()) {
						isFieldAccess = true;
					}
					if (vb.getType().isArray()) {
						if (vb.getType().getElementType().isPrimitive()) {
							isFieldAccess = true;
						}
					}
				}
			}
			return false;
		}
	}
	
	class DecisionStructureChecker extends ASTVisitor {
		private boolean isStructured = false;

		public boolean visit(IfStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean visit(DoStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean visit(EnhancedForStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean visit(ForStatement stat) {
			this.setStructured(true);
			return false;
		}

		public boolean isStructured() {
			return isStructured;
		}

		public void setStructured(boolean isStructured) {
			this.isStructured = isStructured;
		}
	}
}