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
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;

public class IDEqual extends IConcreteInstruction {
	ItrAddress cp = null;
	ItrAddress c = null;
	IInstruction innerCode = null;
	
	public IDEqual(ItrAddress cp, ItrAddress c, IInstruction innerCode) {
		this.cp = cp;
		this.c = c;
		this.innerCode = innerCode;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		IntermediateFactory f = IntermediateFactory.getInstance();
		
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ic.addInstruction(new IDSub(cp, c));
		ic.addInstruction(f.createInstruction(new IGoto(cp)));
		
			ICollection inner = new ICollection();
			inner.addInstruction(innerCode);
			inner.addInstruction(new IInc(cp));
		
		ic.addInstruction(new IJumpNZ(cp,inner));
		
		
		/*
		ic.addInstruction(new IBF("("));
		ic.addInstruction(new IReturnPeek());
		
		ic.addInstruction(innerCode);
		ic.addInstruction(new IInc(cp));
		ic.addInstruction(new IBF(")"));*/
		
		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

	public boolean canDecompose() {
		return true;
	}
	
	public String toString() {
		return "[IDEqual "+cp+ " - "+c+"]";
	}
}
