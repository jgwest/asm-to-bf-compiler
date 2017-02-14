/*
	Copyright 2011 Jonathan West

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
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.utility.BFCUtil;

public class MSet extends IConcreteInstruction {
	int value = -99999;
	AsmMemoryAddress dest = null;
	
	/**
	 * 
	 * @param dest 
	 * @param value The value parameter is directly applied to the memory address, without any additional translation.
	 */
	public MSet(AsmMemoryAddress dest, int value) {
		this.value = value;
		this.dest = dest;
	}
	
	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		int v = value;
		
		if(v < 0) {
			ic.addInstruction(new ISet(dest.getSign(), 1));
			v *= -1;
		} else {
			ic.addInstruction(new ISet(dest.getSign(), 0));
		}
		
		int[] s = BFCUtil.split(v);
		 
		for(int x = 0; x < s.length; x++) {
			ic.addInstruction(new ISet(dest.getField(x), s[x]));
		}
		
		ic.addInstruction(new IReturnPop());
		return ic;
	}
	
	public String toString() {
		return "[ MSet " + dest.getDebugLabel() + " <-- " + value +" ]";
	}

}
