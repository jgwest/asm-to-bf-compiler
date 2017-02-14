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
import com.asmtobfc.instructions.intermediate.IDec;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.IInc;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IIncDecTest extends UtilTestCase {

	public void testOne() {
		ICollection ic = new ICollection();
		
		ItrAddress a = new ItrAddress(50, "a");
		ItrAddress b = new ItrAddress(80, "b");
		
		ic.addInstruction(new IInc(a));
		ic.addInstruction(new IInc(b));
		ic.addInstruction(new IInc(a));
		ic.addInstruction(new IInc(b));
		ic.addInstruction(new IInc(a));
		ic.addInstruction(new IInc(a));
		ic.addInstruction(new IInc(b));
		ic.addInstruction(new IInc(a));
		int[] fr = runWMemory(ic);
		assertTrue(fr[a.getPosition()] == 5);
		assertTrue(fr[b.getPosition()] == 3);
		
		
		ic = new ICollection();
		ic.addInstruction(new IGoto(a));
		ic.addInstruction(new IClear());		
		ic.addInstruction(new IGoto(b));
		ic.addInstruction(new IClear());
		
		for(int x = 0; x < 120; x++) {
			ic.addInstruction(new IInc(a));
		}

		for(int x = 0; x < 80; x++) {
			ic.addInstruction(new IInc(b));
		}
		
		fr = runWMemory(fr, ic);
		assertTrue(fr[a.getPosition()] == 120);
		assertTrue(fr[b.getPosition()] == 80);

		
		
		ic = new ICollection();
		ic.addInstruction(new IGoto(a));
		ic.addInstruction(new IGoto(b));
		ic.addInstruction(new ISet(a, 100));
		ic.addInstruction(new ISet(b, 100));
		
		for(int x = 0; x < 80; x++) {
			ic.addInstruction(new IDec(a));
		}

		for(int x = 0; x < 60; x++) {
			ic.addInstruction(new IDec(b));
		}

		fr = runWMemory(fr, ic);
		assertTrue(fr[a.getPosition()] == 20);
		assertTrue(fr[b.getPosition()] == 40);

		
		
		int setPositions = countWrittenPos(fr);
		assertTrue(setPositions == 2);

	}
	
	
}
