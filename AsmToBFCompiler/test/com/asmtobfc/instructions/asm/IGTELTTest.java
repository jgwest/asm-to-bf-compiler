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

package com.asmtobfc.instructions.asm;

import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.asm.IGTELT;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IGTELTTest extends UtilTestCase {

	public void testIGTELT() {
		ItrAddress a = new ItrAddress(100);
		ItrAddress b = new ItrAddress(101);
		ItrAddress c = new ItrAddress(102);
		
		ICollection innerLt = new ICollection();
			innerLt.addInstruction(new ISet(c, 1));

		ICollection innerGte = new ICollection();
			innerGte.addInstruction(new ISet(c, 100));
			
		ICollection ic = new ICollection();
		
		ic.addInstruction(new ISet(a, 10));
		ic.addInstruction(new ISet(b, 10));
		
		
		// Test a == b
		ic.addInstruction(new ISet(c, 50));
		
		ic.addInstruction(new IGTELT(a, b, innerGte, innerLt));
		
		int[] r;
		
		r = runWMemory(ic, 50);

		assertTrue(r[c.getPosition()] == 100);
		

		// Test a < b
		
		ic.addInstruction(new ISet(b, 20)); // a < b
		ic.addInstruction(new IGTELT(a, b, innerGte, innerLt));
		
		r = runWMemory(ic, 50);

		assertTrue(r[a.getPosition()] < r[b.getPosition()]);
		
		assertTrue(r[c.getPosition()] == 1);
		
		
		// Test a > b
		ic.addInstruction(new ISet(b, 5)); // a > b
		ic.addInstruction(new IGTELT(a, b, innerGte, innerLt));
		
		r = runWMemory(ic, 50);

		assertTrue(r[b.getPosition()] < r[a.getPosition()]);
		assertTrue(r[c.getPosition()] == 100);

	}
}
