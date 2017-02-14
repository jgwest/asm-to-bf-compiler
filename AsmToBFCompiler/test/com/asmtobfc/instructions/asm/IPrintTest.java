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

package com.asmtobfc.instructions.asm;

import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.intermediate.IPrintVal;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IPrintTest extends UtilTestCase {

	public void testIPrintTest() {
	
		for(int x = 0; x < 256; x++) {
			ItrAddress val = new ItrAddress(100);
			ICollection ic = new ICollection();

			ic.addInstruction(new ISet(val, x));
			ic.addInstruction(new IPrintVal(val));

			InterpreterResult ir = run(ic, 50);
			assertTrue(Integer.parseInt(ir.getResult()) == x); 
			
		}
		
	}
}
