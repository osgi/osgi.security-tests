/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.osgi.security.fram01.test;

import java.io.File;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Fram01Test {
	
	@Test
	public void testFram01(){
		
		final String INJECTED_BUNDLE_PATH = "injectedBundleFram01.jar";
		final String FORMATED_INJECTED_BUNDLE_PATH = "file:" + INJECTED_BUNDLE_PATH; 
		
		Bundle bundle = FrameworkUtil.getBundle(Fram01Test.class);
		BundleContext ctx = bundle.getBundleContext();        
		
		try{	
			assertTrue(INJECTED_BUNDLE_PATH + " was not found", new File(INJECTED_BUNDLE_PATH).exists());
			ctx.installBundle(FORMATED_INJECTED_BUNDLE_PATH).start();
			fail("[FAIL] This bundle can install and start another bundle on the platform: "+ ctx.getBundle(FORMATED_INJECTED_BUNDLE_PATH));
		} catch (Exception e) {
			//Log Error
		}
	}
	
}
