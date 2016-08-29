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

public class RecursiveThread implements Runnable {
	
	private int num;

	public RecursiveThread (int num) {
		this.num = num;
	}

	public void run() {
		try {
			Thread t = new Thread(new RecursiveThread(num+1));
			t.start();
			for (int b=0;b<1;b++)
				b--;
		} catch (Exception e) {

	    }
	}
}
