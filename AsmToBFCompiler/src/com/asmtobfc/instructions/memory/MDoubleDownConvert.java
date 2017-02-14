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
import com.asmtobfc.compiler.AsmTempMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IClear;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.meta.MTempLock;
import com.asmtobfc.instructions.meta.MTempUnlock;


/** Converts a Big Memory instruction to a BF cell */
public class MDoubleDownConvert extends IConcreteInstruction {
	private AsmMemoryAddress src = null;
	private ItrAddress destMsb = null;
	private ItrAddress destLsb = null;
	
	/**
	 * 
	 * @param src The Big Memory address to convert
	 * @param dest The BF cell to store the resulting value in 
	 */
	public MDoubleDownConvert(AsmMemoryAddress src, ItrAddress destMsb, ItrAddress destLsb) {
		super();
		this.src = src;
		this.destMsb = destMsb;
		this.destLsb = destLsb;
	}

	public boolean canDecompose() {
		return true;
	}

	/** This only works with numbers less than 9999. */
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		
		AsmTempMemoryAddress t = cs.newAsmMemoryTemp(src); ic.addInstruction(new MTempLock(t));
		
		ic.addInstruction(new MCopy(src, t));
		
		ic.addInstruction(new IClear(destMsb));
		
		
		// 10
		ic.addInstruction(new IGoto(t.getField(3)));
		ic.addInstruction(new IBF("[-"));
		ic.addInstruction(new IGoto(destMsb));
		ic.addInstruction(new IBF("+", 10));
		ic.addInstruction(new IGoto(t.getField(3)));		
		ic.addInstruction(new IBF("]"));

		// 1
		ic.addInstruction(new IGoto(t.getField(2)));
		ic.addInstruction(new IBF("[-"));
		ic.addInstruction(new IGoto(destMsb));
		ic.addInstruction(new IBF("+"));
		ic.addInstruction(new IGoto(t.getField(2)));		
		ic.addInstruction(new IBF("]"));
		
		
		ic.addInstruction(new IClear(destLsb));
		
		// 10
		ic.addInstruction(new IGoto(t.getField(1)));
		ic.addInstruction(new IBF("[-"));
		ic.addInstruction(new IGoto(destLsb));
		ic.addInstruction(new IBF("+", 10));
		ic.addInstruction(new IGoto(t.getField(1)));		
		ic.addInstruction(new IBF("]"));

		// 1
		ic.addInstruction(new IGoto(t.getField(0)));
		ic.addInstruction(new IBF("[-"));
		ic.addInstruction(new IGoto(destLsb));
		ic.addInstruction(new IBF("+"));
		ic.addInstruction(new IGoto(t.getField(0)));		
		ic.addInstruction(new IBF("]"));
		
		ic.addInstruction(new MTempUnlock(t));
		return ic;
	}

}
