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
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IDAdd;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;
import com.asmtobfc.instructions.meta.MTempLock;
import com.asmtobfc.instructions.meta.MTempUnlock;
import com.asmtobfc.utility.BFCUtil;

public class MPrint extends IConcreteInstruction {
	AsmMemoryAddress dest = null;
	
	public MPrint(AsmMemoryAddress dest) {
		this.dest = dest;
	}
	
	public boolean canDecompose() {
		return true;
	}

	// TODO: LOWER - This method works but is really inefficient.
	
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		AsmTempMemoryAddress destCopy = cs.newAsmMemoryTemp(dest); ic.addInstruction(new MTempLock(destCopy));
		
		AsmTempMemoryAddress zeroconst = cs.newAsmMemoryTemp(dest); ic.addInstruction(new MTempLock(zeroconst));
		AsmTempMemoryAddress ma = cs.newAsmMemoryTemp(dest); ic.addInstruction(new MTempLock(ma));
		ItrTempAddress fe = cs.newTemp(dest.getSign()); ic.addInstruction(new ITempLock(fe));

		
		ic.addInstruction(new MCopy(dest, destCopy));
		
		// If the number is positive...
		
			ICollection icPositive = new ICollection();
			
			icPositive.addInstruction(new MCopy(destCopy, ma));
			icPositive.addInstruction(new MSet(zeroconst, BFCUtil.ZERO_CONSTANT+40000)); // TODO: This makes certain assumptions.
			icPositive.addInstruction(new MSub(ma, zeroconst, ma));
			
		// If the number is negative...
			
			ICollection icNegative = new ICollection();
			icNegative.addInstruction(new MSet(zeroconst, BFCUtil.ZERO_CONSTANT));
			icNegative.addInstruction(new MSub(zeroconst, destCopy, ma));
			
			icNegative.addInstruction(new MSet(zeroconst, BFCUtil.ZERO_CONSTANT+40000)); // TODO: This makes certain assumptions.

			icNegative.addInstruction(new MSub(ma, zeroconst, ma));
			
			icNegative.addInstruction(new ISet(fe, '-'));
			icNegative.addInstruction(new IGoto(fe));
			icNegative.addInstruction(new IBF("."));
		
		
		ic.addInstruction(new MSet(zeroconst, BFCUtil.ZERO_CONSTANT));
		ic.addInstruction(new MGt(destCopy, zeroconst, icPositive));
		
		ic.addInstruction(new MSet(zeroconst, BFCUtil.ZERO_CONSTANT));
		ic.addInstruction(new MLt(destCopy, zeroconst, icNegative));
		
		
		
		for(int x = 5; x >= 0; x--) {
			ic.addInstruction(new ISet(fe, 48));
			ic.addInstruction(new IDAdd(ma.getField(x), fe));
			ic.addInstruction(new IGoto(ma.getField(x)));
			ic.addInstruction(new IBF("."));
		}

		// print \n
		ic.addInstruction(new ISet(fe, 10));
		ic.addInstruction(new IBF("."));

		ic.addInstruction(new ITempUnlock(fe));
		ic.addInstruction(new MTempUnlock(ma));	
		ic.addInstruction(new MTempUnlock(zeroconst));
		ic.addInstruction(new MTempUnlock(destCopy));
		ic.addInstruction(new IReturnPop());
		

		return ic;
	}

}
