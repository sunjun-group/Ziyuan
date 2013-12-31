/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

import tzuyu.plugin.core.dto.TypeMnemonic;

/**
 * @author LLT
 * @author Peter Kalauskas [Randoop, TreeInput]
 */
class TreeInput {
	private List<TreeNode> fRoots;

	public TreeInput() {
		fRoots = new ArrayList<TreeNode>();
	}
	
	public void reset() {
		fRoots.clear();
	}

	public TreeNode addRoot(Object object) {
		for (TreeNode root : fRoots) {
			if (root.getObject().equals(object)) {
				return root;
			}
		}

		TreeNode root = new TreeNode(null, object, false, false);
		fRoots.add(root);
		return root;
	}

	public void remove(TreeNode node, Set<String> fDeletedTypeNodes) {
		Object obj = node.getObject();

		if (obj instanceof TypeMnemonic) {
			fDeletedTypeNodes.add(obj.toString());

			node.delete();
			if (!node.getParent().hasChildren()) {
				remove(node.getParent(), fDeletedTypeNodes);
			}
		} else if (obj instanceof String) {
			for (TreeNode child : node.getChildren()) {
				remove(child, fDeletedTypeNodes);
			}

			fRoots.remove(node);
		}
	}

	public TreeNode[] getRoots() {
		return (TreeNode[]) fRoots.toArray(new TreeNode[fRoots.size()]);
	}

	public void removeAll() {
		fRoots = new ArrayList<TreeNode>();
	}

	public boolean hasMissingClasses(IJavaProject currentProject) {
		for (TreeNode packageNode : getRoots()) {
			for (TreeNode classNode : packageNode.getChildren()) {
				if (!((TypeMnemonic) classNode.getObject()).getJavaProject()
						.equals(currentProject)) {
					return true;
				}
			}
		}

		return false;
	}
}
