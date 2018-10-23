/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.plugin.utils.filter;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * @author LLT
 * move the code from GenerateTestHandler.
 *
 */
public class NestedBlockChecker extends AbstractMethodFilter {

	@Override
	protected void reset() {
		isValid = false;
	}
	
	private void checkChildNestCondition(Statement stat, boolean isParentLoop) {
		ChildJudgeStatementChecker checker = new ChildJudgeStatementChecker(stat);
		stat.accept(checker);
		if ((!isParentLoop && checker.containJudge) || (isParentLoop && checker.containIfJudge)) {
			isValid = true;
		}
	}

	public boolean visit(SwitchStatement stat) {
		if (isValid) {
			return false;
		}
		checkChildNestCondition(stat, false);

		return false;
	}

	public boolean visit(IfStatement stat) {
		if (isValid) {
			return false;
		}
		checkChildNestCondition(stat, false);

		return false;
	}

	public boolean visit(DoStatement stat) {
		if (isValid) {
			return false;
		}
		checkChildNestCondition(stat, true);
		return false;
	}

	public boolean visit(EnhancedForStatement stat) {
		if (isValid) {
			return false;
		}
		checkChildNestCondition(stat, true);
		return false;
	}

	public boolean visit(ForStatement stat) {
		if (isValid) {
			return false;
		}
		checkChildNestCondition(stat, true);
		return false;
	}
	
	class ChildJudgeStatementChecker extends ASTVisitor {

		boolean containJudge = false;
		boolean containIfJudge = false;

		private Statement parentStatement;

		public ChildJudgeStatementChecker(Statement parentStat) {
			this.parentStatement = parentStat;
		}

		public boolean visit(DoStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(EnhancedForStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(ForStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(IfStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				containIfJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(SwitchStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				containIfJudge = true;
				return false;
			} else {
				return true;
			}
		}

		public boolean visit(WhileStatement stat) {
			if (stat != parentStatement) {
				containJudge = true;
				return false;
			} else {
				return true;
			}
		}
	}

}

