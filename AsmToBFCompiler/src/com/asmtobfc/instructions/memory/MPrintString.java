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

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class MPrintString  extends IConcreteInstruction {

	String text;
	
	public MPrintString(String s) {
		text = s;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		ItrTempAddress fe = cs.newTemp(null); ic.addInstruction(new ITempLock(fe));
		
		for(int x = 0; x < text.length(); x++) {
			ic.addInstruction(new ISet(fe, text.charAt(x)));
			ic.addInstruction(new IGoto(fe));
			ic.addInstruction(new IBF("."));
//			ic.addInstruction(new IClear(fe));
		}
		
		ic.addInstruction(new ITempUnlock(fe));
		ic.addInstruction(new IReturnPop());

		return ic;
	}

	public boolean canDecompose() {
		return true;
	}

}
