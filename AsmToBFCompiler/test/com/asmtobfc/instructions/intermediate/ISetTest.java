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
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class ISetTest extends UtilTestCase {

	public void testOne() {
		ICollection ic = new ICollection();

		ISet is = null;
		
		ItrAddress a = new ItrAddress(52, "a");
		ItrAddress b = new ItrAddress(54, "b");
		ItrAddress c = new ItrAddress(56, "c");
		ItrAddress d = new ItrAddress(58, "d");		
		
		is = new ISet(c, 2); ic.addInstruction(is);
		is = new ISet(a, 10); ic.addInstruction(is);
		is = new ISet(d, 14); ic.addInstruction(is);
		is = new ISet(b, 8); ic.addInstruction(is);
		
		int[] fr = runWMemory(ic);
		
		assertTrue(fr[c.getPosition()] == 2);
		assertTrue(fr[a.getPosition()] == 10);
		assertTrue(fr[d.getPosition()] == 14);
		assertTrue(fr[b.getPosition()] == 8);
		
		int setPositions = countWrittenPos(fr);
		assertTrue(setPositions == 4);

	}

	public void testTwo() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(500, "a");
		ItrAddress b = new ItrAddress(10000, "b");
		
		
		ic.addInstruction(new ISet(b, 92));
		ic.addInstruction(new ISet(a, 50));
		
		int[] fr = runWMemory(ic);
		assertTrue(fr[b.getPosition()] == 92);
		assertTrue(fr[a.getPosition()] == 50);

		ic.addInstruction(new ISet(b, 72));
		fr = runWMemory(fr, ic);
		assertTrue(fr[b.getPosition()] == 72);
		assertTrue(fr[a.getPosition()] == 50);

		ic.addInstruction(new ISet(a, 112));
		fr = runWMemory(fr, ic);

		assertTrue(fr[b.getPosition()] == 72);
		assertTrue(fr[a.getPosition()] == 112);

		ic = new ICollection();
		ic.addInstruction(new IGoto(b));
		ic.addInstruction(new ISet(1));
		ic.addInstruction(new IGoto(a));
		ic.addInstruction(new ISet(2));
		fr = runWMemory(fr, ic);		
		assertTrue(fr[b.getPosition()] == 1);
		assertTrue(fr[a.getPosition()] == 2);
		
		ic = new ICollection();
		ic.addInstruction(new IGoto(b));
		ic.addInstruction(new ISet(1));
		ic.addInstruction(new ISet(2));
		ic.addInstruction(new ISet(3));
		ic.addInstruction(new IGoto(a));
		ic.addInstruction(new ISet(1));
		ic.addInstruction(new ISet(2));
		ic.addInstruction(new ISet(3));
		fr = runWMemory(fr, ic);
		assertTrue(fr[b.getPosition()] == 3);
		assertTrue(fr[a.getPosition()] == 3);
		
		int setPositions = countWrittenPos(fr);
		assertTrue(setPositions == 2);
		
	}

}
