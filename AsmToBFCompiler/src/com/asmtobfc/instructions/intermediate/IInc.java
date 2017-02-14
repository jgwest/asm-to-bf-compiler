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
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;

public class IInc extends IConcreteInstruction {
	
	private ItrAddress destination = null;
	
	public IInc(ItrAddress destination) {
		this.destination = destination;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		IntermediateFactory f = IntermediateFactory.getInstance();
		ICollection ic = new ICollection();
		
		ic.addInstruction(f.createInstruction(new IGoto(destination)));
		ic.addInstruction(f.createInstruction(new IBF("+")));
		
		return ic;
	}
	
	public boolean canDecompose() {
		return true;
	}

	public String toString() {
		return "[IInc "+destination+"]";
	}

	
}
