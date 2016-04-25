package org.osgi.security.util.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ClassCastException;

import org.osgi.security.util.api.Util;
import org.osgi.security.util.api.UtilImpl;

public class UtilActivator implements BundleActivator
{
	private ServiceRegistration<?> serviceRegistration;
	private boolean displayBox, displayServer, test;
	private String serverIP;
	private int port, dport, sport;
	
	//To write log file
	private FileWriter writer = null;

	public void start(BundleContext context) throws Exception
	{
		//method to redirect the output System.out and System.err
		outputRedirection();
				
		Properties prop = new Properties();
		// pour test sur felix (RPI)
		// File file = new File("com.sogetiht.otb.properties.cfg");
		// Pour test sur livebox
		File file = new File(System.getProperty("user.dir") + "/" + "org.osgi.security.properties.cfg");
		try
		{
			//load a property file
			InputStream is = new FileInputStream(file);
			prop.load(is);
			is.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("ERROR: Could not load property file '"
					+ file.getAbsolutePath()
			        + "'. The system cannot find the file specified.");
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		
		try
		{
			//get the property values
			displayBox    = getProp(prop,"display.box","true").equals("true");
			displayServer = getProp(prop,"display.server","false").equals("true");
			serverIP      = getProp(prop,"server.ip",null);
			port          = Integer.parseInt(getProp(prop,"server.port","0"));
			dport         = Integer.parseInt(getProp(prop,"server.dport","0"));
			sport         = Integer.parseInt(getProp(prop,"server.sport","0"));
			test          = getProp(prop,"test","false").equals("true");
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}

		if (serverIP == null || port == 0)
		{
			displayServer = false;
		}
			
		try
		{
			Util service = new UtilImpl(context, displayBox, displayServer,
			          serverIP, port, dport, sport, test);
			serviceRegistration = context.registerService(Util.class.getName(), service, null);
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}

	
	private String getProp(Properties prop, String key, String defaultValue)
	{
		String property = null;
		try
		{
			property = prop.getProperty(key);
		}
		catch (ClassCastException e)
		{
			System.err.println("ERROR: The property ("+key+") contains any key or value that isn't a string. The default property ("+defaultValue+") is set");
		}
   
		if (property == null || property.equals(""))
		{
			property = defaultValue;
		}
		
		return property;
	}
	
	private void outputRedirection()
	{
		try 
		{
			//Create and erase previous log file
			writer = new FileWriter("generated/Log.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		PrintStream stream = new PrintStream(System.out)
		{
			public void println(String str)
			{
				super.print(str + System.getProperty("line.separator"));
				try 
				{
					writer.append(str, 0, str.length());
					writer.append(System.getProperty("line.separator"));
					writer.flush();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			public void print(String str)
			{
				super.print(str);
				try 
				{
					writer.append(str, 0, str.length());
					writer.flush();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
		};
		
		System.setOut(stream);
		System.setErr(stream);
	}

	public void stop(BundleContext context) throws Exception
	{
		serviceRegistration.unregister();
	}
  
}
