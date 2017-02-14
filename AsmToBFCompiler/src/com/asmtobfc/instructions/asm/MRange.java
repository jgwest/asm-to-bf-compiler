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
import com.asmtobfc.compiler.AsmTempMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.memory.MEqual;
import com.asmtobfc.instructions.memory.MGte;
import com.asmtobfc.instructions.memory.MLt;
import com.asmtobfc.instructions.memory.MPrint;
import com.asmtobfc.instructions.memory.MPrintString;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.MTempLock;
import com.asmtobfc.instructions.meta.MTempUnlock;
import com.asmtobfc.utility.BFCUtil;

public class MRange extends IConcreteInstruction {

	private static final boolean DEBUG = false; 
	
	private int firstValue = -1;
	private int lastValue = -1;
	private IInstruction[] inner = null;
	private AsmMemoryAddress value = null; 

	public MRange(AsmMemoryAddress valueUnderTest, int firstValue, int lastValue, IInstruction[] inner) {
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
			if(DEBUG) {
				ic.addInstruction(new MPrintString("Inside first == last, value:"));
				ic.addInstruction(new MPrint(value));
				ic.addInstruction(new MPrintString("\n"));
			}
			AsmTempMemoryAddress t1 = cs.newAsmMemoryTemp(value); ic.addInstruction(new MTempLock(t1));
			
			ic.addInstruction(new MSet(t1, BFCUtil.convInt(first)));
			ic.addInstruction(new MEqual(t1, value, inner[first]));
//			ic.addInstruction(new MPrint(value));
			
			ic.addInstruction(new MTempUnlock(t1));
			ic.addInstruction(new IReturnPop());
			return ic;
		}
		
		if(first == last-1) {
			
			// System.out.println("["+first+"<>"+last+"]");
			ic.addInstruction(new IReturnPush());

			
			AsmTempMemoryAddress t1 = cs.newAsmMemoryTemp(value); ic.addInstruction(new MTempLock(t1));
			
			if(DEBUG) {
				ic.addInstruction(new MPrintString("Inside first == last-1, ["+first+" to "+last+"]\n"));
			}
			
			// if(value < last) do inner[first];
			ic.addInstruction(new MSet(t1, BFCUtil.convInt(last)));
			ic.addInstruction(new MLt(value, t1, inner[first])); 

			
			// if(value >= last) do inner[last];
			ic.addInstruction(new MSet(t1, BFCUtil.convInt(last)));
			ic.addInstruction(new MGte(value, t1, inner[last])); 
			
			ic.addInstruction(new MTempUnlock(t1));
			ic.addInstruction(new IReturnPop());

			return ic;
		}
		
		int width = last-first+1;

		int as = first;
		int af = first + (width/2)-1;
		int bs = af+1;
		int bf = last;
		
		
		ic.addInstruction(new IReturnPush());
		AsmTempMemoryAddress t1 = cs.newAsmMemoryTemp(value); ic.addInstruction(new MTempLock(t1));		

		if(DEBUG) {
			ic.addInstruction(new MPrintString("---------------------\n"));
			ic.addInstruction(new MPrintString("as:"+as+" af:"+af+"    bs:"+bs+"   bf:"+bf+"\n"));
			ic.addInstruction(new MPrintString("value:"));
			ic.addInstruction(new MPrint(value));
			ic.addInstruction(new MPrintString("\n"));
			
//			System.out.println(as+" -> "+af);
//			System.out.println(bs+" -> "+bf);
		}
		ICollection icleft = range(as, af, depth+1, cs);
		ICollection icright = range(bs, bf, depth+1, cs);
		
		ic.addInstruction(new MSet(t1, BFCUtil.convInt(bs)));
		
		// A = t1, B = value
		
		// A > B then run first param
		// if A < B then run second parm
		
		// if value >= t2 then larger (icright) 
	
		
		// if(value >= t1) do(icright); 
		ic.addInstruction(new MGte(value, t1, icright));
		
		// if(value < t1) do (icleft);
		ic.addInstruction(new MLt(value, t1, icleft));
				
		ic.addInstruction(new MTempUnlock(t1));
		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

}
