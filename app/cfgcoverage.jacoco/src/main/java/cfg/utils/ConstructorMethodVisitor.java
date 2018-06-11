package cfg.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import cfg.CFG;
import cfg.CfgNode;
import cfg.DecisionBranchType;
import cfg.SwitchCase;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;

/**
 * @author LLT
 *
 */
public class ConstructorMethodVisitor extends MethodVisitor {
	private CFG cfg;
	
	/* internal fields */
	private int currentLine;
	private MethodNode methodNode;
	private CfgNode lastNode;
	private List<Label> currentLabels = new ArrayList<Label>();
	private List<Jump> jumps = new ArrayList<ConstructorMethodVisitor.Jump>();
	private AbstractInsnNode currentInstruction;
	/* using map instead of using label.info directly to avoid wrongly modify existing label.info */
	private Map<Label, CfgNode> labelInfos = new HashMap<Label, CfgNode>();
	
	public ConstructorMethodVisitor() {
		super(Opcodes.ASM5);
	}
	
	public void visit(String className, MethodNode method) {
		String fullMethodName = ClassUtils.toClassMethodStr(className, method.name);
		String methodId = SignatureUtils.createMethodNameSign(fullMethodName, method.desc);
		cfg = new CFG(methodId);
		cfg.setMethodNode(method);
		this.methodNode = method;

		for (final TryCatchBlockNode n : methodNode.tryCatchBlocks) {
			n.accept(this);
		}
		currentInstruction = methodNode.instructions.getFirst();
		while (currentInstruction != null) {
			currentInstruction.accept(this);
			currentInstruction = currentInstruction.getNext();
		}
		this.visitEnd();
	
	}
	
	@Override
	public void visitLabel(final Label label) {
		currentLabels.add(label);
		if (lastNode != null && 
				CollectionUtils.existIn(lastNode.getInsnNode().getOpcode(), 
						Opcodes.RET,
						Opcodes.IRETURN,
						Opcodes.LRETURN,
						Opcodes.FRETURN,
						Opcodes.DRETURN,
						Opcodes.ARETURN,
						Opcodes.RETURN,
						Opcodes.ATHROW, 
						Opcodes.TABLESWITCH,
						Opcodes.LOOKUPSWITCH,
						Opcodes.GOTO)) {
			lastNode = null;
		}
	}

	@Override
	public void visitLineNumber(final int line, final Label start) {
		currentLine = line;
	}

	protected void visitInsn() {
		CfgNode node = new CfgNode(currentInstruction, currentLine);
		node.setDecisionNode(OpcodeUtils.isCondition(node.getInsnNode().getOpcode()));
		
		cfg.addNode(node);
		if (lastNode != null) {
			if (lastNode.isDecisionNode()) {
				lastNode.setDecisionBranch(node, DecisionBranchType.FALSE);
			} else {
				node.setPredecessor(lastNode);
			}
		}
		if (!currentLabels.isEmpty()) {
			for (Label label : currentLabels) {
				labelInfos.put(label, node);
			}
			currentLabels.clear();
		}
		lastNode = node;
		
	}
	
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		// TODO Auto-generated method stub
		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitInsn(final int opcode) {
		visitInsn();
	}

	@Override
	public void visitIntInsn(final int opcode, final int operand) {
		visitInsn();
	}

	@Override
	public void visitVarInsn(final int opcode, final int var) {
		visitInsn();
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		visitInsn();
	}

	@Override
	public void visitFieldInsn(final int opcode, final String owner,
			final String name, final String desc) {
		visitInsn();
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc, final boolean itf) {
		visitInsn();
	}

	@Override
	public void visitInvokeDynamicInsn(final String name, final String desc,
			final Handle bsm, final Object... bsmArgs) {
		visitInsn();
	}

	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		visitInsn();
		jumps.add(new Jump(lastNode, label, DecisionBranchType.TRUE));
	}

	@Override
	public void visitLdcInsn(final Object cst) {
		visitInsn();
	}

	@Override
	public void visitIincInsn(final int var, final int increment) {
		visitInsn();
	}

	@Override
	public void visitTableSwitchInsn(final int min, final int max,
			final Label dflt, final Label... labels) {
		int[] keys = new int[labels.length];
		int key = min;
		for (int idx = 0; idx < labels.length; idx++) {
			keys[idx] = key++;
		}
		visitSwitchInsn(dflt, labels, keys);
	}

	@Override
	public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
			final Label[] labels) {
		visitSwitchInsn(dflt, labels, keys);
	}

	private void visitSwitchInsn(final Label dflt, final Label[] labels, int[] keys) {
		visitInsn();
		Jump defaultCaseJump = new Jump(lastNode, dflt);
		defaultCaseJump.isSwitchSource = true;
		jumps.add(defaultCaseJump);
		for (int i = 0; i < labels.length; i++) {
			Label label = labels[i];
			int key = keys[i];
			Jump caseJump = new Jump(lastNode, label);
			caseJump.isSwitchSource = true;
			caseJump.switchCaseKey = key;
			jumps.add(caseJump);
			
		}
	}

	@Override
	public void visitMultiANewArrayInsn(final String desc, final int dims) {
		visitInsn();
	}

	@Override
	public void visitEnd() {
		for (Jump jump : jumps) {
			CfgNode target = (CfgNode) labelInfos.get(jump.target);
			CfgNode source = jump.source;
			if (source.isDecisionNode()) {
				source.setDecisionBranch(target, jump.branchType);
			} else {
				target.setPredecessor(source);
			}
			if (jump.isSwitchSource) {
				if (source.getSwitchCases() == null) {
					source.setSwitchCases(new ArrayList<SwitchCase>());
				}
				if (jump.switchCaseKey == null) {
					// default
					SwitchCase switchCase = new SwitchCase(source, target);
					source.getSwitchCases().add(switchCase);
				} else {
					SwitchCase switchCase = new SwitchCase(source, jump.switchCaseKey, target);
					source.getSwitchCases().add(switchCase);
				}
			}
		}
	}

	protected static class Jump {
		final CfgNode source;
		final Label target;
		/* branchRelationship in general, but here, to make it 
		 * strictly only have TRUE & FALSE value, & be coincident with decision node, we use DecisionBranchType.
		 * */
		DecisionBranchType branchType = DecisionBranchType.TRUE; 
		boolean isSwitchSource;
		Integer switchCaseKey;

		public Jump(final CfgNode source, final Label target, DecisionBranchType branchType) {
			this.source = source;
			this.target = target;
			this.branchType = branchType;
		}
		
		public Jump(final CfgNode source, final Label target) {
			this.source = source;
			this.target = target;
		}

		public Label getTarget() {
			return target;
		}

		public CfgNode getSource() {
			return source;
		}
		
		public DecisionBranchType getBranchType() {
			return branchType;
		}
	}
	
	public CFG getCfg() {
		return cfg;
	}
}
