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

package com.asmtobfc.instructions.meta;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IGoto;

public class IReturnPop extends IConcreteInstruction {

	public IReturnPop() { }
	
	public IInstruction decompose(CompilerStatus cs) {
		int v = cs.popCurrFromReturnStack();
		return new IGoto(new ItrAddress(v, "return-pop"));
	}

	public boolean canDecompose() {
		return true;
	}
}
