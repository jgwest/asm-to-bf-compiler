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
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;
import com.asmtobfc.instructions.meta.ITempLock;
import com.asmtobfc.instructions.meta.ITempUnlock;

public class IDGtle extends IConcreteInstruction {
	public static final int A_GREATERTHAN_B = 2;
	public static final int A_LESSTHAN_B = 1;
	public static final int A_EQUALS_B = 3;

	ItrAddress a = null;
	ItrAddress b = null;
	ItrAddress r = null;
	
	public IDGtle(ItrAddress a, ItrAddress b, ItrAddress r) {
		this.a = a;
		this.b = b;
		this.r = r; 
	}
	
	public boolean canDecompose() {
		return true;
	}

//	public IInstruction decompose2(CompilerStatus cs) {
//		ICollection ic = new ICollection();
//		ic.addInstruction(new IReturnPush());
//		
//		ic.addInstruction(new ISet(r, 0));
//		ic.addInstruction(new IBF("("));
//			
//				ICollection innerLoop1 = new ICollection();
//				innerLoop1.addInstruction(new IInc(r));
//			ic.addInstruction(new IEqual(a, cs.getConstZero(), innerLoop1));
//			
//				
//				ICollection innerLoop2 = new ICollection();
//				innerLoop2.addInstruction(new IInc(r));
//				innerLoop2.addInstruction(new IInc(r));
//			ic.addInstruction(new IEqual(b, cs.getConstZero(), innerLoop2));
//
//			ic.addInstruction(new IDec(a));
//			ic.addInstruction(new IDec(b));
//			
//			ic.addInstruction(new IGoto(r));
//
//		ic.addInstruction(new IBF(")"));
//		
//		ic.addInstruction(new IReturnPop());
//
//		return ic;
//	}
	
	
	
	public IInstruction decompose(CompilerStatus cs) {
		IntermediateFactory f = IntermediateFactory.getInstance();
		
		ICollection ic = new ICollection();
		ic.addInstruction(new IReturnPush());

		ItrTempAddress ZERO = cs.newTemp(a); ic.addInstruction(new ITempLock(ZERO));

		
		ic.addInstruction(new ISet(r, 0));
		
		ICollection jnznner = new ICollection();

				ICollection innerLoop1 = new ICollection();
				innerLoop1.addInstruction(new IInc(r));
			jnznner.addInstruction(new IEqual(a, ZERO, innerLoop1));
			
				
				ICollection innerLoop2 = new ICollection();
				innerLoop2.addInstruction(new IInc(r));
				innerLoop2.addInstruction(new IInc(r));
			jnznner.addInstruction(new IEqual(b, ZERO, innerLoop2));

			jnznner.addInstruction(new IDec(a));
			jnznner.addInstruction(new IDec(b));
			
			jnznner.addInstruction(f.createInstruction(new IGoto(r)));
			
			ic.addInstruction(jnznner);
		
		ic.addInstruction(new IJumpNZ(r,jnznner));

		ic.addInstruction(new ITempUnlock(ZERO));
		
		ic.addInstruction(new IReturnPop());

		return ic;
	}

	public String toString() {
		return "[IDGTle a:("+a + ")  b:("+b+")   r:("+r+")  ]";
	}
	
	
}
