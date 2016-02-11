package com.osf.util.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ClassCastException;

import com.osf.util.api.Util;
import com.osf.util.api.UtilImpl;

public class UtilActivator implements BundleActivator
{
	private ServiceRegistration<?> serviceRegistration;
	private boolean displayBox, displayServer, test;
	private String serverIP;
	private int port, dport, sport;

	public void start(BundleContext context) throws Exception
	{
		Properties prop = new Properties();
		// pour test sur felix (RPI)
		// File file = new File("com.sogetiht.otb.properties.cfg");
		// Pour test sur livebox
		File file = new File("/tmp/features/F1/com.sogetiht.otb.properties.cfg");
		try {
			//load a property file
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Could not load property file '"
					+ file.getAbsolutePath()
			        + "'. The system cannot find the file specified.");
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		
		try {
			//get the property values
			displayBox    = getProp(prop,"display.box","true").equals("true");
			displayServer = getProp(prop,"display.server","false").equals("true");
			serverIP      = getProp(prop,"server.ip",null);
			port          = Integer.parseInt(getProp(prop,"server.port","0"));
			dport         = Integer.parseInt(getProp(prop,"server.dport","0"));
			sport         = Integer.parseInt(getProp(prop,"server.sport","0"));
			test          = getProp(prop,"test","false").equals("true");
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}

		if (serverIP == null || port == 0)
			displayServer = false;

		try {
			Util service = new UtilImpl(context, displayBox, displayServer,
			          serverIP, port, dport, sport, test);
			serviceRegistration = context.registerService(Util.class.getName(), service, null);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	
	private String getProp(Properties prop, String key, String defaultValue)
	{
		String property = null;
		try {
			property = prop.getProperty(key);
		} catch (ClassCastException e) {
			System.err.println("ERROR: The property ("+key+") contains any key or value that isn't a string. The default property ("+defaultValue+") is set");
		}
    if (property == null || property.equals(""))
      property = defaultValue;

    return property;
  }


  public void stop(BundleContext context) throws Exception
  {
    serviceRegistration.unregister();
  }
}