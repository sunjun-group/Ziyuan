/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import tzuyu.plugin.action.TzuyuPlugin;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * @author LLT
 * 
 */
public class ProjectTypeSelectionExtension extends TypeSelectionExtension {
	private static final int VALIDATION_METHOD = 110;
	ITypeInfoFilterExtension classInputFilterExtension;
	ISelectionStatusValidator classInputSelectionStatusValidator;

	public ProjectTypeSelectionExtension() {
		classInputFilterExtension = new ClassInputFilterExtension();
		classInputSelectionStatusValidator = new ClassInputSelectionStatusValidator();
	}

	@Override
	public ITypeInfoFilterExtension getFilterExtension() {
		return classInputFilterExtension;
	}

	@Override
	public ISelectionStatusValidator getSelectionValidator() {
		return classInputSelectionStatusValidator;
	}

	private class ClassInputFilterExtension implements ITypeInfoFilterExtension {

		public boolean select(ITypeInfoRequestor typeInfoRequestor) {
			int flags = typeInfoRequestor.getModifiers();
			if (Flags.isInterface(flags) || Flags.isAbstract(flags)) {
				return false;
			}

			return true;
		}
	}

	private class ClassInputSelectionStatusValidator implements
			ISelectionStatusValidator {

		public IStatus validate(Object[] selection) {
			for (Object obj : selection) {
				try {
					if (obj instanceof IType) {
						IType type = (IType) obj;
						int flags = type.getFlags();
						if (type.isInterface()) {
							String msg = MessageFormat.format(
									"'{0}' is an interface",
									type.getElementName());
							return createUIStatus(IStatus.ERROR, msg);
						} else if (Flags.isAbstract(flags)) {
							String msg = MessageFormat.format(
									"'{0}' is abstract", type.getElementName());
							return createUIStatus(IStatus.ERROR, msg);
						} else if (!Flags.isPublic(flags)) {
							String msg = MessageFormat.format(
									"'{0}' is not public",
									type.getElementName());
							return createUIStatus(IStatus.ERROR, msg);
						}
					} else {
						return createUIStatus(IStatus.ERROR,
								"One of the selected elements is not a Java class or enum");
					}
				} catch (JavaModelException e) {
					PluginLogger.logEx(e);
				}
			}
			return createUIStatus(IStatus.OK, "");
		}
	}

	public static IStatus createUIStatus(int severity, String message) {
		return new Status(severity, TzuyuPlugin.PLUGIN_ID, VALIDATION_METHOD,
				message, null);
	}
}
