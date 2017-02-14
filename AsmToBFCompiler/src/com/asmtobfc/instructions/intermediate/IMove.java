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

package com.asmtobfc.instructions.intermediate;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;

public class IMove extends IConcreteInstruction {
	ItrAddress src = null;
	ItrAddress dest = null;
	boolean clearDestBeforeCopy = true;
	
	public IMove(ItrAddress src, ItrAddress dest, boolean clearDestBeforeCopy) {
		this.src = src;
		this.dest = dest;
		this.clearDestBeforeCopy = clearDestBeforeCopy;
	}

	
	public IMove(ItrAddress src, ItrAddress dest) {
		this.src = src;
		this.dest = dest;
		this.clearDestBeforeCopy = true;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		IntermediateFactory f = IntermediateFactory.getInstance();
		
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());

		if(clearDestBeforeCopy) {
			ic.addInstruction(new IClear(dest));
		}
		
		
		ic.addInstruction(f.createInstruction(new IGoto(src)));
		ic.addInstruction(f.createInstruction(new IBF("[")));
			ic.addInstruction(f.createInstruction(new IGoto(dest)));
			ic.addInstruction(f.createInstruction(new IBF("+")));
			ic.addInstruction(f.createInstruction(new IGoto(src)));
			ic.addInstruction(f.createInstruction(new IBF("-")));
		ic.addInstruction(f.createInstruction(new IBF("]")));
		
		ic.addInstruction(new IReturnPop());
		
		
		return ic;
	}

	public boolean canDecompose() {
		return true;
	}
	
	public String toString() {
		return "[ICopy "+src+" "+dest+"]";
	}

}
