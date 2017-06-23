package learntest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sav.common.core.utils.ConfigUtils;
import sav.common.core.utils.FileUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "learntest"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		ImageRegistry imgReg = getImageRegistry();
//		imgReg.put(ImageUI.CHECK_MARK, getImageDescriptor(ImageUI.CHECK_MARK));
//		imgReg.put(ImageUI.WRONG_VALUE_MARK, getImageDescriptor(ImageUI.WRONG_VALUE_MARK));
//		imgReg.put(ImageUI.WRONG_PATH_MARK, getImageDescriptor(ImageUI.WRONG_PATH_MARK));
//		imgReg.put(ImageUI.QUESTION_MARK, getImageDescriptor(ImageUI.QUESTION_MARK));
		initLog4j();
	}
	
	public static void initLog4j() throws Exception {
		ResourceBundle log4j = ResourceBundle.getBundle("learntest_log4j");
		Properties props = new Properties();
		for (String key : log4j.keySet()) {
			props.setProperty(key, log4j.getString(key));
		}
		/* TODO temporary get default files using user.dir */
		if (props.getProperty("log4j.appender.file.File") == null) {
			props.setProperty("log4j.appender.file.File", FileUtils.getFilePath(ConfigUtils.getProperty("user.dir"),
					"learntest-eclipse.log"));
		}
		PropertyConfigurator.configure(props);
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
	public static Activator getDefault() {
		return plugin;
	}

	
	
}