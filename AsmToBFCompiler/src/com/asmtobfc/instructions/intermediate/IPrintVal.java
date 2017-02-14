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
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class IPrintVal extends IConcreteInstruction {
	ItrAddress value = null;
	
	public IPrintVal(ItrAddress value) {
		this.value = value;
	}
	
	public boolean canDecompose() {
		return true;
	}

	
	public IInstruction howMany(final int compValue, ItrTempAddress valCopy, ItrTempAddress DIGIT_VALUE, ItrTempAddress numValues, ItrTempAddress endLoop) {
		ICollection ic = new ICollection();	
		
		ic.addInstruction(new ISet(DIGIT_VALUE, compValue));
		ic.addInstruction(new ISet(numValues, 0));
		ic.addInstruction(new ISet(endLoop, 1));
		ic.addInstruction(new IGoto(endLoop));
		ic.addInstruction(new IBF("[")); // TODO: EASY - ImmediateFactory me
				
			ICollection inner1 = new ICollection();
				inner1.addInstruction(new IInc(numValues));
				inner1.addInstruction(new IDSub(valCopy, DIGIT_VALUE));
				inner1.addInstruction(new ISet(DIGIT_VALUE, compValue));

			ic.addInstruction(new IGT(valCopy, DIGIT_VALUE, inner1));
			
			ICollection inner2 = new ICollection();
				inner2.addInstruction(new IInc(numValues));
				inner2.addInstruction(new IDSub(valCopy, DIGIT_VALUE));
				inner2.addInstruction(new ISet(DIGIT_VALUE, compValue));
				inner2.addInstruction(new ISet(endLoop, 0));
			
			ic.addInstruction(new IEqual(valCopy, DIGIT_VALUE, inner2));
				
			ICollection inner3 = new ICollection();
				inner3.addInstruction(new ISet(endLoop, 0));
		
			ic.addInstruction(new ILT(valCopy, DIGIT_VALUE, inner3));

			ic.addInstruction(new IGoto(endLoop));
			
		ic.addInstruction(new IBF("]")); // TODO: EASY - ImmediateFactory me
		
		ic.addInstruction(new IPrint(numValues, true));

		return ic;
		
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ItrTempAddress valCopy = cs.newTemp(value); ic.addInstruction(new ITempLock(valCopy));
		
		ItrTempAddress DIGIT_VALUE = cs.newTemp(value); ic.addInstruction(new ITempLock(DIGIT_VALUE));
		ItrTempAddress numHunds = cs.newTemp(value); ic.addInstruction(new ITempLock(numHunds));
		
		ItrTempAddress endLoop = cs.newTemp(value); ic.addInstruction(new ITempLock(endLoop));
		
		ic.addInstruction(new ICopy(value, valCopy));
		
		ic.addInstruction(howMany(100, valCopy, DIGIT_VALUE, numHunds, endLoop));

		ic.addInstruction(new ISet(DIGIT_VALUE, 10));
		ic.addInstruction(howMany(10, valCopy, DIGIT_VALUE, numHunds, endLoop));

		ic.addInstruction(new ISet(DIGIT_VALUE, 1));
		ic.addInstruction(howMany(1, valCopy, DIGIT_VALUE, numHunds, endLoop));

		ic.addInstruction(new ITempUnlock(endLoop));
		ic.addInstruction(new ITempUnlock(numHunds));
		ic.addInstruction(new ITempUnlock(DIGIT_VALUE));
		ic.addInstruction(new ITempUnlock(valCopy));
		
		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

}
