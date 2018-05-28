/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.eclipse.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author LLT
 *
 */
public class WorkbenchUtils {
	
	 public static IWorkbenchWindow getActiveWorkbenchWindow(AbstractUIPlugin plugin) {
	        if (Display.getCurrent() != null) {
	            return plugin.getWorkbench().getActiveWorkbenchWindow();
	        }
	        // need to call from UI thread
	        final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
	        Display.getDefault().syncExec(new Runnable() {
	            public void run() {
	                window[0] = plugin.getWorkbench().getActiveWorkbenchWindow();
	            }
	        });
	        return window[0];
	    }
	    
	    public static String[] getProjectsInWorkspace(){
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject[] projects = root.getProjects();
			
			String[] projectStrings = new String[projects.length];
			for(int i=0; i<projects.length; i++){
				projectStrings[i] = projects[i].getName();
			}
			
			return projectStrings;
		}
}
