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

package com.asmtobfc.comparison;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.asm.IRange;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.interpreters.SlowInterpreter;
import com.asmtobfc.utility.BFCLaunchUtil;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IRangeTreeComparisonTest extends UtilTestCase {
	static final InterpreterEngine ie = new SlowInterpreter();

	final int RANGE_SIZE = 100;
	
	public void testFunctionTree() {

		
//		AsmMemoryAddress valueUnderTest = createMemAddress(10);
//		AsmMemoryAddress result = createMemAddress(50);
		
		ItrAddress valueUnderTest = new ItrAddress(10);
		ItrAddress result = new ItrAddress(50);
		
		
		ICollection ic = new ICollection();
		ic.addInstruction(new ISet(result, 0));
		
		ICollection[] ica = new ICollection[RANGE_SIZE];
		for(int x = 0; x < ica.length; x++) {
			ica[x] = new ICollection();
			ica[x].addInstruction(new ISet(result, x));
		}
				
		ic.addInstruction(new IRange(valueUnderTest, 0, ica.length-1, ica));

		
		int tempVarsReqd = BFCLaunchUtil.calculateTempVarsReqd(ic);
		
		CompilerStatus cs = new CompilerStatus(tempVarsReqd, 100);

		IInstruction program = ic.decompose(cs);
		
		String oldf = program.toBF();
		
		System.out.println("Program size:"+oldf.length());
		
		
		for(int x = 0; x < ica.length; x++) {
			String f = "";
			
			ic = new ICollection();
			ic.addInstruction(new ISet(valueUnderTest, BFCUtil.convInt(x)));
			
			ic.addInstruction(new IGoto(new ItrAddress(0)));
			ic.addInstruction(new IBF(oldf));
			
			tempVarsReqd = BFCLaunchUtil.calculateTempVarsReqd(ic);

			cs = new CompilerStatus(tempVarsReqd, 100);			
			
			program = ic.decompose(cs);
			f = program.toBF();
						
			InterpreterResult ir = ie.runEngine(f);
			
			System.out.println();
			System.out.println("Checking, x:"+x);
			
			System.out.println(ir.getResult());

			System.out.println(ir.getMemory()[result.getPosition()]);
			
			assertTrue(ir.getMemory()[result.getPosition()] == x);
			
//			assertTrue(checkAddr(ir.getMemory(), result, BFCUtil.convInt(x)));
//			assertTrue(checkAddr(ir.getMemory(), valueUnderTest, BFCUtil.convInt(x)));
						
		}

	}
	
}







