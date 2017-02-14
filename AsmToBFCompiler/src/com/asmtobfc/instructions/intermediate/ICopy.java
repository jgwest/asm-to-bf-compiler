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

public class ICopy extends IConcreteInstruction {
	ItrAddress src = null;
	ItrAddress dest = null;
	boolean clearDestBeforeCopy = true;
	
	public ICopy(ItrAddress src, ItrAddress dest, boolean clearDestBeforeCopy) {
		this.src = src;
		this.dest = dest;
		this.clearDestBeforeCopy = clearDestBeforeCopy;
	}

	
	public ICopy(ItrAddress src, ItrAddress dest) {
		
		this.src = src;
		this.dest = dest;
		this.clearDestBeforeCopy = true;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		IntermediateFactory f = IntermediateFactory.getInstance();
		
		ICollection ic = new ICollection();
		
		
		ic.addInstruction(new IBF("Copy{"));

		
		ic.addInstruction(new IReturnPush());

		ItrTempAddress t1 = cs.newTemp(src); ic.addInstruction(new ITempLock(t1)); 
		
//		ItrAddress t1 = new ItrAddress(SingleTempHelper.getInstance().calculateClosestTo(src.getPosition()));
		
		
		 
//		if(!(cs instanceof TempVariableScanCompilerStatus)) {
//			int diff = Math.abs(src.getPosition()-dest.getPosition());
//			if(diff > 5000) {
////				System.out.println("DEBUG: "+diff + " "+dest.getLabel() + " "+src.getLabel());
//			}
//		}
		
		if(clearDestBeforeCopy) {
			ic.addInstruction(new IClear(dest));
		}
		
		ic.addInstruction(new IClear(t1));
		
		ic.addInstruction(f.createInstruction(new IGoto(src)));
		ic.addInstruction(f.createInstruction(new IBF("[")));
			ic.addInstruction(new IBF("Copy1a{"));
			ic.addInstruction(f.createInstruction(new IGoto(t1)));
			ic.addInstruction(new IBF("}"));
			ic.addInstruction(f.createInstruction(new IBF("+")));
			ic.addInstruction(new IBF("Copy1b{"));
			ic.addInstruction(f.createInstruction(new IGoto(dest)));
			ic.addInstruction(new IBF("}"));
			ic.addInstruction(f.createInstruction(new IBF("+")));
			ic.addInstruction(new IBF("Copy1c{"));
			ic.addInstruction(f.createInstruction(new IGoto(src)));
			ic.addInstruction(new IBF("}"));
			ic.addInstruction(f.createInstruction(new IBF("-")));
		ic.addInstruction(f.createInstruction(new IBF("]")));

		ic.addInstruction(new IBF("Copy1d{"));
		ic.addInstruction(f.createInstruction(new IGoto(t1)));
		ic.addInstruction(f.createInstruction(new IBF("[")));
			ic.addInstruction(f.createInstruction(new IBF("-")));
			ic.addInstruction(f.createInstruction(new IGoto(src)));
			ic.addInstruction(f.createInstruction(new IBF("+")));
			ic.addInstruction(f.createInstruction(new IGoto(t1)));
		ic.addInstruction(f.createInstruction(new IBF("]")));
		
		ic.addInstruction(new IBF("}"));
		
		ic.addInstruction(new ITempUnlock(t1));
		
		ic.addInstruction(new IReturnPop());		

		ic.addInstruction(new IBF("}"));

		
		return ic;
	}

	public boolean canDecompose() {
		return true;
	}
	
	public String toString() {
		return "[ICopy "+src+" "+dest+"]";
	}

}
