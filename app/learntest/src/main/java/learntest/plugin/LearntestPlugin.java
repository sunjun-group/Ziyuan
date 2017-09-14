package learntest.plugin;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import learntest.plugin.console.LearntestConsole;

/**
 * The activator class controls the plug-in life cycle
 */
public class LearntestPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "learntest"; //$NON-NLS-1$

	// The shared instance
	private static LearntestPlugin plugin;
	
	public LearntestPlugin() {
		
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		LearntestConsole.delegateConsole();
//		initLogger(null);
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		try {
//			URL installURL = getDefault().getDescriptor().getInstallURL();
			URL installURL = plugin.getBundle().getEntry("/");
			URL url = new URL(installURL, path);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LearntestPlugin getDefault() {
		return plugin;
	}

	public static void initLogger(String projectName) throws CoreException {
		LearntestLogger.initLog4j(projectName);
	}

	
	
}