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
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.ICopy;
import com.asmtobfc.instructions.intermediate.IDGtle;
import com.asmtobfc.instructions.intermediate.IEqual;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class MGtle extends IConcreteInstruction {
	AsmMemoryAddress left = null;
	AsmMemoryAddress right = null;
	ItrAddress result = null;
	
	public static final int A_LESSTHAN_B = IDGtle.A_LESSTHAN_B;
	public static final int A_GREATERTHAN_B = IDGtle.A_GREATERTHAN_B;
	public static final int A_EQUALS_B = IDGtle.A_EQUALS_B;
	
	public MGtle(AsmMemoryAddress left, AsmMemoryAddress right, ItrAddress result) {
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

		ItrTempAddress a = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(a));
		ItrTempAddress b = cs.newTemp(right.getSign()); ic.addInstruction(new ITempLock(b));
		ItrTempAddress three = cs.newTemp(result); ic.addInstruction(new ITempLock(three));
		
		ic.addInstruction(new ISet(result, 3));
		ic.addInstruction(new ISet(three, 3));
		
		for(int x = 5; x >= 0; x--) {
				ICollection inner = new ICollection();
				inner.addInstruction(new ICopy(left.getField(x),a));
				inner.addInstruction(new ICopy(right.getField(x),b));
				inner.addInstruction(new IDGtle(a, b, result));
			ic.addInstruction(new IEqual(result, three,inner));
			
		}
		
		ic.addInstruction(new ITempUnlock(a));
		ic.addInstruction(new ITempUnlock(b));
		ic.addInstruction(new ITempUnlock(three));
		ic.addInstruction(new IReturnPop());
		return ic;
	}

}
