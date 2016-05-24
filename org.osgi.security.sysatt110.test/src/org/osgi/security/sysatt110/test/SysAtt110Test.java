package org.osgi.security.sysatt110.test;

import static org.osgi.framework.FrameworkUtil.getBundle;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.security.util.api.Util;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 **/

public class SysAtt110Test
{
	private BundleContext bundleContext = getBundle(SysAtt110Test.class).getBundleContext();
    private Util util;
    private boolean succeed = false;

    @Before
	public void before() throws Exception
  	{
    	ServiceTracker<Util, Util> serviceRef = new ServiceTracker<>(bundleContext, Util.class, null);
  		serviceRef.open();
  		util = (Util) serviceRef.waitForService(30000); 
  		Assert.assertNotNull("JUnit test issue: util service is not available even after 30 s", util);
    }
  	
    @After
	public void after()
	{
    	if(util != null) {
    		util.stop(succeed);
    	}
	}
  	
  	@Test
    public void testSysatt110() throws Exception
    {
  		util.start("sysatt110","Access to system log directory","Access to /var/log directory, which contains system log files");
  		
  		util.println("\n[TEST] Checking access to /var/log directory...\n");
  		File dir = new File("/var/log/");
		File list[] ;
		list = dir.listFiles();
		
		Assert.assertNull("JUnit test issue: logs are accessible by any bundle.", list);
		
		succeed = true;
    }
}