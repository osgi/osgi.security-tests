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

package org.osgi.security.dos08.test;

import static org.junit.Assert.fail;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.junit.Test;

public class Dos08Test {

    private File folder = new File(System.getProperty("user.dir") + "/generated/.osgiSecurityFramework");
    
    /* 
	 * The value of this variable is fixed at 99666 for optimal execution of the test.
	 * For a more aggressive execution (ie a real exhaustion of system resources),
	 * put a higher value, like 1000000 or even more. 
	 */
    private final int NUMBER = 99666;
    
    @Test
    public void testDos08() throws Exception {
    	try{
    		if (!folder.exists())
    			folder.mkdir();
    		if (!folder.isDirectory())
    			throw new Exception("'.osgiSecurityFramework' already exists and is no directory");

            folder = new File(folder.getAbsolutePath(), "dos08_data");
            if (!folder.exists())
            	folder.mkdir();
            if (!folder.isDirectory())
            	throw new Exception("'dos08_data' already exists and is no directory");

            createBigFile("dos08_Zombie_Data");
            createBigFile(folder.getAbsolutePath() + "/Zombie_Data");

    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	fail("[FAIL] Zombie data created.  Be careful to delete the .osgiSecurityFramework directory in generated directory and dos08_Zombie_Data file.");
    }
    
    private void createBigFile(String path) {
        try {
        	File file = new File(path);
        	if (!file.exists()) {
        		file.createNewFile();
        	} else {
        		throw new Exception(file.getAbsolutePath() + ": Zombie Data still there. Did you try to uninstall and reinstall the bundle ?");
        	}

        	FileWriter fw = new FileWriter(file.getAbsolutePath());
        	BufferedWriter bw = new BufferedWriter(fw);
        	
        	for (int i=0; i<NUMBER; i++)
        		bw.write("This is the content to write into file");
        	
        	bw.close();
        } catch (Exception e) {
          	System.out.println(e.getMessage());
        }
    }
}
