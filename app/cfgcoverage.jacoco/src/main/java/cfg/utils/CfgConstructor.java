/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg.utils;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import cfg.CFG;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class CfgConstructor {
	
	public static CFG constructCDG(InputStream is, String className, String methodNameOrWithSign) throws IOException {
		CFG cfg = constructCFG(is, className, methodNameOrWithSign);
		return cfg;
	}

	public static CFG constructCFG(InputStream is, String className, String methodNameOrWithSign) throws IOException {
		ClassReader classReader = new ClassReader(is);
		ConstructorClassVisitor classVisitor = new ConstructorClassVisitor(className, methodNameOrWithSign);
		classReader.accept(classVisitor, 0);
		ConstructorMethodVisitor methodVisitor = new ConstructorMethodVisitor();
		methodVisitor.visit(className, classVisitor.methodNode);
		CFG cfg = methodVisitor.getCfg();
		CfgConstructorUtils.completeCfg(cfg);
		return cfg;
	}
	
	public static CFG constructCFG(String className, MethodNode methodNode) {
		ConstructorMethodVisitor methodVisitor = new ConstructorMethodVisitor();
		methodVisitor.visit(className, methodNode);
		CFG cfg = methodVisitor.getCfg();
		CfgConstructorUtils.completeCfg(cfg);
		return cfg;
	}
	
	private static class ConstructorClassVisitor extends ClassVisitor {
		String methodName;
		String methodSignature;
		MethodNode methodNode;

		public ConstructorClassVisitor(String className, String methodNameOrWithSign) {
			super(Opcodes.ASM5);
			this.methodName = SignatureUtils.extractMethodName(methodNameOrWithSign);
			this.methodSignature = SignatureUtils.extractSignature(methodNameOrWithSign);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (methodName.equals(name) && (methodSignature.isEmpty() || methodSignature.equals(signature))) {
				methodNode = new MethodNode(access, name, desc, signature, exceptions);
				return methodNode;
			}
			return null;
		}
	}
}
