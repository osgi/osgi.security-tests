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

package org.osgi.security.dos0332.test;

import static org.junit.Assert.fail;
import org.junit.Test;

public class Dos0332Test {
    
    @Test
    public void testDos0332() throws Exception {
    	
    	try {
            Thread t = new Thread(new RecursiveThread(0));
            t.start();
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
    	
    	Thread.sleep(30000);
    	
    	fail("[FAIL] Threads can use system resources without limits.");
    }
}
