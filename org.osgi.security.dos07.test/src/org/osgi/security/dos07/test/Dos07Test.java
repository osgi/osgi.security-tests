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

package org.osgi.security.dos07.test;

import static org.junit.Assert.fail;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

@RunWith(OSGiSecurityTestRunner.class)
public class Dos07Test {

    private final File temp = new File(System.getProperty("user.dir") + "/generated/.osgiSecurityFramework");
    private final File file = new File(temp.getPath(), "dos07_file");
    private final File folder = new File(temp.getPath(), "dos07_folder");
    private final File folder1 = new File(temp.getPath(), "dos07_folder1");
    
    /* 
	 * The value of this variable is fixed at 1000 for optimal execution of the test.
	 * For a more aggressive execution (ie a real exhaustion of system resources),
	 * put a higher value, like 1000000 or even more. 
	 */
    private final int NUMBER = 1000;

    @Test
    public void testDos07() throws Exception { 	
    	try {
		    // Log (System.getProperty("user.dir") + "/generated/.osgiSecurityFramework");
    		createFolder(temp);
		    createFolder(file);
		    createFolder(folder);
		    createFolder(folder1);

		    // Log ("Creation of a infinite number of subfolders in " + folder.getAbsolutePath());
		    createInfiniteFolder(folder);
		    Thread.sleep(1500);
		    
		    // Log ("Creation of a infinite number of subfolders recursively in " + folder.getAbsolutePath());
		    createInfiniteFolder1(folder1);
		    Thread.sleep(1500);
		    
		    // Log ("Creation of a infinite number of files in " + file.getAbsolutePath());
		    createInfiniteFile(file);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    	
    	fail("[FAIL] Files and directories can be created infinitely. Be careful to delete the .osgiSecurityFramework directory in generated directory.");
    }
    
    private void createFolder(File path) {  
    	try {
    	    if (!path.exists())
    	    	path.mkdir();
    	    if (!path.isDirectory())
    	    	throw new Exception("'"+path.getName() + "' already exists and is no directory");
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }


	private void createInfiniteFolder1(File path) {
    	File target;
    	int i = 0;

    	try {
    	    while (i < NUMBER) {
    	    	target = new File(path.getPath(),String.valueOf(i));
    	    	if (!target.mkdir()) {
    	    		throw new Exception("Too much folders");
    	    	}
    	    	path = target;
    	    	i++;
    	    }
    	} catch(Exception e) {
    	    System.out.println(e.getMessage() + " (" + i + " folders created)");
    	}
	}


	private void createInfiniteFolder(File path) {
    	File target;
    	int i = 1;

    	try {
    		while (i < NUMBER) {
    	    	target = new File(path.getPath(),String.valueOf(i));
    	    	if (!target.mkdir()) {
    	    		throw new Exception("Too much subfolders");
    	    	}
    	    	i++;
    	    }
    	} catch(Exception e) {
    	    System.out.println(e.getMessage() + " (" + i + " nested folders created)");
    	}
	}

	private void createInfiniteFile(File path) {

    	File target;
    	int i = 1;

    	try {
    		while (i < NUMBER) {
    	    	target = new File(path.getPath(),String.valueOf(i));
    	    	target.createNewFile();
    	    	i++;
    	    }
    	} catch(Exception e) {
    	   	System.out.println(e.getMessage() + " (" + i + " files created)");
    	}
	}
}
