/*
	Copyright 2011, 2012 Jonathan West

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

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IBinaryRange;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.memory.MCopy;
import com.asmtobfc.instructions.memory.MDownConvertToBinary;
import com.asmtobfc.instructions.memory.MPrint;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.interpreters.SlowInterpreter;
import com.asmtobfc.parser.AsmMemoryMap;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class MemFunctionTreeTest extends UtilTestCase {
	static final InterpreterEngine ie = new SlowInterpreter();

	public void testFunctionTree() {
		final int SIZE_OF_PROGRAM_MEM = 50;
		AsmMemoryMap map = new AsmMemoryMap(10, SIZE_OF_PROGRAM_MEM+700, 700);
		
//		map.printAssignments();
		
		// Create the copy array
		ICollection[] ica = new ICollection[SIZE_OF_PROGRAM_MEM];
		for(int x = 0; x < ica.length; x++) {
			AsmMemoryAddress ma = map.getProgMemory(x);
			AsmMemoryAddress dest = map.getPointerCopy();
			
			ica[x] = new ICollection();
//			ica[x].addInstruction(new MPrintString("-------\ndest:"));
//			ica[x].addInstruction(new MPrint(dest));
			ica[x].addInstruction(new MCopy(dest, ma));
//			ica[x].addInstruction(new MPrintString("\nma:"));
//			ica[x].addInstruction(new MPrint(ma));
//			ica[x].addInstruction(new MPrintString("\n[At "+x+"]\n"));
		}

		ICollection ic = new ICollection();
		ic.addInstruction(new MSet(map.getPointerCopy(), BFCUtil.convInt(12222)));
		ic.addInstruction(new MPrint(map.getPointerCopy()));
		
		CompilerStatus cs = new CompilerStatus(390);
		
		ic.addInstruction(new IBinaryRange(map.getMemoryPointerArr(), ica));
		ic.addInstruction(new MPrint(map.getPointerCopy()));

		
		IInstruction program = ic.decompose(cs);
				
		String oldf = program.toBF();
		
		InterpreterResult ir = ie.runEngine(oldf);		
		
		for(int x = 0; x < SIZE_OF_PROGRAM_MEM; x++) {
			System.out.println("x:"+x);
			String f = "";
			cs = new CompilerStatus(390);
			ic = new ICollection();
			ic.addInstruction(new MSet(map.getRegister(0), BFCUtil.convInt(x)));
			ic.addInstruction(new MDownConvertToBinary(map.getRegister(0), map.getMemoryPointerArr()));
			ic.addInstruction(new IGoto(new ItrAddress(0)));
			ic.addInstruction(new IBF(oldf));
			
			program = ic.decompose(cs);
			f = program.toBF();
						
			ir = ie.runEngine(f);
			System.out.println(ir.getResult());
			assertTrue(this.checkAddr(ir.getMemory(), map.getProgMemory(x), BFCUtil.convInt(12222)));
		}

	}

}
