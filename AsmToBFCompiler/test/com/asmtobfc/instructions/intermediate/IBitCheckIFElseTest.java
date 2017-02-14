/*
	Copyright 2012 Jonathan West

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
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IBitCheckIFElseTest extends UtilTestCase {


	public void testEqualsOne() {
		ICollection ic = new ICollection();

		ItrAddress valueToTest = new ItrAddress(10);
		ItrAddress result = new ItrAddress(20);

		
		ic.addInstruction(new ISet(valueToTest, 1));
		
		
		ICollection isOne = new ICollection();
			isOne.addInstruction(new ISet(result, 101));

		ICollection isZero = new ICollection();
			isZero.addInstruction(new ISet(result, 100));
		
		ic.addInstruction(new IBitCheckIfElse(valueToTest, isOne, isZero));
		
		int[] mem = runWMemory(ic);
		
		assertTrue(mem[result.getPosition()] == 101);
	}

	public void testEqualsZero() {
		ICollection ic = new ICollection();

		ItrAddress valueToTest = new ItrAddress(10);
		ItrAddress result = new ItrAddress(20);

		
		ic.addInstruction(new ISet(valueToTest, 0));
		
		
		ICollection isOne = new ICollection();
			isOne.addInstruction(new ISet(result, 101));

		ICollection isZero = new ICollection();
			isZero.addInstruction(new ISet(result, 100));
		
		ic.addInstruction(new IBitCheckIfElse(valueToTest, isOne, isZero));
		
		int[] mem = runWMemory(ic);
		
		assertTrue(mem[result.getPosition()] == 100);
	}


}
