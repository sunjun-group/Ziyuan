/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.handler.filter.methodfilter;

import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.core.SourceType;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.PrimitiveUtils;

/**
 * @author LLT
 *
 */
public class TestableMethodFilter implements IMethodFilter {
	
	@Override
	public boolean isValid(CompilationUnit cu, MethodDeclaration md) {
		if(md.isConstructor() || md.parameters().isEmpty()
				|| !Modifier.isPublic(md.getModifiers()) || Modifier.isAbstract(md.getModifiers())
						|| !containsAtLeastOnePrimitiveTypeParam(md.parameters())
						|| !containsAtLeastOnePrimitiveTypeField(cu)){
			return false;
		}
		if (CollectionUtils.isEmpty(md.getBody().statements())) {
			return false;
		}
		return true;
	}
	
	/* backup from previous implementation */
	@SuppressWarnings("unused")
	private boolean containsAtLeastOnePrimitiveTypeParam(List<?> parameters){
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
	
	@SuppressWarnings("restriction")
	private boolean containsAtLeastOnePrimitiveTypeField(CompilationUnit cu){
		ITypeRoot type = cu.getTypeRoot();
		try {
			IJavaElement[] elements = type.getChildren();
			for (int i = 0; i < elements.length; i++) {
				IJavaElement element = elements[i];
				if (element instanceof SourceType) {
					SourceType source = (SourceType) element;
					IField[] fields = source.getFields();
					for (IField iField : fields) {
						String fieldT = iField.getTypeSignature();
						if (isPrimitiveType(fieldT)) {
							return true;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean isPrimitiveType(String signature) {
		if (signature.equals(Signature.SIG_INT)
				|| signature.equals(Signature.SIG_SHORT) 
				|| signature.equals(Signature.SIG_BYTE)
				|| signature.equals(Signature.SIG_BOOLEAN) 
				|| signature.equals(Signature.SIG_CHAR) 
				|| signature.equals(Signature.SIG_DOUBLE)
				|| signature.equals(Signature.SIG_FLOAT)
				|| signature.equals(Signature.SIG_LONG) ) {
			return true;
		}else if (signature.equals("[" + Signature.SIG_INT)
				|| signature.equals("[" + Signature.SIG_SHORT) 
				|| signature.equals("[" + Signature.SIG_BYTE)
				|| signature.equals("[" + Signature.SIG_BOOLEAN) 
				|| signature.equals("[" + Signature.SIG_CHAR) 
				|| signature.equals("[" + Signature.SIG_DOUBLE)
				|| signature.equals("[" + Signature.SIG_FLOAT)
				|| signature.equals("[" + Signature.SIG_LONG)) {
			return true;
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
