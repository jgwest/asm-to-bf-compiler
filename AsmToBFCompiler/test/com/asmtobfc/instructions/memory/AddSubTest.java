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

package com.asmtobfc.instructions.memory;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.memory.MAdd;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.instructions.memory.MSub;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class AddSubTest extends UtilTestCase {

	public void testSub() {
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);
		AsmMemoryAddress three = createMemAddress(c+=7);
		
		ICollection ic = null; 
		
		int x = 0;
		while(x++ < 333) {
			ic = new ICollection();
			int n1 = 50000+ x *80;
			int n2 = 40000 + x * 230;
			int result = n1 - n2 + 40000;
			if(result < 40000-32768) continue;
			
			ic.addInstruction(new MSet(one, n1));
			ic.addInstruction(new MSet(two, n2));
			ic.addInstruction(new MSet(three, 0));
			
			ic.addInstruction(new MSub(one, two, three));
			
			int[] r = runWMemory(ic, 40);
	
//			printAtAddr(three, r);
			
			assertTrue(checkAddr(r, three, result));
		}
	}

	
	public void testAdd() {
		
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);
		AsmMemoryAddress three = createMemAddress(c+=7);
		
		ICollection ic = null; 
		
		int x = 0;
		while(x++ < 300) {
			ic = new ICollection();
			int n1 = 20000+ x *150;
			int n2 = 20000 + x * 60;
			int result = n1 + n2 - 40000;
			if(result < 40000-32768) continue;
			
			ic.addInstruction(new MSet(one, n1));
			ic.addInstruction(new MSet(two, n2));
			ic.addInstruction(new MSet(three, 0));
			
			ic.addInstruction(new MAdd(one, two, three));
			
			int[] r = runWMemory(ic, 40);
	
//			printAtAddr(three, r);
			
			assertTrue(checkAddr(r, three, result));
		}
	}
		
}
