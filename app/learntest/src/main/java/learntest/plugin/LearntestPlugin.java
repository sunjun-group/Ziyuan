package learntest.plugin;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.commons.PluginException;
import learntest.plugin.commons.event.EmptyGentestManager;
import learntest.plugin.commons.event.IGentestManager;
import learntest.plugin.commons.event.IJavaGentestEventManager;
import learntest.plugin.commons.event.IJavaModelRuntimeInfo;
import sav.common.core.pattern.IDataProvider;

/**
 * The activator class controls the plug-in life cycle
 */
public class LearntestPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "learntest"; //$NON-NLS-1$
	private static Logger log = LoggerFactory.getLogger(LearntestPlugin.class);

	// The shared instance
	private static LearntestPlugin plugin;
	private IGentestManager javaGentestManager;
	
	public LearntestPlugin() {
		javaGentestManager = initJavaGentestManager();
	}

	protected IGentestManager initJavaGentestManager() {
		return new EmptyGentestManager();
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//LearntestConsole.delegateConsole();
		javaGentestManager.start();
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

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		javaGentestManager.stop();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 */
	public static LearntestPlugin getDefault() {
		return plugin;
	}

	public static void initLogger(String projectName) throws PluginException {
		getDefault().internalInitLogger(projectName);
	}

	protected void internalInitLogger(String projectName) throws PluginException {
		LearntestLogger.initLog4j(projectName, "learntest_log4j");
	}
	
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
	
	public static void displayView(final String viewId) {
		getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					getDefault().showCoverageView(viewId);
				} catch (PluginException e) {
					log.error(e.getMessage());
				}
			}
		});
	}

	public void showCoverageView(String viewId) throws PluginException {
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return;
		IWorkbenchPage page = window.getActivePage();
		if (page != null) {
			try {
				page.showView(viewId, null, IWorkbenchPage.VIEW_CREATE);
				//page.bringToTop(view);
			} catch (PartInitException e) {
				throw PluginException.wrapEx(e);
			}
		}
	}
	
	private IGentestManager getJavaGentestManager() {
		return javaGentestManager;
	}
	
	public static IJavaGentestEventManager getJavaGentestEventManager() {
		return getDefault().getJavaGentestManager();
	}

	public static IDataProvider<IJavaModelRuntimeInfo> getJavaModelRuntimeInfoProvider() {
		return getDefault().getJavaGentestManager();
	}
}