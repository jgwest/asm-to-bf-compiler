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
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class MDoubleDownConvertTest extends UtilTestCase {

	public void testMDoubleDownConvertTest() {
	
		for(int x = 0; x < 9999; x++) {
			AsmMemoryAddress a = createMemAddress(100);
			
			ItrAddress msb = new ItrAddress(100);
			ItrAddress lsb = new ItrAddress(101);
			
			ICollection ic = new ICollection();

			ic.addInstruction(new MSet(a, BFCUtil.convInt(x)));
			ic.addInstruction(new MDoubleDownConvert(a, msb, lsb));

			InterpreterResult ir = run(ic, 50);			
			
			assertTrue(ir.getMemory()[lsb.getPosition()] == x % 100);
			assertTrue(ir.getMemory()[msb.getPosition()] == (x/100)%100);
			
		}
		
	}
}
