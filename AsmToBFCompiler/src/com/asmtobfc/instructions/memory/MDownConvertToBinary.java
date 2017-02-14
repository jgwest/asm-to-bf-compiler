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

package com.asmtobfc.instructions.memory;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.AsmTempMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.MTempLock;
import com.asmtobfc.instructions.meta.MTempUnlock;
import com.asmtobfc.utility.BFCUtil;


/** Converts a Big Memory instruction to a BF cell */
public class MDownConvertToBinary extends IConcreteInstruction {
	private AsmMemoryAddress src = null;
	private ItrAddress[] dest = null; // First byte of array is LSB
	
	/**
	 * 
	 * @param src The Big Memory address to convert
	 * @param dest The BF cells to store the resulting value in 
	 */
	public MDownConvertToBinary(AsmMemoryAddress src, ItrAddress[] dest) {
		super();
		this.src = src;
		this.dest = dest;
	}

	public boolean canDecompose() {
		return true;
	}

	/** This only works with numbers less than 256. */
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		
		ic.addInstruction(new IReturnPush());
		
		AsmTempMemoryAddress t = cs.newAsmMemoryTemp(src); ic.addInstruction(new MTempLock(t));
		AsmTempMemoryAddress t2 = cs.newAsmMemoryTemp(src); ic.addInstruction(new MTempLock(t2));
		AsmTempMemoryAddress ZERO = cs.newAsmMemoryTemp(src); ic.addInstruction(new MTempLock(ZERO));
		
		
		AsmTempMemoryAddress subValue = cs.newAsmMemoryTemp(src); ic.addInstruction(new MTempLock(subValue));
		
		// Initial setup ----
		for(int x = 0; x < dest.length; x++) {
			ic.addInstruction(new ISet(dest[x], 0));
		}
		ic.addInstruction(new MSet(ZERO, BFCUtil.convInt(0)));
		
		ic.addInstruction(new MCopy(src, t));
		
		// Start ---
		
		int start = 8192;
		do {
			
			int destPos = (int)(StrictMath.log(start)/StrictMath.log(2));

			ic.addInstruction(new MSet(subValue, BFCUtil.convInt(start)));
			ic.addInstruction(new MSub(t, subValue, t2));
			
			ICollection gte = new ICollection();
			ic.addInstruction(new MGte(t2, ZERO, gte));
				gte.addInstruction(new ISet(dest[destPos], 1));
				gte.addInstruction(new MCopy(t2, t));
			
			start /= 2;
			
		} while(start >= 1);
					
		
		
		ic.addInstruction(new MTempUnlock(subValue));
		ic.addInstruction(new MTempUnlock(ZERO));
		ic.addInstruction(new MTempUnlock(t2));
		ic.addInstruction(new MTempUnlock(t));
		
		
		ic.addInstruction(new IReturnPop()); // TODO: EASY - What's the use of this?
		return ic;
	}

}
