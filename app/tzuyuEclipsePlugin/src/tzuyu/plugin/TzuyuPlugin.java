package tzuyu.plugin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import tzuyu.engine.utils.StringUtils;
import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.exception.ErrorType;
import tzuyu.plugin.reporter.PluginLogger;
import tzuyu.plugin.view.dfa.DfaView;

/**
 * The activator class controls the plug-in life cycle
 */
public class TzuyuPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "tzuyu.eclipse.plugin"; //$NON-NLS-1$
	public static final String DFA_VIEW_ID = "tzuyu.plugin.view.dfa"; //$NON-NLS-1$
	public static final String ICON_PATH = "icons/";
	
	// The shared instance
	private static TzuyuPlugin plugin;
	private Messages messages;
	private Map<String, ImageDescriptor> imageDescriptors;
	
	/**
	 * The constructor
	 */
	public TzuyuPlugin() {
		imageDescriptors = new HashMap<String, ImageDescriptor>();
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		messages = new Messages();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public Messages getInternalMessages() {
		return messages;
	}
	
	public static Messages getMessages() {
		return getDefault().getInternalMessages();
	}

	/**
	 * Returns the shared instance
	 */
	public static TzuyuPlugin getDefault() {
		return plugin;
	}
	
	public GenTestPreferences getGenTestPreferences(IJavaProject project) {
		GenTestPreferences prefs = new GenTestPreferences(project, false);
		Preferences projectNode = getProjectPreferencesNode(project.getProject());
		if (projectNode != null) {
			prefs.read(projectNode);
		}
		return prefs;
	}

	private Preferences getProjectPreferencesNode(IProject project) {
		IScopeContext projectScope = new ProjectScope(project);
		Preferences projectNode = projectScope.getNode(PLUGIN_ID);
		return projectNode;
	}
	
	public void persistGenTestPreferences(IProject project, GenTestPreferences prefs) {
		Preferences projectNode = getProjectPreferencesNode(project);
		if (projectNode != null) {
			prefs.write(projectNode);
			try {
				projectNode.flush();
			} catch (BackingStoreException e) {
				PluginLogger.logEx(e, ErrorType.CANNOT_SAVE_PROJECT_PREFERENCES);
			}
		}
	}
	
	public void persistPreferences(IProject project, String key, Object value,
			String defaultIfNull) {
		Preferences projectNode = getProjectPreferencesNode(project);
		if (projectNode != null) {
			projectNode.put(key, StringUtils.toString(value, defaultIfNull));
			try {
				projectNode.flush();
			} catch (BackingStoreException e) {
				PluginLogger.logEx(e, ErrorType.CANNOT_SAVE_PROJECT_PREFERENCES);
			}
		}
	}
	
	// show views
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        if (Display.getCurrent() != null) {
            return getDefault().getWorkbench().getActiveWorkbenchWindow();
        }
        // need to call from UI thread
        final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                window[0] = getDefault().getWorkbench().getActiveWorkbenchWindow();
            }
        });
        return window[0];
    }
    
    public static IViewPart showDfaView() {
    	return showView(DFA_VIEW_ID);
    }
    
    public ImageDescriptor getImageDescriptor(String id) {
        ImageDescriptor imageDescriptor = imageDescriptors.get(id);
        if (imageDescriptor == null) {
            String pluginId = getDefault().getBundle().getSymbolicName();
            imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, ICON_PATH + id);
            imageDescriptors.put(id, imageDescriptor);
        }
        return imageDescriptor;
    }
    
    public static DfaView getDfaShowedView() {
    	IViewPart view = getShowedView(DFA_VIEW_ID);
    	if (view == null || !(view instanceof DfaView)) {
    		return null;
    	}
		return (DfaView)view;
    }
    
    public static IViewPart getShowedView(String id) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IViewReference reference = window.getActivePage().findViewReference(id);
			if (reference != null) {
				return reference.getView(false);
			}
		}
		return null;
	}
    
	private static IViewPart showView(String viewId) {
        IWorkbenchPage page = getActiveWorkbenchWindow().getActivePage();
        try {
            return page.showView(viewId);
        } catch (PartInitException e) {
        	PluginLogger.logEx(e, "Could not show view with Id=" + viewId);
        }
        return null;
    }
}
