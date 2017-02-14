/*
	Copyright 2011 Jonathan West

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
*/

package com.asmtobfc.utility;

import com.asmtobfc.utility.BFCUtil;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testSplit() {
		
		int r[];
		
		r = BFCUtil.split(0);
		assertTrue(r[0] == 0);
		assertTrue(r[1] == 0);
		assertTrue(r[2] == 0);
		assertTrue(r[3] == 0);
		assertTrue(r[4] == 0);
		assertTrue(r[5] == 0);

		r = BFCUtil.split(1000);
		assertTrue(r[0] == 0);
		assertTrue(r[1] == 0);
		assertTrue(r[2] == 0);
		assertTrue(r[3] == 1);
		assertTrue(r[4] == 0);
		assertTrue(r[5] == 0);
		
		r = BFCUtil.split(654321);
		assertTrue(r[0] == 1);
		assertTrue(r[1] == 2);
		assertTrue(r[2] == 3);
		assertTrue(r[3] == 4);
		assertTrue(r[4] == 5);
		assertTrue(r[5] == 6);

		r = BFCUtil.split(1000);
		assertTrue(r[0] == 0);
		assertTrue(r[1] == 0);
		assertTrue(r[2] == 0);
		assertTrue(r[3] == 1);
		assertTrue(r[4] == 0);
		assertTrue(r[5] == 0);

		r = BFCUtil.split(999999);
		assertTrue(r[0] == 9);
		assertTrue(r[1] == 9);
		assertTrue(r[2] == 9);
		assertTrue(r[3] == 9);
		assertTrue(r[4] == 9);
		assertTrue(r[5] == 9);

		r = BFCUtil.split(111111);
		assertTrue(r[0] == 1);
		assertTrue(r[1] == 1);
		assertTrue(r[2] == 1);
		assertTrue(r[3] == 1);
		assertTrue(r[4] == 1);
		assertTrue(r[5] == 1);

		r = BFCUtil.split(500);
		assertTrue(r[0] == 0);
		assertTrue(r[1] == 0);
		assertTrue(r[2] == 5);
		assertTrue(r[3] == 0);
		assertTrue(r[4] == 0);
		assertTrue(r[5] == 0);

		r = BFCUtil.split(20);
		assertTrue(r[0] == 0);
		assertTrue(r[1] == 2);
		assertTrue(r[2] == 0);
		assertTrue(r[3] == 0);
		assertTrue(r[4] == 0);
		assertTrue(r[5] == 0);

		r = BFCUtil.split(40020);
		assertTrue(r[0] == 0);
		assertTrue(r[1] == 2);
		assertTrue(r[2] == 0);
		assertTrue(r[3] == 0);
		assertTrue(r[4] == 4);
		assertTrue(r[5] == 0);


	}
}
