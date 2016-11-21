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

package org.osgi.security.dos031.test;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

import java.math.BigInteger;

@RunWith(OSGiSecurityTestRunner.class)
public class Dos031Test {
    
	/* 
	 * The value of this variable is fixed at 10000 for optimal execution of the test.
	 * For a more aggressive execution (ie a real exhaustion of system resources),
	 * put a higher value, like 1000000 or even more. 
	 */
	private final int NUM = 10000; 
	
    @Test
    public void testDos031() throws Exception {
    	
    	int cpt = 0;
    	BigInteger n = new BigInteger("3");
    	
    	while (cpt < NUM) {
    		factorial(n);
    		n = n.add(BigInteger.ONE);
    		cpt++;
    	}
    	
    	fail("[FAIL] System resources can be exhausted by resource consuming operations.");
    }
    
    private BigInteger factorial(BigInteger n) {
    	
        if (n.compareTo(BigInteger.ZERO) == 0) {
          return BigInteger.ONE;
        }
   
        BigInteger r = BigInteger.ONE;
        
        while (n.compareTo(BigInteger.ZERO) != 0) {
          r = r.multiply(n);
          n = n.add(BigInteger.ONE.negate());
        }
        
        return r;
      }
}
