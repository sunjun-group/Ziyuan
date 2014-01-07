package tzuyu.plugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.constants.Messages;
import tzuyu.plugin.core.exception.ErrorType;
import tzuyu.plugin.reporter.PluginLogger;

/**
 * The activator class controls the plug-in life cycle
 */
public class TzuyuPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "tzuyu.eclipse.plugin"; //$NON-NLS-1$

	// The shared instance
	private static TzuyuPlugin plugin;
	private Messages messages;
	
	/**
	 * The constructor
	 */
	public TzuyuPlugin() {
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
	
	public Messages getMessages() {
		return messages;
	}

	/**
	 * Returns the shared instance
	 */
	public static TzuyuPlugin getDefault() {
		return plugin;
	}
	
	public GenTestPreferences getGenTestPreferences(IProject project) {
		GenTestPreferences prefs = new GenTestPreferences();
		Preferences projectNode = getProjectPreferencesNode(project);
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
}
