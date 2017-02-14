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

package com.asmtobfc.instructions.memory;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IMove;

public class MMove extends IConcreteInstruction {
	AsmMemoryAddress src = null;
	AsmMemoryAddress dest = null;
	
	public MMove(AsmMemoryAddress src, AsmMemoryAddress dest) {
		this.src = src;
		this.dest = dest;
	}

	public boolean canDecompose() {
		return true;
	}

	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		
		if(src.getContents().length != dest.getContents().length) throw(new RuntimeException("Sizes of memory addresses differ."));
		
		ic.addInstruction(new IMove(src.getSign(), dest.getSign()));
		
		for(int  x = 0; x< src.getContents().length; x++) {
			ic.addInstruction(new IMove(src.getContents()[x], dest.getContents()[x]));
		}
		
		return ic;
	}

	public String toString() {
		return "[ MMove " + src.getDebugLabel() + " --> " + dest.getDebugLabel() +" ]";
	}

}
