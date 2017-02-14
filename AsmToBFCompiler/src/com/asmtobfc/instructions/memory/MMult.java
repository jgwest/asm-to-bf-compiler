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
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IClear;
import com.asmtobfc.instructions.intermediate.ICopy;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class MMult extends IConcreteInstruction {
	AsmMemoryAddress left = null;
	AsmMemoryAddress right = null;
	AsmMemoryAddress result = null;
	
	
	public MMult(AsmMemoryAddress left, AsmMemoryAddress right, AsmMemoryAddress result) {
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
		
		// TempMemoryAddress t1 = cs.newMemoryTemp(); ic.addInstruction(new MTempLock(t1));
		
		ItrTempAddress tleft = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(tleft));
		ItrTempAddress tright = cs.newTemp(right.getSign()); ic.addInstruction(new ITempLock(tright));
		ItrTempAddress result = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(result));
		ItrTempAddress ten = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(ten));
		
		ic.addInstruction(new MDownConvert(left, tleft));
		ic.addInstruction(new MDownConvert(right, tright));

		ic.addInstruction(new IClear(result));
		ic.addInstruction(new IGoto(tright));
		ic.addInstruction(new IBF("["));
			ic.addInstruction(new IBF("-"));
			ic.addInstruction(new ICopy(tleft, result, false));
			ic.addInstruction(new IGoto(tright));
		ic.addInstruction(new IBF("]"));
		
//		ic.addInstruction(new MCopy(left, t1));
//		ic.addInstruction(new MCopy(right, t2));
		
		// TODO: This instruction is unfinished. (MMult)
		
		
//		ic.addInstruction(new MCopy(t1, result));
		
		ic.addInstruction(new ITempUnlock(ten));
		ic.addInstruction(new ITempUnlock(tleft));
		ic.addInstruction(new ITempUnlock(tright));
		ic.addInstruction(new ITempUnlock(result));
		ic.addInstruction(new IReturnPop());
		return ic;
	}

}
