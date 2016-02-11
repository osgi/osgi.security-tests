package com.osf.osgi.sysatt110.test;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.osf.util.api.Util;

import java.lang.System;
import java.io.File;

/**
 * 
 */

public class Activator implements BundleActivator {

	private static BundleContext bundleContext;
    private Util util;
    private boolean succeed = true;

	static BundleContext getContext()
    {
		return bundleContext;
    }

  	public void start(BundleContext context) throws Exception
    {
  		Activator.bundleContext = context;
    	ServiceReference<?> service = getContext().getServiceReference(
    	Util.class.getName());
    	if (service != null)
    	{
    		main(service);
    	}
        else
        {
        	ServiceListener listener = new ServiceListener()
        	{
        		public void serviceChanged(ServiceEvent e)
    	        {
        			ServiceReference<?> service = e.getServiceReference();
        			switch(e.getType())
        			{
    	            	case ServiceEvent.REGISTERED:
    	            		main(service);
    	            		break;
    	            	default:
    	            		// Nothing
    	            		break;
        			}
    	        }
    	    };

    	    String filter = "(" + Constants.OBJECTCLASS + "="
    	    		+ Util.class.getName() + ")";
    	    getContext().addServiceListener(listener, filter);
    	}
    }

  	private void main (ServiceReference<?> service)
    {
  		util = (Util) getContext().getService(service);
    	if (util != null)
    	{
    		util.start("sysatt110","Récupération de fichiers de logs","Récupération des fichiers pertinents de log système");
    	    try {
    	    	util.sendCmd("sysatt110");
    	    	File dir = new File("/var/log/");
    	        File list[] ;
    	        list = dir.listFiles();

    	        for (int i=0; i<list.length; i++) {
    	        	if (list[i].isFile()&&list[i].canRead()) {
    	        		util.sendFile(list[i]);
    	        	}
    	        }
    	        util.sendCmd("::done::");
    	    } catch (Exception e) {
    	        succeed = false;
    	        util.err(e);
    	    }
    	    util.stop(succeed);
    	}
    	else
    	{
    		System.err.println("Service not available. Please install the package com.sogetiht.otb.util.jar");
    	}
    }

  	public void stop(BundleContext context) throws Exception
    {
  		Activator.bundleContext = null;
    }
  	
  	@Test
    public void testSysatt110() throws Exception {
    	Assert.assertNotNull(bundleContext);
    }
}
