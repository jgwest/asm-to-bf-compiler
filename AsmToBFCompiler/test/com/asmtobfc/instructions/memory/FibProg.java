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
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IDec;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.memory.MAdd;
import com.asmtobfc.instructions.memory.MCopy;
import com.asmtobfc.instructions.memory.MPrint;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.interpreters.FastInterpreter;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class FibProg extends UtilTestCase {

	public void testFibProg() {
		int tc = 100;
		AsmMemoryAddress a = createMemAddress(tc+=7);
		AsmMemoryAddress b = createMemAddress(tc+=7);
		AsmMemoryAddress c = createMemAddress(tc+=7);

		ItrAddress x = new ItrAddress(200);
		
		ICollection ic = new ICollection();
		ic.addInstruction(new MSet(a, BFCUtil.convInt(1)));
		ic.addInstruction(new MSet(b, BFCUtil.convInt(1)));
		ic.addInstruction(new MSet(c, BFCUtil.convInt(0)));
		
		ic.addInstruction(new ISet(x, 21));
		ic.addInstruction(new IGoto(x));
		ic.addInstruction(new IBF("["));
			ic.addInstruction(new MCopy(b, c));
			
			ic.addInstruction(new MAdd(b, a, b));
			
			ic.addInstruction(new MPrint(b));
			ic.addInstruction(new MCopy(c, a));
			ic.addInstruction(new IDec(x));
		ic.addInstruction(new IBF("]"));
		
		
		// InterpreterResult  ir = run(ic, 60);
		
		// String f = compileProgramToString(ic, 60);
		
		// this.writeFile("c:\\fib-bad.bf",f);
		
		// System.out.println(f);
		
		// System.out.println(ir.getResult());
	}	
	
	public static InterpreterEngine ie = new FastInterpreter();
	public static InterpreterResult run(IInstruction ii, int numTemp) {
		CompilerStatus cs = new CompilerStatus(numTemp);
		IInstruction program = ii.decompose(cs);
		String f = program.toBF();
		
		InterpreterResult ir = ie.runEngine(f);
		return ir;
	}

}
