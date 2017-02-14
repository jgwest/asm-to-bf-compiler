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
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IPrint;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class MPrintA extends IConcreteInstruction {
	AsmMemoryAddress src = null;
	
	public MPrintA(AsmMemoryAddress src) {
		this.src = src;
	}
	
	public boolean canDecompose() {
		return true;
	}

	
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ItrTempAddress dest = cs.newTemp(src.getSign()); ic.addInstruction(new ITempLock(dest));
		
		ic.addInstruction(new MDownConvert(src, dest));
		
		ic.addInstruction(new IPrint(dest, false));
		
		ic.addInstruction(new ITempUnlock(dest));
		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

}
