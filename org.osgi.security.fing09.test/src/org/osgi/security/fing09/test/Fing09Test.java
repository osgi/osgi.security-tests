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

package org.osgi.security.fing09.test;

import static org.junit.Assert.fail;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

@RunWith(OSGiSecurityTestRunner.class)
public class Fing09Test {
	
	private File outputDirectory  = new File(System.getProperty("user.home"), ".osgiSecurityFramework");
	private FileWriter fw;
	private BufferedWriter bw;
	
	@Test
	public void testFing09() {

		String path = System.getProperty("user.home") + "/.bashrc";	
		PrintStream orgStream = System.out;		
		
	    try {
		  bw.write("Ecriture dans le fichier " + path + "\n");

	      PrintStream fileStream = new PrintStream(new FileOutputStream(path,true));
	      System.setOut(fileStream);
	      System.setOut(orgStream);
	      fail("[FAIL] Method found (java.lang.System.setOut()). Are security options actived ?");
	    } catch (java.lang.NoSuchMethodError e){
    	// Log (("Method not found (java.lang.System.setOut()). Are security options actived ?");
	    } catch (Exception e) {
	      err(e);
	    }
	}
	
	@Before
	public void initFile(){
		try {
	      if (!outputDirectory.exists())
	    	  outputDirectory.mkdir();
	      if (!outputDirectory.isDirectory())
	        throw new Exception("'.osgiSecurityFramework' already exists and is no directory");
	      File outputFile = new File(outputDirectory.getPath(), "fing09Test");
	      fw = new FileWriter(outputFile.getAbsoluteFile());
	      bw = new BufferedWriter(fw);

	    } catch(Exception e) {
	      err(e);
	    }
	}
	
	@After
	public void closeFile() {
	    try {
	      bw.close();
	      fw.close();
	    } catch(Exception e) {
	      err(e);
	    }
	  }
	
	private void err(Exception e) {
		try {
	      String str = e.getClass().getName();
	      String message = e.getMessage();

	      if (message != null)
	      	bw.write("!!: " + str + ": " + message +"\n");
	      else
	      	bw.write("!!: " + str + "\n");
	    } catch(Exception ex) {
	      System.err.println(ex.getMessage());
	    }
	}

}
