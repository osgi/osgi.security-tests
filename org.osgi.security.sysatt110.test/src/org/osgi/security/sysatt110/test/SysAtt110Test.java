package org.osgi.security.sysatt110.test;

import static org.osgi.framework.FrameworkUtil.getBundle;
import static org.junit.Assert.*;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
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
    private File list[];

    @Before
	public void before() throws Exception
  	{
    	ServiceTracker<Util, Util> serviceRef = new ServiceTracker<>(bundleContext, Util.class, null);
  		serviceRef.open();
  		util = (Util) serviceRef.waitForService(30000); 
  		assertNotNull("JUnit test issue: util service is not available even after 30 s", util);
    }
  	
    @After
	public void after()
	{
    	if (util != null) {
    		util.stop(succeed);
    	}
	}
  	
    @Rule
    public Verifier verifier = new Verifier()
    {
    	protected void verify()
    	{
    		assertNull("[FAIL] Logs are accessible by any bundle,", list);
    	}
    };
    
  	@Test
    public void testSysatt110() throws Exception
    {
  		util.start(true);		
  		File dir = new File("/var/log/");		
		list = dir.listFiles();
    }
}