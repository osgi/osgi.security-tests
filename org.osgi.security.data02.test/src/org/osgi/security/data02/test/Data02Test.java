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

package org.osgi.security.data02.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class Data02Test {
	
	@Test
	public void test02Data() {
		
		final String INVALID_BUNDLE_PATH = "invalidBundleData02.jar";
		final String FORMATED_INVALID_BUNDLE_PATH = "file:" + INVALID_BUNDLE_PATH; 
		
		Bundle bundle = FrameworkUtil.getBundle(Data02Test.class);
		BundleContext ctx = bundle.getBundleContext();
		
		try {
			assertTrue(INVALID_BUNDLE_PATH + " was not found", new File(INVALID_BUNDLE_PATH).exists());
			ctx.installBundle(FORMATED_INVALID_BUNDLE_PATH).start();
		} catch (BundleException e) {
			fail("[FAIL] Bundle with non ASCII character in its manifest file has created an BundleException : " + e.getMessage());
			//Nevertheless with this method, invalidBundle is still present on the platform in installed state
		}
	}

}
