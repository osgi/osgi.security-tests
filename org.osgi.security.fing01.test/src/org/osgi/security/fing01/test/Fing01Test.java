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

package org.osgi.security.fing01.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

import static org.junit.Assert.assertNull;

@RunWith(OSGiSecurityTestRunner.class)
public class Fing01Test {
	
	@Test
	public void testFing01(){
		
		//Get data about system environment
		assertNull("[FAIL] System environment data are accessible,", System.getenv());	
				
		//Get data about system properties
		assertNull("[FAIL] System properties are accessible,", System.getProperties());	

		/* Log ?
		System.out.println(System.getenv());
		System.out.println(System.getenv("TEMP"));
		System.out.println(System.getenv("PATH"));
		System.out.println(System.getProperties());
		System.out.println(System.getProperties("os.version"));
		*/
	}

}
