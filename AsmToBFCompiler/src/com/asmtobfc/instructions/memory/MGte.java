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
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IEqual;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPeek;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

/** Greater than or equal to*/
public class MGte extends IConcreteInstruction {
	AsmMemoryAddress left = null;
	AsmMemoryAddress right = null;
	ItrAddress result = null;
	IInstruction inner = null;
	
	public MGte(AsmMemoryAddress left, AsmMemoryAddress right, IInstruction inner) {
		this.left = left;
		this.right = right;
		this.inner = inner;
	}

	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ItrTempAddress t = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(t));
		ItrTempAddress GT = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(GT));
		ItrTempAddress EQ = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(EQ));
		ItrTempAddress ONE = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(ONE));
		
		ItrTempAddress tempResult = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(tempResult));
		
		
		// Set Constants
		ic.addInstruction(new ISet(tempResult, 0));
		ic.addInstruction(new ISet(ONE, 1));
		ic.addInstruction(new ISet(GT, MGtle.A_GREATERTHAN_B));
		ic.addInstruction(new ISet(EQ, MGtle.A_EQUALS_B));
		
		
		ic.addInstruction(new MGtle(left, right, t));
		
		ICollection innerSetResult = new ICollection();
			innerSetResult.addInstruction(new ISet(tempResult, 1));

		ICollection innerSetResult2 = new ICollection();
			innerSetResult2.addInstruction(new ISet(tempResult, 1));
		
		
		ic.addInstruction(new IReturnPeek());
		ic.addInstruction(new IEqual(t, GT, innerSetResult));
		ic.addInstruction(new IEqual(t, EQ, innerSetResult2));		
		
		
		ic.addInstruction(new IEqual(tempResult, ONE, inner));

		ic.addInstruction(new ITempUnlock(ONE));
		ic.addInstruction(new ITempUnlock(EQ));
		ic.addInstruction(new ITempUnlock(GT));
		ic.addInstruction(new ITempUnlock(t));
		ic.addInstruction(new IReturnPop());
		return ic;
	}

}
