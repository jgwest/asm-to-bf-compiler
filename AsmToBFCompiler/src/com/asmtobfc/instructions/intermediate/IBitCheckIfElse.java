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
/** 
 * 
 *
 * Implementation Idea:
 *		if(is 0) {
 *		  (code for is 0) 
 *		} else {
 *		  (code for is 1)
 *		}
 *		
 *		
 *		set temp1 = 1
 *		copy of value under test
 *		goto copy
 *		[
 *		// is 1 code here
 *		set temp1 to 0
 *		set copy to 0
 *		]
 *		goto temp1
 *		[
 *		// is 0 code here
 *		set temp1 ti 0
 *		]
 *
 */
public class IBitCheckIfElse extends IConcreteInstruction {
	
	private ItrAddress valueUnderTest = null;
	private ICollection valueIsOne = null;
	private ICollection valueIsZero = null;
	
	public IBitCheckIfElse(ItrAddress valueUnderTest, ICollection valueIsOne, ICollection valueIsZero) {
		this.valueUnderTest = valueUnderTest;
		this.valueIsOne = valueIsOne;
		this.valueIsZero = valueIsZero;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		
		IntermediateFactory f = IntermediateFactory.getInstance();
		ICollection ic = new ICollection();
		
		ic.addInstruction(new IReturnPush());
		ItrTempAddress valueUnderTestCopy = cs.newTemp(valueUnderTest); ic.addInstruction(new ITempLock(valueUnderTestCopy));
		ItrTempAddress t1 = cs.newTemp(valueUnderTest); ic.addInstruction(new ITempLock(t1));

		ic.addInstruction(new ICopy(valueUnderTest, valueUnderTestCopy));
		
		ic.addInstruction(f.createInstruction(new ISet(t1, 1)));

		
		ic.addInstruction(f.createInstruction(new IGoto(valueUnderTestCopy)));
		ic.addInstruction(f.createInstruction(new IBF("[")));
			ic.addInstruction(valueIsOne);
			ic.addInstruction(f.createInstruction(new ISet(t1, 0)));
			ic.addInstruction(f.createInstruction(new ISet(valueUnderTestCopy, 0)));
			ic.addInstruction(f.createInstruction(new IGoto(valueUnderTestCopy)));
		ic.addInstruction(f.createInstruction(new IBF("]")));
		ic.addInstruction(f.createInstruction(new IGoto(t1)));
		
		ic.addInstruction(f.createInstruction(new IBF("[")));
			ic.addInstruction(valueIsZero);
			ic.addInstruction(f.createInstruction(new ISet(t1, 0)));
			ic.addInstruction(f.createInstruction(new IGoto(t1)));
		ic.addInstruction(f.createInstruction(new IBF("]")));

		ic.addInstruction(new ITempUnlock(t1));
		ic.addInstruction(new ITempUnlock(valueUnderTestCopy));

		ic.addInstruction(new IReturnPop());

		
		return ic;
	}
	
	public boolean canDecompose() {
		return true;
	}

	public String toString() {
		return "[IBitCheckIfElse (...) ]";
	}

	
}
