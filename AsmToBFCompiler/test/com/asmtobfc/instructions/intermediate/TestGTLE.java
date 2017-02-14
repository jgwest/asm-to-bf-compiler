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
import com.asmtobfc.instructions.intermediate.IDGtle;
import com.asmtobfc.instructions.intermediate.IGT;
import com.asmtobfc.instructions.intermediate.ILT;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class TestGTLE extends UtilTestCase {

	public void testGtleFunc() {
		for(int x = 0; x < 100; x+= 7) {
			for(int y =0; y < 100; y+= 7) {
				int expectedresult = (x == y ? 3 : (x > y ? 2 : 1));
				assertTrue(doGtleFunc(x, y) == expectedresult);		
			}
		} 
	}
	
	public void testGtFunc() {
		for(int x = 0; x < 100; x+= 7) {
			for(int y =0; y < 100; y+= 7) {
				boolean expectedresult = x > y;
				assertTrue(doGt(x, y) == expectedresult);		
			}
		} 
	}

	public void testLtFunc() {
		for(int x = 0; x < 100; x+= 7) {
			for(int y =0; y < 100; y+= 7) {
				boolean expectedresult = x < y;
				assertTrue(doLt(x, y) == expectedresult);		
			}
		} 
	}

	
	public boolean doLt(int va, int vb) {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		
		ic.addInstruction(new ISet(a, va));
		ic.addInstruction(new ISet(b, vb));
		ic.addInstruction(new ISet(c, 11));
		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(c, 23));
		
		ic.addInstruction(new ILT(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		return result[c.getPosition()] == 23;
	
	}

	public boolean doGt(int va, int vb) {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		
		ic.addInstruction(new ISet(a, va));
		ic.addInstruction(new ISet(b, vb));
		ic.addInstruction(new ISet(c, 11));
		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(c, 23));
		
		ic.addInstruction(new IGT(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		return result[c.getPosition()] == 23;
	
	}
	
	public int doGtleFunc(int va, int vb) {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress r = new ItrAddress(32);
		
		ic.addInstruction(new ISet(a, va));
		ic.addInstruction(new ISet(b, vb));
		ic.addInstruction(new ISet(r, 62));
		
		ic.addInstruction(new IDGtle(a, b, r));
		
		int[] result = runWMemory(ic);
		
		return result[r.getPosition()];
	}
	
	
	

}
