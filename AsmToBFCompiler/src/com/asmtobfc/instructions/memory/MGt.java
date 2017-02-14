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
import com.asmtobfc.instructions.intermediate.IEqual;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPeek;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class MGt extends IConcreteInstruction {
	AsmMemoryAddress left = null;
	AsmMemoryAddress right = null;
	ItrAddress result = null;
	IInstruction inner = null;
	
	public MGt(AsmMemoryAddress left, AsmMemoryAddress right, IInstruction inner) {
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
		ItrTempAddress two = cs.newTemp(left.getSign()); ic.addInstruction(new ITempLock(two));
		
		ic.addInstruction(new ISet(two, 2));
		ic.addInstruction(new MGtle(left, right, t));
		
		ic.addInstruction(new IReturnPeek());
		ic.addInstruction(new IEqual(t, two, inner));

		ic.addInstruction(new ITempUnlock(two));
		ic.addInstruction(new ITempUnlock(t));
		ic.addInstruction(new IReturnPop());
		return ic;
	}

}
