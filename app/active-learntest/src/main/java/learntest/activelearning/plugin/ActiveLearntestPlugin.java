package learntest.activelearning.plugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ActiveLearntestPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "active-learntest"; //$NON-NLS-1$

	// The shared instance
	private static ActiveLearntestPlugin plugin;
	
	public ActiveLearntestPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 */
	public static ActiveLearntestPlugin getDefault() {
		return plugin;
	}
}
