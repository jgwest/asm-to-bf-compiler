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
import com.asmtobfc.instructions.meta.IReturnPeek;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class IJumpNZ extends IConcreteInstruction {
	private IInstruction inner = null;
	private ItrAddress valueUnderTest = null;
	
	private static boolean validBF = true;
	
	public IJumpNZ(ItrAddress valueUnderTest, IInstruction inner) {
		this.valueUnderTest = valueUnderTest;
		this.inner = inner;
	}
	
	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		if(validBF) return decomposeValidBF(cs);
		else return decomposeInvalidBF(cs);
	}

	
	public IInstruction decomposeInvalidBF(CompilerStatus cs) {
		IntermediateFactory f = IntermediateFactory.getInstance();
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ic.addInstruction(f.createInstruction(new IGoto(valueUnderTest)));
		ic.addInstruction(f.createInstruction(new IBF("(")));
		ic.addInstruction(new IReturnPeek());
		
		ic.addInstruction(inner);
		
		ic.addInstruction(f.createInstruction(new IGoto(valueUnderTest)));
		ic.addInstruction(f.createInstruction(new IBF(")")));
		
		ic.addInstruction(new IReturnPop());
		return ic;

	}
	
	public IInstruction decomposeValidBF(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ItrTempAddress t = cs.newTemp(valueUnderTest); ic.addInstruction(new ITempLock(t));
		ItrTempAddress m = cs.newTemp(valueUnderTest); ic.addInstruction(new ITempLock(m));
		
		// Set m
		ic.addInstruction(new ICopy(valueUnderTest, t));
		ic.addInstruction(new ISet(m, 1));
		ic.addInstruction(new IGoto(t));
		ic.addInstruction(new IBF("["));
			ic.addInstruction(new IDec(m));
			ic.addInstruction(new IClear(t));
		ic.addInstruction(new IBF("]"));
		ic.addInstruction(new IGoto(m));
		
		ic.addInstruction(new IBF("["));
		
			// Do m
			ic.addInstruction(new IGoto(m));
			ic.addInstruction(new IBF("["));
				ic.addInstruction(new IReturnPeek());
				ic.addInstruction(inner);
				ic.addInstruction(new IClear(m)); // This is just in case the code decides to change the position itself.
			ic.addInstruction(new IBF("]"));
			
			
			// Set m
			ic.addInstruction(new ICopy(valueUnderTest, t));
			ic.addInstruction(new ISet(m, 1));
			ic.addInstruction(new IGoto(t));
			ic.addInstruction(new IBF("["));
				ic.addInstruction(new IDec(m));
				ic.addInstruction(new IClear(t));
			ic.addInstruction(new IBF("]"));
			ic.addInstruction(new IGoto(m));
		
		ic.addInstruction(new IBF("]"));
		
		
		ic.addInstruction(new ITempUnlock(t));
		ic.addInstruction(new ITempUnlock(m));
		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

}
