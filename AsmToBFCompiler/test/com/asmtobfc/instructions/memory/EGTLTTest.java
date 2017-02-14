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
import com.asmtobfc.instructions.intermediate.IClear;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.memory.MEqual;
import com.asmtobfc.instructions.memory.MGt;
import com.asmtobfc.instructions.memory.MGtle;
import com.asmtobfc.instructions.memory.MLt;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class EGTLTTest extends UtilTestCase {

	public void testMGtle() {
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);

		ICollection ic = null;
		ItrAddress r = new ItrAddress(70);
		
		for(int x = 0; x< 30; x++) {
			ic = new ICollection();
			
			int a = (int)(Math.random()*60000f)+8000;
			int b = (int)(Math.random()*60000f)+8000;
			int makeEqual = (int)(Math.random()*8f);
			if(makeEqual == 1) {
				a = b;
			}
			ic.addInstruction(new MSet(one, a));
			ic.addInstruction(new MSet(two, b));		
			ic.addInstruction(new IClear(r));
			ic.addInstruction(new MGtle(one, two,r));
			
			int[] m = runWMemory(ic);
			
			assertTrue(checkAddr(m, one, a));
			assertTrue(checkAddr(m, two, b));
			
			int expected = -1;
			if(a == b) expected = 3;
			if(a > b) expected = 2;
			if(a < b) expected = 1;
			
			// System.out.println(a);
			// System.out.println(b);
			// System.out.println(m[r.getPosition()]);
			// System.out.println("----");
	
			
			assertTrue(m[r.getPosition()] == expected);			
		}
	}

	public void testMEqual() {
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);

		ICollection ic = null;
		ItrAddress r = new ItrAddress(70);
		
		for(int x = 0; x< 300; x++) {
			ic = new ICollection();
			
			int a = (int)(Math.random()*60000f)+8000;
			int b = (int)(Math.random()*60000f)+8000;
			int makeEqual = (int)(Math.random()*2f);
			if(makeEqual == 1) {
				a = b;
			}
			ic.addInstruction(new MSet(one, a));
			ic.addInstruction(new MSet(two, b));		
			ic.addInstruction(new IClear(r));
			
			ICollection inner = new ICollection();
				inner.addInstruction(new ISet(r, 1));
			
			ic.addInstruction(new MEqual(one, two, inner));
			
			int[] m = runWMemory(ic);
			
			assertTrue(checkAddr(m, one, a));
			assertTrue(checkAddr(m, two, b));
			
			int expected = -1;
			if(a == b) expected = 1;
			else expected = 0;
				
			assertTrue(m[r.getPosition()] == expected);			
		}
	}

	public void testMGt() {
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);

		ICollection ic = null;
		ItrAddress r = new ItrAddress(70);
		
		for(int x = 0; x< 300; x++) {
			ic = new ICollection();
			
			int a = (int)(Math.random()*60000f)+8000;
			int b = (int)(Math.random()*60000f)+8000;
			int makeEqual = (int)(Math.random()*8f);
			if(makeEqual == 1) {
				a = b;
			}
			ic.addInstruction(new MSet(one, a));
			ic.addInstruction(new MSet(two, b));		
			ic.addInstruction(new IClear(r));
			
			ICollection inner = new ICollection();
				inner.addInstruction(new ISet(r, 1));
			
			ic.addInstruction(new MGt(one, two, inner));
			
			int[] m = runWMemory(ic);
			
			assertTrue(checkAddr(m, one, a));
			assertTrue(checkAddr(m, two, b));
			
			int expected = -1;
			if(a > b) expected = 1;
			else expected = 0;
				
			assertTrue(m[r.getPosition()] == expected);			
		}
	}

	
	public void testMLt() {
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);

		ICollection ic = null;
		ItrAddress r = new ItrAddress(70);
		
		for(int x = 0; x< 300; x++) {
			ic = new ICollection();
			
			int a = (int)(Math.random()*60000f)+8000;
			int b = (int)(Math.random()*60000f)+8000;
			int makeEqual = (int)(Math.random()*8f);
			if(makeEqual == 1) {
				a = b;
			}
			ic.addInstruction(new MSet(one, a));
			ic.addInstruction(new MSet(two, b));		
			ic.addInstruction(new IClear(r));
			
			ICollection inner = new ICollection();
				inner.addInstruction(new ISet(r, 1));
			
			ic.addInstruction(new MLt(one, two, inner));
			
			int[] m = runWMemory(ic);
			
			assertTrue(checkAddr(m, one, a));
			assertTrue(checkAddr(m, two, b));
			
			int expected = -1;
			if(a < b) expected = 1;
			else expected = 0;
				
			assertTrue(m[r.getPosition()] == expected);			
		}
	}

	public void testMGte() {
		int c = 50;
		AsmMemoryAddress one = createMemAddress(c+=7);
		AsmMemoryAddress two = createMemAddress(c+=7);

		ICollection ic = null;
		ItrAddress r = new ItrAddress(70);
		
		for(int x = 0; x< 300; x++) {
			ic = new ICollection();
			
			int a = (int)(Math.random()*60000f)+8000;
			int b = (int)(Math.random()*60000f)+8000;
			int makeEqual = (int)(Math.random()*8f);
			if(makeEqual == 1) {
				a = b;
			}
			ic.addInstruction(new MSet(one, a));
			ic.addInstruction(new MSet(two, b));		
			ic.addInstruction(new IClear(r));
			
			ICollection inner = new ICollection();
				inner.addInstruction(new ISet(r, 1));
			
			ic.addInstruction(new MGte(one, two, inner));
			
			int[] m = runWMemory(ic);
			
			assertTrue(checkAddr(m, one, a));
			assertTrue(checkAddr(m, two, b));
			
			int expected = -1;
			if(a >= b) expected = 1;
			else expected = 0;
				
			assertTrue(m[r.getPosition()] == expected);			
		}
	}

	
}
