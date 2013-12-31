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

import org.eclipse.core.runtime.Assert;

/**
 * @author LLT
 * @author Peter Kalauskas (Randoop)
 */
class TreeNode {

	private TreeNode fParent;
	private List<TreeNode> fChildren;
	private Object fObject;

	private boolean fIsChecked;
	private boolean fIsGrayed;

	TreeNode(TreeNode parent, Object object, boolean checkedState,
			boolean grayedState) {
		Assert.isLegal(object != null);
		fParent = parent;
		fObject = object;

		fIsChecked = checkedState;
		fIsGrayed = grayedState;

		fChildren = new ArrayList<TreeNode>();

		updateRelatives();
	}

	public TreeNode addChild(Object object, boolean checkedState,
			boolean grayedState) {
		TreeNode node = new TreeNode(this, object, checkedState, grayedState);
		fChildren.add(node);

		node.updateRelatives();

		return node;
	}

	public void setObject(Object object) {
		fObject = object;
	}

	public Object getObject() {
		return fObject;
	}

	public TreeNode getParent() {
		return fParent;
	}

	public void setChecked(boolean state) {
		fIsChecked = state;
	}

	public void setGrayed(boolean state) {
		fIsGrayed = state;
	}

	public boolean isChecked() {
		return fIsChecked;
	}

	public boolean isGrayed() {
		return fIsGrayed;
	}

	public boolean hasChildren() {
		return !fChildren.isEmpty();
	}

	public TreeNode[] getChildren() {
		return (TreeNode[]) fChildren.toArray(new TreeNode[fChildren.size()]);
	}

	public void removeAllChildren() {
		fChildren = new ArrayList<TreeNode>();
	}

	@Override
	public int hashCode() {
		return fObject.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TreeNode) {
			TreeNode otherNode = (TreeNode) obj;
			boolean objectsEqual = getObject().equals(otherNode.getObject());
			boolean parentsEqual;
			if (getParent() == null) {
				parentsEqual = otherNode.getParent() == null;
			} else {
				parentsEqual = getParent().equals(otherNode.getParent());
			}
			return objectsEqual && parentsEqual;
		}
		return false;
	}

	public void delete() {
		if (fParent != null) {
			fParent.fChildren.remove(this);
			updateParent();
		}
	}

	public void updateRelatives() {
		updateParent();
		updateChildren();
	}

	private void updateParent() {
		if (fParent != null) {
			TreeNode[] siblings = fParent.getChildren();

			boolean allChecked = true;
			boolean noneChecked = true;
			for (TreeNode sibling : siblings) {
				allChecked &= sibling.isChecked() && !sibling.isGrayed();
				noneChecked &= !sibling.isChecked();
			}

			if (allChecked) {
				fParent.setChecked(true);
				fParent.setGrayed(false);
			} else if (noneChecked) {
				fParent.setChecked(false);
				fParent.setGrayed(false);
			} else {
				fParent.setChecked(true);
				fParent.setGrayed(true);
			}

			fParent.updateParent();
		}
	}

	private void updateChildren() {
		TreeNode[] children = getChildren();

		for (TreeNode child : children) {
			if (!isGrayed()) {
				child.setGrayed(false);
				child.setChecked(isChecked());
				child.updateChildren();
			}
		}
	}

}
