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

package org.osgi.security.dos0331.test;

import static org.junit.Assert.fail;
import org.junit.Test;

public class Dos0331Test {
	
	/* 
	 * The value of this variable is fixed at 10000 for optimal execution of the test.
	 * For a more aggressive execution (ie a real exhaustion of system resources),
	 * put a higher value, like 1000000 or even more. 
	 */
	private final int NUMBER = 10000;
	
    @Test
    public void testDos033() throws Exception {
    	
    	int cpt = 0;
    	while (cpt < NUMBER) {
    		A a = new A(0);
    		cpt = a.getNbInstance();
    		System.out.println("Number of A class instances : " + cpt);
    	}
    	fail("[FAIL] Infinite number of classes can be instanciated.");
    }
}
