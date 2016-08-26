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

package org.osgi.security.fing06.test;

import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Fing06Test {

	@Test
	public void testFing06() {
		
		Bundle bundle = FrameworkUtil.getBundle(Fing06Test.class);
		BundleContext ctx = bundle.getBundleContext();
		
		//For all bundle in the Framework TODO maybe one test is enough
		for(Bundle currBundle : ctx.getBundles()){		
			
			assertNull("[FAIL] BundleId is accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getBundleId());
			assertNull("[FAIL] Headers are accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getHeaders());
			assertNull("[FAIL] Last modification date is accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getLastModified());
			assertNull("[FAIL] Registered services are accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getRegisteredServices());
			assertNull("[FAIL] State is accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getState());
			assertNull("[FAIL] Symbolic name is accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getSymbolicName());
			assertNull("[FAIL] Version number is accessible for the bundle: " + currBundle.getSymbolicName() + ",", currBundle.getVersion());
		
		}
	}

}
