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

package org.osgi.security.fing08.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import java.io.File;
import org.junit.Test;

public class Fing08Test {

	@Test
	public void testfing08() {
		
		//Files
        checkPath(new File(System.getProperty("user.home")+"/.bashrc"));
        checkPath(new File(System.getProperty("user.home")+"/.bash_profile"));
        checkPath(new File(System.getProperty("user.home")+"/.bash_logout"));
        checkPath(new File(System.getProperty("user.home")+"/.ssh/authorized_keys"));
        checkPath(new File(System.getProperty("user.home")+"/.rhosts"));
        checkPath(new File("/etc/ssh/sshd_util"));
        checkPath(new File("/etc/ssh/profile"));
        checkPath(new File("/etc/aliases"));
        checkPath(new File("/etc/hosts"));
        checkPath(new File("/etc/hosts.allow"));
        checkPath(new File("/etc/hosts.deny"));
        checkPath(new File("/etc/resolv.conf"));
        checkPath(new File("/etc/adduser.conf"));
        checkPath(new File("/etc/shadow"));
        checkPath(new File("/etc/passwd"));
        checkPath(new File("/etc/group"));
        checkPath(new File("/etc/pam.conf"));
        checkPath(new File("/etc/cron.d/anacron"));
        checkPath(new File("/etc/network/interfaces"));
        checkPath(new File("/var/spool/cron/crontabs/root"));
        checkPath(new File("/etc/sudoers"));
        checkPath(new File("/etc/cron.allow"));
        checkPath(new File("/etc/cron.deny"));
        checkPath(new File("/etc/bash_completion"));
        checkPath(new File("/etc/bash.bashrc"));
        checkPath(new File("/etc/inittab"));

        //Folders
        checkPath(new File("/etc"));
        checkPath(new File("/etc/default"));
        checkPath(new File(System.getProperty("user.home")+"/.ssh"));
        checkPath(new File("/etc/cron.hourly"));
        checkPath(new File("/etc/cron.daily"));
        checkPath(new File("/etc/cron.monthly"));
        checkPath(new File("/etc/cron.weekly"));
        checkPath(new File("/var/spool/cron/crontabs"));
        checkPath(new File("/etc/profile.d"));
        checkPath(new File("/etc/init.d"));
        checkPath(new File("/boot/grub"));
        checkPath(new File("/etc/pam.d"));
        checkPath(new File("/etc/kernel"));

        // All Files in a folder
        checkFolder(new File("/etc/init.d"));
        checkFolder(new File("/etc/default"));
        checkFolder(new File("/etc/pam.d"));
        checkFolder(new File("/boot/grub"));
        checkFolder(new File("/etc/kernel"));	
	}
	
	
	private void checkPath(File file) {

	    try {
	      if (file.exists()) {
	        if (file.canWrite()) {
	        //Log  util.println("***** "+ file.getAbsolutePath() + " CAN BE MODIFIED *****");
	        } else {
	        //Log  util.println(file.getAbsolutePath() + " cannot be modified");
	        }
	        assertFalse("[FAIL] " + file.getAbsolutePath() +" file can be modified", file.canWrite());        
	      }else {
	        try {
	          file.createNewFile();
	        // Log util.println("***** " +file.getAbsolutePath() + " CAN BE CREATED *****");
	          file.delete();
	          fail("[FAIL] " + file.getAbsolutePath() +" file can be created");
	        } catch (Exception e) {
	        // Log util.println(file.getAbsolutePath() + " cannot be created");
	        }
	      }
	    } catch (Exception e) {
	      // Log  util.err(e);
	    }
	  }

	  private void checkFolder(File path) {
	    File files[];
	    
	    if (path.isDirectory()){
	      files= path.listFiles();
	      for(int i=0; i<files.length; i++)
	        checkPath(files[i]);
	    }
	  }

}
