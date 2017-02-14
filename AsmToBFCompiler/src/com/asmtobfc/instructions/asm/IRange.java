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

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IEqual;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.memory.MPrintString;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class IRange extends IConcreteInstruction {

	final boolean DEBUG = false;
	
	private int firstValue = -1;
	private int lastValue = -1;
	private IInstruction[] inner = null;
	private ItrAddress value = null; 

	public IRange(ItrAddress valueUnderTest, int firstValue, int lastValue, IInstruction[] inner) {
		super();
		this.value = valueUnderTest;
		this.firstValue = firstValue;
		this.lastValue = lastValue;
		this.inner = inner;
	}

	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		return range(firstValue, lastValue, 0, cs);
	}

	
	private ICollection range(int first, int last, int depth, CompilerStatus cs) {
		ICollection ic = new ICollection();
		// System.out.print(Util.repeat("   ", depth));

		if(first == last) {
			// System.out.println("["+first+"]");

			ic.addInstruction(new IReturnPush());
			ItrTempAddress t1 = cs.newTemp(value); ic.addInstruction(new ITempLock(t1));
			ic.addInstruction(new ISet(t1, first));
			ic.addInstruction(new IEqual(t1, value, inner[first]));
			ic.addInstruction(new ITempUnlock(t1));
			ic.addInstruction(new IReturnPop());
			return ic;
		}
		
		if(first == last-1) {
			
			// System.out.println("["+first+"<>"+last+"]");
			ic.addInstruction(new IReturnPush());

			ItrTempAddress t1 = cs.newTemp(value); ic.addInstruction(new ITempLock(t1));
			
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
		ItrTempAddress t1 = cs.newTemp(value); ic.addInstruction(new ITempLock(t1));
		
		if(DEBUG) {
	//		System.out.println("---------------------");
			ic.addInstruction(new MPrintString("---------------------\n"));
			ic.addInstruction(new MPrintString("as:"+as+" af:"+af+"    bs:"+bs+"   bf:"+bf+"\n"));
			ic.addInstruction(new MPrintString("value:"));
	//		ic.addInstruction(new IPrintVal(value));
			ic.addInstruction(new MPrintString("\n"));
		}
		
//		System.out.println(as+" -> "+af);
//		System.out.println(bs+" -> "+bf);
		ICollection icleft = range(as, af, depth+1, cs);
		ICollection icright = range(bs, bf, depth+1, cs);
		
		ic.addInstruction(new ISet(t1, bs));
		
		// A = t1, B = value
		
		// A > B then run first param
		// if A < B then run second parm
		
		// if value >= t2 then larger (icright) 
		
		ic.addInstruction(new IGTELT(value, t1, /* value >= t1*/ icright, /* value < t1*/ icleft));
		
		ic.addInstruction(new ITempUnlock(t1));
		ic.addInstruction(new IReturnPop());
		
		
		
		return ic;
	}

}
