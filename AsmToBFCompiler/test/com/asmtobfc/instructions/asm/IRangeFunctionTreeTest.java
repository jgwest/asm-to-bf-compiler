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

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.asm.IRange;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.IPrint;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.interpreters.SlowInterpreter;
import com.asmtobfc.parser.AsmMemoryMap;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class IRangeFunctionTreeTest extends UtilTestCase {
	static final InterpreterEngine ie = new SlowInterpreter();

	
	public void testFunctionTree() {

		ItrAddress v = new ItrAddress(100); 		
		ItrAddress r = new ItrAddress(101);

		ICollection ic = new ICollection();
		// ic.addInstruction(new ISet(v, 50));
		ic.addInstruction(new ISet(r, 0));
		
		ICollection[] ica = new ICollection[120];
		for(int x = 0; x < ica.length; x++) {
			ica[x] = new ICollection();
			ica[x].addInstruction(new ISet(r, x));
		}
		
		CompilerStatus cs = new CompilerStatus(90);
		
		// ic.addInstruction(range(0, ica.length-1, 0,ica, cs));
		ic.addInstruction(new IRange(v, 0, ica.length-1, ica));
		// ic.addInstruction(new IPrint(r));
		
		IInstruction program = ic.decompose(cs);
		
		String oldf = program.toBF();
		
		// System.out.println("running...");
		
		for(int x = 0; x < ica.length; x++) {
			String f = "";
			cs = new CompilerStatus(90);
			ic = new ICollection();
			ic.addInstruction(new ISet(v, x));
			ic.addInstruction(new IGoto(new ItrAddress(0)));
			ic.addInstruction(new IBF(oldf));
			
			program = ic.decompose(cs);
			f = program.toBF();
						
			InterpreterResult ir = ie.runEngine(f);
			
			assertTrue(ir.getMemory()[r.getPosition()] == x);
			// System.out.println(ir.getResult());
			// System.out.println(ir.getMemory()[r.getPosition()]);
		}

	}
	
	public void testFunctionTree2() {
		AsmMemoryMap map = new AsmMemoryMap(8, 20, 130);
		
		AsmMemoryAddress r = map.getProgMemory(18);
		
		
		
		IInstruction[] ica = new IInstruction[10];
		for(int x = 0; x < ica.length-1; x++) {
			ica[x] = new ICollection();
			
			ica[x].addInstruction(new MSet(r, BFCUtil.convInt(x)));
			ica[x].addInstruction(new IGoto(map.getFuncVal()));
			ica[x].addInstruction(new IBF("+"));
		}
		
		ica[ica.length-1] = new ISet(map.getContinueProgram(), 0);
		
		// Program start
		ICollection ic = new ICollection();
		ic.addInstruction(new ISet(map.getFuncVal(), 0));		
		ic.addInstruction(new ISet(map.getContinueProgram(), 1));
		ic.addInstruction(new IBF("["));
			
			ic.addInstruction(new IPrint(map.getFuncVal()));		
			ic.addInstruction(new IRange(map.getFuncVal(), 0, ica.length-1, ica));
			
			ic.addInstruction(new IGoto(map.getFuncVal()));
			ic.addInstruction(new IGoto(map.getContinueProgram()));
		ic.addInstruction(new IBF("]"));
		
		
		// ic.addInstruction(new MPrint(r));
		// ic.addInstruction(i)
		
		
		
		InterpreterResult ir = runB(ic, 110);
		System.out.println(ir.getResult());

	}

}








/*
  	public void range(int first, int last, int depth) {
		System.out.print(Util.repeat("   ", depth));

		if(first == last) {
			System.out.println("["+first+"]");
			return;
		}
		
		if(first == last-1) {
			System.out.println("["+first+"<>"+last+"]");
			return; 
		}
		
		System.out.println("["+first+"-"+last+"]");
		
		int width = last-first+1;
		
		int as = first;
		int af = first + (width/2)-1;
		int bs = af+1;
		int bf = last;
		
		range(as, af, depth+1);
		range(bs, bf, depth+1);
		
	}
*/

/*


private ItrLabel value = null;

public ICollection range(int first, int last, int depth, ICollection[] inner, CompilerStatus cs) {
	ICollection ic = new ICollection();
	// System.out.print(Util.repeat("   ", depth));

	if(first == last) {
		// System.out.println("["+first+"]");

		ic.addInstruction(new IReturnPush());
		ItrTempLabel t1 = cs.newTemp(); ic.addInstruction(new ITempLock(t1));
		ic.addInstruction(new ISet(t1, first));
		ic.addInstruction(new IEqual(t1, value, inner[first]));
		ic.addInstruction(new ITempUnlock(t1));
		ic.addInstruction(new IReturnPop());
		return ic;
	}
	
	if(first == last-1) {
		
		// System.out.println("["+first+"<>"+last+"]");
		ic.addInstruction(new IReturnPush());

		ItrTempLabel t1 = cs.newTemp(); ic.addInstruction(new ITempLock(t1));
		
		ic.addInstruction(new ISet(t1, last));
					
		ic.addInstruction(new IGTELT(value, t1, inner[last], inner[first]));

		ic.addInstruction(new ITempUnlock(t1));
		ic.addInstruction(new IReturnPop());

		return ic;
	}
	
	// System.out.println("["+first+"-"+last+"]");
	
	int width = last-first+1;

	int as = first;
	int af = first + (width/2)-1;
	int bs = af+1;
	int bf = last;
	
	
	ic.addInstruction(new IReturnPush());
	ItrTempLabel t1 = cs.newTemp(); ic.addInstruction(new ITempLock(t1));
	
	ICollection icleft = range(as, af, depth+1, inner, cs);
	ICollection icright = range(bs, bf, depth+1, inner, cs);
	
	ic.addInstruction(new ISet(t1, bs));
	
	// A = t1, B = value
	
	// A > B then run first param
	// if A < B then run second parm
	
	// if value >= t2 then larger (icright) 
	
	ic.addInstruction(new IGTELT(value, t1, icright, icleft));
	
	ic.addInstruction(new ITempUnlock(t1));
	ic.addInstruction(new IReturnPop());
	
	return ic;
}
*/