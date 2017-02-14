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

package com.asmtobfc.interpreter;

import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.IInc;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class InterpreterTest extends UtilTestCase {

	public void testJumpOnNotZero() {
		ICollection ic = new ICollection();
		
		ItrAddress a = new ItrAddress(21);
		ItrAddress b = new ItrAddress(22);
		ItrAddress c = new ItrAddress(23);
		
		ic.addInstruction(new ISet(a, 0));
		ic.addInstruction(new ISet(b, 10));
		ic.addInstruction(new ISet(c, 20));

		ic.addInstruction(new IGoto(a));
		ic.addInstruction(new IBF("("));
		ic.addInstruction(new ISet(b, 20));
		ic.addInstruction(new IInc(a));
		ic.addInstruction(new IBF(")"));

		ic.addInstruction(new IGoto(a));
		ic.addInstruction(new IBF("("));
		ic.addInstruction(new ISet(c, 30));
		ic.addInstruction(new IInc(a));
		ic.addInstruction(new IBF(")"));

		
		int[] r = runWMemory(ic);
		
		assertTrue(r[b.getPosition()] == 20);
		assertTrue(r[c.getPosition()] == 20);
		
	}
	
}
