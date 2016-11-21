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

package org.osgi.security.fing07.test;

import java.io.File;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.security.test.api.OSGiSecurityTestRunner;

@RunWith(OSGiSecurityTestRunner.class)
public class Fing07Test {
	
	@Test
	public void testfing07(){
	
		File folder = new File(System.getProperty("user.home"), ".osgiSecurityFramework");
		try{
		
			if(!folder.exists())
				folder.mkdir();
			if(!folder.isDirectory())
				throw new Exception("'.osgiSecurityFramework' already exists and is no directory");
	
			File symFolder = new File(folder.getPath(), "fing07_symLink");
			if(!symFolder.exists())
				symFolder.mkdir();
			if(!symFolder.isDirectory())
				throw new Exception("'fing07_symLink' already exists and is no directory");

			String symFolderPath = symFolder.getCanonicalPath();  
			createSymbolicLink("/etc/passwd", symFolderPath);
			createSymbolicLink("/etc/shadow", symFolderPath);
			createSymbolicLink("/bin/rm", symFolderPath);
			createSymbolicLink("/bin/pwd", symFolderPath);
			createSymbolicLink("/bin/chmod", symFolderPath);
			createSymbolicLink("/", symFolderPath + "/root");
			createSymbolicLink("/bin", symFolderPath + "/bin");
			createSymbolicLink("/etc", symFolderPath + "/etc");
			createSymbolicLink("/usr", symFolderPath + "/usr");
			createSymbolicLink("/var/log", symFolderPath + "/log");
			
			assertFalse("[FAIL] Symbolic link pointing on /etc/passwd can be created", new File(symFolderPath +"/passwd").canRead());
			assertFalse("[FAIL] Symbolic link pointing on /etc/shadow can be created", new File(symFolderPath +"/shadow").canRead());
			assertFalse("[FAIL] Symbolic link pointing on /bin/rm can be created", new File(symFolderPath +"/rm").canRead());
			assertFalse("[FAIL] Symbolic link pointing on /bin/pwd can be created", new File(symFolderPath +"/pwd").canRead());
			assertFalse("[FAIL] Symbolic link pointing on /bin/chmod can be created", new File(symFolderPath +"/chmod").canRead());
			assertNull("[FAIL] Symbolic link pointing on /root can be created", new File(symFolderPath +"/root").list());
			assertNull("[FAIL] Symbolic link pointing on /bin can be created", new File(symFolderPath +"/bin").list());
			assertNull("[FAIL] Symbolic link pointing on /etc can be created", new File(symFolderPath +"/etc").list());
			assertNull("[FAIL] Symbolic link pointing on /usr can be created", new File(symFolderPath +"/usr").list());
			assertNull("[FAIL] Symbolic link pointing on /var/log can be created", new File(symFolderPath +"/log").list());
		
		} catch (Exception e) {
			
			// Log => .err(e)
		}
	}
	
	private void createSymbolicLink(String src, String dst) {
		try {
			Runtime.getRuntime().exec(new String[] {"/bin/ln", "-s", src, dst});
		} catch (java.lang.NoSuchMethodError e){
			//Log => Method not found (java.lang.Runtime.exec()). Are security options actived ?
			
		} catch (Exception e){
			//Log => .err(e)
		}
	}

}



