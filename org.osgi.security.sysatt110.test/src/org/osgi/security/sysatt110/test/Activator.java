package org.osgi.security.sysatt110.test;

import org.osgi.framework.BundleContext;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import org.osgi.security.util.api.Util;

import java.lang.System;
import java.io.File;

/**
 * 
 */

public class Activator implements BundleActivator
{
	private static BundleContext bundleContext;
	private static ServiceReference<?> service;
    private Util util;
    private boolean succeed = false;

	static BundleContext getContext()
    {
		return bundleContext;
    }
	
	static ServiceReference<?> getService()
    {
		return service;
    }

	public void start(BundleContext context) throws Exception
  	{
  		Activator.bundleContext = context;
    }
  	
  	public void stop(BundleContext context) throws Exception
  	{
  		Activator.bundleContext = null;
    }
  	
  	@Test
    public void testSysatt110() throws Exception
    {
  		service = getContext().getServiceReference(Util.class.getName());
    	if (service != null)
    	{
    		util = (Util) getContext().getService(getService());
        	if (util != null)
        	{
        		util.start("sysatt110","Récupération de fichiers de logs","Récupération des fichiers pertinents de log système");
        	    try
        	    {
        	    	util.sendCmd("sysatt110");
        	    	File dir = new File("/var/log/");
        	        File list[] ;
        	        list = dir.listFiles();
        	        Assert.assertNotNull("no access to /var/log/", list);
        	        for (int i=0; i<list.length; i++)
        	        {
        	        	if (list[i].isFile()&&list[i].canRead())
        	        	{
        	        		util.sendFile(list[i]);
        	        	}
        	        }
        	        util.sendCmd("::done::");
        	    }
        	    catch (Exception e)
        	    {
        	        util.err(e);
        	    }
        	    succeed = true;
        	    util.stop(succeed);
        	}
        	else
        	{
        		System.err.println("Service not available. Please install the package org.osgi.security.util.api.jar");
        	}
    	}
    	else
    	{
        	ServiceListener listener = new ServiceListener()
        	{
        		public void serviceChanged(ServiceEvent e)
        		{
        			service = e.getServiceReference();
        			switch (e.getType())
        			{
    	            	case ServiceEvent.REGISTERED:
    	            		util = (Util) getContext().getService(getService());
    	                	if (util != null)
    	                	{
    	                		util.start("sysatt110","Récupération de fichiers de logs","Récupération des fichiers pertinents de log système");
    	                	    try
    	                	    {
    	                	    	util.sendCmd("sysatt110");
    	                	    	File dir = new File("/var/log/");
    	                	        File list[] ;
    	                	        list = dir.listFiles();
    	                	        Assert.assertNotNull("no access to /var/log/", list);
    	                	        for (int i=0; i<list.length; i++)
    	                	        {
    	                	        	if (list[i].isFile()&&list[i].canRead())
    	                	        	{
    	                	        		util.sendFile(list[i]);
    	                	        	}
    	                	        }
    	                	        util.sendCmd("::done::");
    	                	    }
    	                	    catch (Exception e1)
    	                	    {
    	                	        util.err(e1);
    	                	    }
    	                	    succeed = true;
    	                	    util.stop(succeed);
    	                	}
    	                	else
    	                	{
    	                		System.err.println("Service not available. Please install the package org.osgi.security.util.api.jar");
    	                	}
							break;
    	             	default:
    	            		// Nothing
    	            		break;
        			}
    	        }
    	    };
    	    String filter = "(" + Constants.OBJECTCLASS + "=" + Util.class.getName() + ")";
    	    getContext().addServiceListener(listener, filter);
    	}
  		Assert.assertTrue("Test passed", succeed);
    }
}
