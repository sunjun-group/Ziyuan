/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cfg.CFG;
import cfg.CfgNode;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgConstructor {
	
	public CFG constructCFG(AppJavaClassPath appClasspath, String className, String methodNameOrWithSign, int maxLevel)
			throws SavRtException {
		CFG cfg = buildCFG(appClasspath, className, methodNameOrWithSign, 1, maxLevel);
		ControlDependencyConstructor.computeControlDependency(cfg);
		return cfg;
	}
	
	private CFG buildCFG(AppJavaClassPath appClasspath, String className, String methodNameOrWithSign, int level,
			int maxLevel) throws SavRtException {
		try {
			InputStream is = getClassInputStream(appClasspath, className);
			CFG cfg = initCFG(is, className, methodNameOrWithSign);
			if (level != maxLevel) {
				List<CfgNode> nodeList = new ArrayList<CfgNode>(cfg.getNodeList());
				for (CfgNode node : nodeList) {
					if (node.getInsnNode() instanceof MethodInsnNode) {
						MethodInsnNode methodInsn = (MethodInsnNode) node.getInsnNode();
						CFG subCfg = buildCFG(appClasspath, methodInsn.owner.replace("/", "."),
								SignatureUtils.createMethodNameSign(methodInsn.name, methodInsn.desc), level + 1,
								maxLevel);
						glueCfg(cfg, node, subCfg);
					}
				}
			}
			return cfg;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	private void glueCfg(CFG cfg, CfgNode node, CFG subCfg) {
		if (subCfg == null) {
			return;
		}
		boolean hasExceptionBranch = false;
		for (CfgNode exitNode : subCfg.getExitList()) {
			if (exitNode.getInsnNode().getOpcode() == Opcodes.ATHROW) {
				cfg.getExitList().add(exitNode);
				hasExceptionBranch = true;
			} 
		}
		if (!hasExceptionBranch) {
			return; // no need to attach
		}
		node.setSubCfg(subCfg);
		/* 
		 * turn invokeNode --> nextNode
		 * to: invokeNode --> startNode of subCFG -- --> returnNodes of subCfg --> nextNode
		 * */
		CfgNode nextNode = node.getNext();
		node.getBranches().remove(nextNode);
		node.addBranch(subCfg.getStartNode());
		for (CfgNode exitNode : subCfg.getExitList()) {
			if (OpcodeUtils.isReturnInsn(exitNode.getInsnNode().getOpcode())) {
				exitNode.addBranch(nextNode);
			} 
		}
		for (CfgNode subNode : subCfg.getNodeList()) {
			cfg.addNode(subNode);
			for (CfgNode loopHeader : CollectionUtils.nullToEmpty(node.getLoopHeaders())) {
				subNode.addLoopHeader(loopHeader);
			}
		}
		cfg.addInclusiveMethod(subCfg.getInclusiveMethods());
	}

	private InputStream getClassInputStream(AppJavaClassPath appClasspath, String className)
			throws FileNotFoundException {
		String classFilePath = ClassUtils.getClassFilePath(appClasspath.getTarget(), className);
		File classFile = new File(classFilePath);
		if (!classFile.exists()) {
			throw new FileNotFoundException("File not found! " + classFilePath);
		}
		InputStream is = new FileInputStream(classFile);
		return is;
	}
	
	private CFG initCFG(InputStream is, String className, String methodNameOrWithSign) throws SavRtException {
		try {
			ClassReader classReader;
			classReader = new ClassReader(is);

			ConstructorClassVisitor classVisitor = new ConstructorClassVisitor(className, methodNameOrWithSign);
			classReader.accept(classVisitor, 0);
			ConstructorMethodVisitor methodVisitor = new ConstructorMethodVisitor();
			methodVisitor.visit(className, classVisitor.methodNode);
			CFG cfg = methodVisitor.getCfg();
			return cfg;
		} catch (IOException e) {
			throw new SavRtException(e);
		}
	}
	
	public CFG constructCFG(String className, MethodNode methodNode) {
		ConstructorMethodVisitor methodVisitor = new ConstructorMethodVisitor();
		methodVisitor.visit(className, methodNode);
		CFG cfg = methodVisitor.getCfg();
		ControlDependencyConstructor.computeControlDependency(cfg);
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
			if (ClassUtils.getSimpleName(className).equals(methodName)) {
				this.methodName = "<init>";
			}
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (methodName.equals(name) && (methodSignature.isEmpty() || methodSignature.equals(desc))) {
				methodNode = new MethodNode(access, name, desc, signature, exceptions);
				return null;
			}
			return null;
		}
	}
}
