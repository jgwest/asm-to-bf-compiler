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
import com.asmtobfc.instructions.intermediate.IJumpNZ;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class JumpNZTest extends UtilTestCase {
	
	public void testJumpNZTest1() {
		
		ItrAddress a = new ItrAddress(50);
		ItrAddress b = new ItrAddress(51);
		
		ICollection ic = new ICollection();
		ic.addInstruction(new ISet(a, 10));
		ic.addInstruction(new ISet(b, 20));
		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(b, 100));
			inner.addInstruction(new ISet(a, 100));
		
		ic.addInstruction(new IJumpNZ(a, inner));
		
		int[] r = runWMemory(ic);
		
		assertTrue(r[b.getPosition()] == 20);
			
		
	}
	
	public void testJumpNZTest2() {
		
		ItrAddress a = new ItrAddress(50);
		ItrAddress b = new ItrAddress(51);
		
		ICollection ic = new ICollection();
		ic.addInstruction(new ISet(a, 0));
		ic.addInstruction(new ISet(b, 20));
		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(b, 100));
			inner.addInstruction(new ISet(a, 100));
		
		ic.addInstruction(new IJumpNZ(a, inner));
		
		int[] r = runWMemory(ic);
		
		assertTrue(r[b.getPosition()] == 100);
			
		
	}



}
