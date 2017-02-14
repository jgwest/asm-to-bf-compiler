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
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IDEqual;
import com.asmtobfc.instructions.intermediate.IEqual;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IEqualTest extends UtilTestCase {

	public void testDEqual() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		
		ic.addInstruction(new ISet(a, 30));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new ISet(c, 62));
		
		ICollection inner = new ICollection();
		inner.addInstruction(new ISet(c, 20));
		inner.addInstruction(new ISet(c, 12));
		
		ic.addInstruction(new IDEqual(a, b, inner));
		
		int[] result = runWMemory(ic);
				
		assertTrue(result[c.getPosition()] == 12); 
	}
	
	public void testDEqual2() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		ItrAddress d = new ItrAddress(33);
		
		ic.addInstruction(new ISet(a, 30));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new ISet(c, 62));
		ic.addInstruction(new ISet(d, 20));

		
		ICollection inner = new ICollection();
		inner.addInstruction(new ISet(c, 20));
		
		ICollection inner2 = new ICollection();
		inner.addInstruction(new IDEqual(c, d, inner2));
		
		inner2.addInstruction(new ISet(d, 12));
		
		ic.addInstruction(new IDEqual(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		assertTrue(result[d.getPosition()] == 12); 
		
	}

	public void testEqual() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		
		ic.addInstruction(new ISet(a, 30));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new ISet(c, 62));
		
		ICollection inner = new ICollection();
			inner.addInstruction(new ISet(c, 20));
			inner.addInstruction(new ISet(c, 12));
		
		ic.addInstruction(new IEqual(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		assertTrue(result[a.getPosition()] == 30);
		assertTrue(result[b.getPosition()] == 30);
		assertTrue(result[c.getPosition()] == 12); 
	}

	public void testEqual2() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(62);
		ItrAddress d = new ItrAddress(33);
		
		ic.addInstruction(new ISet(a, 30));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new ISet(c, 62));
		ic.addInstruction(new ISet(d, 20));

		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(c, 20));
		
				ICollection inner2 = new ICollection();
				inner2.addInstruction(new IBF(""));
				inner2.addInstruction(new ISet(d, 12));

			inner.addInstruction(new IEqual(c, d, inner2));
			
		
		ic.addInstruction(new IEqual(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		assertTrue(result[a.getPosition()] == 30);
		assertTrue(result[b.getPosition()] == 30);
		assertTrue(result[c.getPosition()] == 20);
		assertTrue(result[d.getPosition()] == 12); 
	}

	public void testEqual3() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		ItrAddress d = new ItrAddress(33);
		ItrAddress e = new ItrAddress(34);
		
		ic.addInstruction(new ISet(a, 30));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new ISet(c, 62));
		ic.addInstruction(new ISet(d, 20));
		ic.addInstruction(new ISet(e, 34));

		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(c, 20));
		
				ICollection inner2 = new ICollection();
				inner2.addInstruction(new ISet(d, 12));
				inner2.addInstruction(new ISet(a, 12));
				
					ICollection inner3 = new ICollection();
					inner3.addInstruction(new ISet(b, 12));
					
						ICollection inner4 = new ICollection();
						inner4.addInstruction(new ISet(e, 3));
					
					inner3.addInstruction(new IEqual(b, d, inner4));		
				
				inner2.addInstruction(new IEqual(d, a, inner3));

			inner.addInstruction(new IEqual(c, d, inner2));
			
		ic.addInstruction(new IEqual(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		assertTrue(result[a.getPosition()] == 12);
		assertTrue(result[b.getPosition()] == 12);
		assertTrue(result[c.getPosition()] == 20);
		assertTrue(result[d.getPosition()] == 12);
		assertTrue(result[e.getPosition()] == 3); 
	}
	
	public void testEqual4() {
		ICollection ic = new ICollection();

		ItrAddress a = new ItrAddress(30);
		ItrAddress b = new ItrAddress(31);
		ItrAddress c = new ItrAddress(32);
		ItrAddress d = new ItrAddress(33);
		ItrAddress e = new ItrAddress(34);
		
		ic.addInstruction(new ISet(a, 30));
		ic.addInstruction(new ISet(b, 30));
		ic.addInstruction(new ISet(c, 62));
		ic.addInstruction(new ISet(d, 20));
		ic.addInstruction(new ISet(e, 34));

		
			ICollection inner = new ICollection();
			inner.addInstruction(new ISet(c, 20));
		
				ICollection inner2 = new ICollection();
				inner2.addInstruction(new ISet(d, 12));
				inner2.addInstruction(new ISet(a, 12));
				
					ICollection inner3 = new ICollection();
					inner3.addInstruction(new ISet(b, 100));
					
						ICollection inner4 = new ICollection();
						inner4.addInstruction(new ISet(e, 3));
					
					inner3.addInstruction(new IEqual(b, d, inner4));		
				
				inner2.addInstruction(new IEqual(d, a, inner3));

			inner.addInstruction(new IEqual(c, d, inner2));
			
		ic.addInstruction(new IEqual(a, b, inner));
		
		int[] result = runWMemory(ic);
		
		assertTrue(result[a.getPosition()] == 12);
		assertTrue(result[b.getPosition()] == 100);
		assertTrue(result[c.getPosition()] == 20);
		assertTrue(result[d.getPosition()] == 12);
		assertTrue(result[e.getPosition()] == 34); 
	}

}
