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

#include <jni.h>
#include "org_osgi_security_code02_test_Code02Test.h"

JNIEXPORT jboolean JNICALL Java_org_osgi_security_code02_test_Code02Test_runSegFault(JNIEnv * env, jobject thisObj) {

    int *p = NULL;
    *p = 1;
    
    return true;  
}

JNIEXPORT jint JNICALL Java_org_osgi_security_code02_test_Code02Test_runEqual(JNIEnv * env, jobject thisObj) {

    int x = 5;
    return x;
}
