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

package com.asmtobfc.instructions.memory;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.AsmTempMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IDAdd;
import com.asmtobfc.instructions.intermediate.IDSub;
import com.asmtobfc.instructions.intermediate.IDec;
import com.asmtobfc.instructions.intermediate.IGT;
import com.asmtobfc.instructions.intermediate.IInc;
import com.asmtobfc.instructions.intermediate.ILT;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;
import com.asmtobfc.instructions.meta.MTempLock;
import com.asmtobfc.instructions.meta.MTempUnlock;
import com.asmtobfc.utility.BFCUtil;

public class MSub extends IConcreteInstruction {
	AsmMemoryAddress left = null;
	AsmMemoryAddress right = null;
	AsmMemoryAddress result = null;
	
	
	public MSub(AsmMemoryAddress left, AsmMemoryAddress right, AsmMemoryAddress result) {
		this.left = left;
		this.right = right;
		this.result = result;
	}
	
	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		AsmTempMemoryAddress t1 = cs.newAsmMemoryTemp(left); ic.addInstruction(new MTempLock(t1));
		AsmTempMemoryAddress t2 = cs.newAsmMemoryTemp(right); ic.addInstruction(new MTempLock(t2));
		AsmTempMemoryAddress t3 = cs.newAsmMemoryTemp(left); ic.addInstruction(new MTempLock(t3));
		
		ItrTempAddress ten = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(ten));
		
		ic.addInstruction(new MCopy(left, t1));
		ic.addInstruction(new MCopy(right, t2));
		
		if(t1.getContents().length != t2.getContents().length) throw(new RuntimeException("Lengths do not match."));
		
		ic.addInstruction(new MSet(t3, BFCUtil.ZERO_CONSTANT));
		
		// Add 40000 to the left operand
		for(int x = 0; x < t1.getContents().length; x++) {
			ItrAddress l = t1.getField(x);
			
			ItrAddress ln = null; // ln is the value to the left of l
			if(x+1 < t1.getContents().length)  {
				ln = t1.getField(x+1);
			}
			
			ItrAddress r = t3.getField(x); // T3 contains zero constant (currently, 40000)
			
			ic.addInstruction(new IDAdd(l, r)); // Add l[x] + t3[x], store the result in l[x]
			
			ICollection inner = new ICollection();
			
				inner.addInstruction(new ISet(ten, 10));
				inner.addInstruction(new IDSub(l, ten));
				if(ln != null) inner.addInstruction(new IInc(ln));
			
			ic.addInstruction(new ISet(ten, 9));
			ic.addInstruction(new IGT(l, ten, inner)); // If the result is > 10, subtract 10, and "carry the one" 
			
			ic.addInstruction(new ISet(ten, 10));
			ic.addInstruction(new IDAdd(l, ten)); 
		}		
		
		
		// Subtract right operand from left operand 
		for(int x = 0; x < t1.getContents().length; x++) {
			ItrAddress l = t1.getField(x);
			
			ItrAddress ln = null; // ln is the value to the left of l
			if(x+1 < t1.getContents().length)  {
				ln = t1.getField(x+1);
			}
			
			ItrAddress r = t2.getField(x);

			ic.addInstruction(new IDSub(l, r));
			
			ic.addInstruction(new ISet(ten, 10));
			ICollection inner = new ICollection();
				if(ln != null) inner.addInstruction(new IDec(ln));	
			ic.addInstruction(new ILT(l, ten, inner));
			
			
			ICollection inner2 = new ICollection();
				// inner2.addInstruction(new ISet(l, 10));
				inner2.addInstruction(new ISet(ten, 10));
				inner2.addInstruction(new IDSub(l, ten));
			
			ic.addInstruction(new ISet(ten, 9));
			ic.addInstruction(new IGT(l, ten, inner2));
		
		}
		
		ic.addInstruction(new MCopy(t1, result));
		
		ic.addInstruction(new ITempUnlock(ten));
		ic.addInstruction(new MTempUnlock(t1));
		ic.addInstruction(new MTempUnlock(t2));
		ic.addInstruction(new MTempUnlock(t3));
		ic.addInstruction(new IReturnPop());
		return ic;
	}

}
