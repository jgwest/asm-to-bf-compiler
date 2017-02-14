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

package com.asmtobfc.instructions.intermediate;

import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.intermediate.IClear;
import com.asmtobfc.instructions.intermediate.ICopy;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class ICopyTest extends UtilTestCase {
	
	public void testCopy() {
		
		ICollection ic = new ICollection();
		
		ItrAddress a = new ItrAddress(51, "a");
		ItrAddress b = new ItrAddress(52, "b");
		ItrAddress c = new ItrAddress(53, "c");
		
		ic.addInstruction(new ISet(a, 20));
		ic.addInstruction(new ISet(b, 40));
		ic.addInstruction(new ISet(c, 90));
		
		ic.addInstruction(new ICopy(a, b));
		
		int[] r = runWMemory(ic);
		
		assertTrue(r[a.getPosition()] ==  20);
		assertTrue(r[b.getPosition()] == 20);
		assertTrue(r[c.getPosition()] == 90);
		
		assertTrue(countWrittenPos(r) == 3);
		
	}
	
	public void testCopy2() {
		
		ICollection ic = new ICollection();
		
		ItrAddress a = new ItrAddress(51, "a");
		ItrAddress b = new ItrAddress(52, "b");
		ItrAddress c = new ItrAddress(53, "c");
		
		ic.addInstruction(new ISet(a, 10));
		ic.addInstruction(new ISet(b, 20));
		ic.addInstruction(new ISet(c, 30));
		
		int[] r = null;
		
		ic.addInstruction(new ICopy(a, b));
		ic.addInstruction(new ICopy(c, b));
		r = runWMemory(ic);

		assertTrue(r[a.getPosition()] == 10);
		assertTrue(r[b.getPosition()] == 30);
		assertTrue(r[c.getPosition()] == 30);

		ic.addInstruction(new ICopy(b, a));
		r = runWMemory(r, ic);
		
				
		assertTrue(r[a.getPosition()] == 30);
		assertTrue(r[b.getPosition()] == 30);
		assertTrue(r[c.getPosition()] == 30);
		
		assertTrue(countWrittenPos(r) == 3);
	}

	public void testCopy3() {
		ItrAddress l[] = new ItrAddress[10];
		ICollection ic = new ICollection();
		
		for(int x = 0; x < l.length; x++) {
			l[x] = new ItrAddress(50+x);
			ic.addInstruction(new ISet(l[x], x+1));
		}
	
		int currPos = 50;
		for(int y = 0; y < 100; y++) {
			currPos+=l.length;
			for(int x = 0; x < l.length; x++) {
				
				ItrAddress oldlabel = l[x];
				ItrAddress newlabel = new ItrAddress(l[x].getPosition()+l.length);
				ic.addInstruction(new ICopy(oldlabel, newlabel));
				ic.addInstruction(new IClear(oldlabel)); 
				l[x] = newlabel;
			}
		}
		
		int[] r = runWMemory(ic);
		assertTrue(countWrittenPos(r) == l.length);	

		for(int x = 0; x < l.length; x++) {
			assertTrue(r[x+currPos] == x+1);
		}		

	}
}
