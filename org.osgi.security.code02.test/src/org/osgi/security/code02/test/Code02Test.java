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

package org.osgi.security.code02.test;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

@RunWith(OSGiSecurityTestRunner.class)
public class Code02Test {    

	private native boolean runSegFault();
	private native int runEqual();
	
	static
	{
		System.loadLibrary("native");
	}
	
    @Test
    public void testCode02() throws Exception {
    	//runSegFault();
    	assertNotEquals("[FAIL] Native code has been loaded and executed by the system", 5, runEqual());			
    }
}
