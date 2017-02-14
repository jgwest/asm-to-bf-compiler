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
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.memory.MPrint;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.interpreters.FastInterpreter;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class MPrintTest extends UtilTestCase {
	static final InterpreterEngine ie = new FastInterpreter();

	public void testMPrint() {
		int c = 80;
		AsmMemoryAddress one = createMemAddress(c+=7);
		
		ICollection ic = null;
		
		int y = BFCUtil.convInt(32768);
		for(int x = BFCUtil.convInt(-32768); x < y; x+=2087) {
			ic = new ICollection();
			ic.addInstruction(new MSet(one, x));
			ic.addInstruction(new MPrint(one));
			
			
			CompilerStatus cs = new CompilerStatus(60);
			IInstruction program = ic.decompose(cs);

			InterpreterResult ir = ie.runEngine(program.toBF());
			String s = ir.getResult().trim();
			
			while(s.contains("-0")) {
				s = s.replace("-0", "-");
			}
			while(s.startsWith("0")&& s.length() > 1) {
				s = s.substring(1);
			}
			
			assertTrue(s.equalsIgnoreCase(""+(x-40000)));
		}
		
	}
}
