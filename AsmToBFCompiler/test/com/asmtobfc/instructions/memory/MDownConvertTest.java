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
import com.asmtobfc.instructions.memory.MDownConvert;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class MDownConvertTest extends UtilTestCase {

	public void testDownConv() {
		
		AsmMemoryAddress a = createMemAddress(100);
		ItrAddress dest = new ItrAddress(120);
		
		for(int num = 1; num < 123; num++) {
			ICollection ic = new ICollection();
			ic.addInstruction(new MSet(a, num));
			ic.addInstruction(new MDownConvert(a, dest));
			
			int[] r = runWMemory(ic, 10);
			
			assertTrue(r[dest.getPosition()] == num);
		}
		
		
		
	}
	
}
