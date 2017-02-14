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
import com.asmtobfc.instructions.intermediate.IDAdd;
import com.asmtobfc.instructions.intermediate.IDSub;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class DAddDSubTest extends UtilTestCase {

	public void testDAdd1() {
		ICollection ic = new ICollection();
		ItrAddress a = new ItrAddress(30, "a");
		ItrAddress b = new ItrAddress(31, "b");
		
		ic.addInstruction(new ISet(a, 50));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new IDAdd(a, b));
		
		int[] r = runWMemory(ic);
		
		assertTrue(r[a.getPosition()] == 80);
		
		assertTrue(countWrittenPos(r) == 1);
	}
	
	public void testDAdd2() {
		ICollection ic = new ICollection();
		ItrAddress a = new ItrAddress(30, "a");
		ItrAddress b = new ItrAddress(31, "b");
		
		int[] r = null;
		
		for(int x = 0; x< 60; x++) {
			int an = x;
			int bn = (x + 22)% 60;
			
			ic.addInstruction(new ISet(a, an));
			ic.addInstruction(new ISet(b, bn));
			ic.addInstruction(new IDAdd(a, b));
			r = runWMemory(ic);
			assertTrue(r[a.getPosition()] == an + bn);

		}
		assertTrue(countWrittenPos(r) == 1);

	}

	public void testDAdd3() {
		ICollection ic = new ICollection();
		ItrAddress a = new ItrAddress(30, "a");
		ItrAddress b = new ItrAddress(31, "b");
		
		int[] r = null;
		
		for(int x = 0; x< 60; x++) {
			int an = x;
			int bn = (x + 22)% 60;
			
			ic.addInstruction(new ISet(a, an));
			ic.addInstruction(new ISet(b, bn));
			ic.addInstruction(new IDAdd(a, b));
			r = runWMemory(ic);
			assertTrue(r[a.getPosition()] == an + bn);
			
			a = new ItrAddress(40+x, "a");
			b = new ItrAddress(42+x*2, "b");
		}
	}
	

	public void testDSub2() {
		ICollection ic = new ICollection();
		ItrAddress a = new ItrAddress(30, "a");
		ItrAddress b = new ItrAddress(31, "b");
		
		int[] r = null;
		
		for(int x = 0; x< 60; x++) {
			int an = x + (x/2);
			int bn = (x)% 30;
			if(an - bn < 0) continue;
			
			ic.addInstruction(new ISet(a, an));
			ic.addInstruction(new ISet(b, bn));
			ic.addInstruction(new IDSub(a, b));
			r = runWMemory(ic);
			assertTrue(r[a.getPosition()] == an - bn);
		}
		
		assertTrue(countWrittenPos(r) == 1);
	}

	public void testDSub3() {
		ICollection ic = new ICollection();
		ItrAddress a = new ItrAddress(30, "a");
		ItrAddress b = new ItrAddress(31, "b");
		
		int[] r = null;
		
		for(int x = 0; x< 60; x++) {
			int an = x + (x/2);
			int bn = (x)% 30;
			if(an - bn < 0) continue;
			
			ic.addInstruction(new ISet(a, an));
			ic.addInstruction(new ISet(b, bn));
			ic.addInstruction(new IDSub(a, b));
			r = runWMemory(ic);
			assertTrue(r[a.getPosition()] == an - bn);
			
			a = new ItrAddress(40+x, "a");
			b = new ItrAddress(42+x*2, "b");
		}
	}

	
}
