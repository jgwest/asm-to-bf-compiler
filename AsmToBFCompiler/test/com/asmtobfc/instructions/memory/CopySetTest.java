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
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.memory.MCopy;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class CopySetTest extends UtilTestCase {

	public void testCopySet() {
		ItrAddress[] itr = new ItrAddress[6];
		ItrAddress sign = null;
		
		int c = 30;
		for(int x = 0; x < 6; x++) {
			itr[x] = new ItrAddress(c++);
		}
		sign = new ItrAddress(c++);
		
		AsmMemoryAddress one = new AsmMemoryAddress(sign, itr);
		
		itr = new ItrAddress[6];
		for(int x = 0; x < 6; x++) {
			itr[x] = new ItrAddress(c++);
		}
		sign = new ItrAddress(c++);
		
		AsmMemoryAddress two = new AsmMemoryAddress(sign, itr);
		
		ICollection ic = new ICollection(); 
		ic.addInstruction(new MSet(one, 654321));
		ic.addInstruction(new MSet(two, 456789));
		
		int[] r = runWMemory(ic);
		
		assertTrue(checkAddr(r, one, 654321)); assertFalse(checkAddr(r, two, 654321));
		assertTrue(checkAddr(r, two, 456789)); assertFalse(checkAddr(r, one, 456789));
		
		ic.addInstruction(new MCopy(one, two));
		r = runWMemory(ic);
		assertTrue(checkAddr(r, one, 654321)); 
		assertTrue(checkAddr(r, two, 654321));
		
		ic.addInstruction(new MSet(one, -654321));
		ic.addInstruction(new MSet(two, -456789));
		
		r = runWMemory(ic);
		r = runWMemory(ic);		
		assertTrue(checkAddr(r, one, -654321)); 
		assertTrue(checkAddr(r, two, -456789));

		ic.addInstruction(new MCopy(two, one));
		r = runWMemory(ic);
		assertTrue(checkAddr(r, one, -456789)); 
		assertTrue(checkAddr(r, two, -456789));
		
		ic.addInstruction(new MSet(two, 122436));
		ic.addInstruction(new MCopy(two, one));
		r = runWMemory(ic);
		assertTrue(checkAddr(r, one, 122436)); 
		assertTrue(checkAddr(r, two, 122436));

		
	}

}
