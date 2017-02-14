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

package com.asmtobfc.instructions.intermediate;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class IPrint extends IConcreteInstruction {
	ItrAddress value = null;
	private boolean convertToAscii;
	
	public IPrint(ItrAddress value) {
		this.value = value;
		this.convertToAscii = true;
	}
	
	public IPrint(ItrAddress value, boolean convertToAscii) {
		this.value = value;
		this.convertToAscii = convertToAscii;
	}

	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ItrTempAddress t1 = cs.newTemp(value); ic.addInstruction(new ITempLock(t1));
		
		ic.addInstruction(new ICopy(value, t1));
		ic.addInstruction(new IGoto(t1));
		if(convertToAscii) {
			ic.addInstruction(new IBF("+", 48));
		}
		ic.addInstruction(new IBF("."));
				
		ic.addInstruction(new ITempUnlock(t1));

		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

}
