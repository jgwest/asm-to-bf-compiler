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

package com.asmtobfc.instructions.asm;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.ICopy;
import com.asmtobfc.instructions.intermediate.IDGtle;
import com.asmtobfc.instructions.intermediate.IEqual;
import com.asmtobfc.instructions.intermediate.IGT;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class IGTELT extends IConcreteInstruction {
	private ItrAddress left = null;
	private ItrAddress right = null;
	private IInstruction gte = null;
	private IInstruction lt = null;
		
	public IGTELT(ItrAddress left, ItrAddress right, IInstruction gte, IInstruction lt) {
		super();
		this.left = left;
		this.right = right;
		this.gte = gte;
		this.lt = lt;
	}

	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());
		
		ItrTempAddress t1 = cs.newTemp(left); ic.addInstruction(new ITempLock(t1));
		ItrTempAddress t2 = cs.newTemp(right); ic.addInstruction(new ITempLock(t2));
		ItrTempAddress t3 = cs.newTemp(left); ic.addInstruction(new ITempLock(t3));
		ItrTempAddress t4 = cs.newTemp(left); ic.addInstruction(new ITempLock(t4));
		

		ic.addInstruction(new ICopy(left, t1));
		ic.addInstruction(new ICopy(right, t2));
		
		ic.addInstruction(new IDGtle(t1, t2, t3));
//		ic.addInstruction(new MPrintString("IGTELT:{"));
//		ic.addInstruction(new IPrint(t3, true));
//		ic.addInstruction(new MPrintString("}"));
		ic.addInstruction(new ICopy(t3, t2)); // Copy t3 to t2 (we are finished with t2, so will reuse it here);

		// If gtle == A_LESS_THEN_B_ then do lt
		ic.addInstruction(new ISet(t4, IDGtle.A_LESSTHAN_B));
		ic.addInstruction(new IEqual(t3, t4, lt));
		
		// If gtle > A_LESS_THEN_B then do gte
//		ic.addInstruction(new ISet(t4, IDGtle.A_LESSTHAN_B));
//		ic.addInstruction(new IGT(t2, t4, gte));
		
		ic.addInstruction(new ISet(t4, IDGtle.A_LESSTHAN_B));
		ic.addInstruction(new IGT(t2, t4, gte));
		
		ic.addInstruction(new ITempUnlock(t1)); 
		ic.addInstruction(new ITempUnlock(t2));
		ic.addInstruction(new ITempUnlock(t3)); 
		ic.addInstruction(new ITempUnlock(t4));
		
		ic.addInstruction(new IReturnPop());
		
		return ic;
	}

}
