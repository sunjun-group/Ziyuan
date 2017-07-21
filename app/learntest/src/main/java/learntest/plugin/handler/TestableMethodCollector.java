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
import org.eclipse.jdt.core.dom.ArrayType;
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
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import learntest.core.commons.data.classinfo.TargetClass;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.plugin.handler.filter.methodfilter.TargetMethodFilter;
import learntest.plugin.utils.IMethodUtils;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.PrimitiveUtils;

/**
 * @author LLT
 * [extracted from EvaluationHandler]
 */
public class TestableMethodCollector extends ASTVisitor {
	private List<MethodDeclaration> mdList = new ArrayList<MethodDeclaration>();
	private CompilationUnit cu;
	private Collection<TargetMethodFilter> methodFilters;
	private int totalMethodNum = 0;
	private TypeDeclaration curType;
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
		curType = node;
		if (node.isInterface() || node.isMemberTypeDeclaration() ||
				(node.isLocalTypeDeclaration() && !Modifier.isPublic(node.getModifiers()))) {
			return false;
		} 
		// TODO-LLT: temporary only test the first type of compilation unit.
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
			mdList.add(md);
			validMethods.add(initTargetMethod(md));
			System.out.println(initTargetMethod(md));
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean containsAtLeastOnePrimitiveType(List<?> parameters){
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				if(type.isPrimitiveType()){
					return true;
				}
				if(type.isArrayType()){
					ArrayType aType = (ArrayType)type;
					if(aType.getElementType().isPrimitiveType()){
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean containsAllPrimitiveType(List<?> parameters){
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				String typeString = type.toString();
				if(!PrimitiveUtils.isPrimitive(typeString) || svd.getExtraDimensions() > 0){
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private boolean containsArrayOrString(List parameters) {
		for (Object obj : parameters) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
				Type type = svd.getType();
				if (type.isArrayType() || type.toString().contains("String")) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected TargetMethod initTargetMethod(MethodDeclaration method) {
		String simpleMethodName = method.getName().getIdentifier();
		int lineNumber = IMethodUtils.getStartLineNo(cu, method);
		String className = ClassUtils.getCanonicalName(pkgName, curType.getName().getFullyQualifiedName());
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
	
	public List<MethodDeclaration> getMdList() {
		return mdList;
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