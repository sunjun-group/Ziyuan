package tzuyu.plugin.action;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TzuyuPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "tzuyuEclipsePlugin"; //$NON-NLS-1$

	// The shared instance
	private static TzuyuPlugin plugin;
	
	/**
	 * The constructor
	 */
	public TzuyuPlugin() {
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
	public static TzuyuPlugin getDefault() {
		return plugin;
	}

}
