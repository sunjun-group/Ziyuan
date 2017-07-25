/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import java.util.List;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.PrimitiveUtils;

/**
 * @author LLT
 *
 */
public class TestableMethodFilter implements TargetMethodFilter {
	
	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		if(md.isConstructor() || md.parameters().isEmpty()
				|| !Modifier.isPublic(md.getModifiers()) || Modifier.isAbstract(md.getModifiers())){
			return false;
		}
		if (CollectionUtils.isEmpty(md.getBody().statements())) {
			return false;
		}
		return true;
	}
	
	/* backup from previous implementation */
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
}
