package org.osgi.security.sysatt110.test;

import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

/**
 * 
 **/
@RunWith(OSGiSecurityTestRunner.class)
public class SysAtt110Test
{
  	@Test
    public void testSysatt110() throws Exception
    {
  		File dir = new File("/var/log/");		
  		assertNull("[FAIL] Logs are accessible by any bundle,", dir.listFiles());
    }
}