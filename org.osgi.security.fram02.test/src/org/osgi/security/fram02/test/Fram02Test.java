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

package org.osgi.security.fram02.test;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class Fram02Test {
		
	@Test
	public void testFram02() {
		
		Bundle bundle = FrameworkUtil.getBundle(Fram02Test.class);
		BundleContext ctx = bundle.getBundleContext();
		
        for(Bundle currBundle : ctx.getBundles()){
        	//Not modify the state of bundle 0 (Framework OSGi) and this bundle	        	
        	if(!(currBundle == ctx.getBundle(0) || currBundle == bundle)){
        		//Log to list installed bundles System.out.println(currBundle);
    			try{
    				currBundle.uninstall();
    				fail("[FAIL] This bundle can modify the state of other bundles on the Framework (to STOP and UNINSTALL)");
    			}
    			catch (Exception e) {
    				//Log exception
				}
        	}
        }
	}
      
}
