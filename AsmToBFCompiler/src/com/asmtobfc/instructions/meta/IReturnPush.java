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

package com.asmtobfc.instructions.meta;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;

public class IReturnPush extends IConcreteInstruction {

	public IReturnPush() { }
	
	public IInstruction decompose(CompilerStatus cs) {
		cs.pushCurrOntoReturnStack();
		return null;
	}

	public boolean canDecompose() {
		return true;
	}
	// TODO: LOWER - Need to name the various new instructions I've created
}
