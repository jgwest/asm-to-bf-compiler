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

package com.asmtobfc.instructions.meta;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrTempAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IClear;

public class ITempUnlock extends IConcreteInstruction {
	private int uniqueId = -1; 
	
	public ITempUnlock(ItrTempAddress l) {
		this.uniqueId = l.getUniqueId();
	}
	
	public boolean canDecompose() {
		return true;
	}

	/** This method decomposes a clear on the temp label before releasing the temp label. */
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
 
		ItrTempAddress itl = new ItrTempAddress(uniqueId, cs, null);
		ic.addInstruction(new IClear(itl));
		
		IInstruction ii = ic;

		while(ii.canDecompose()) {
			ii = ii.decompose(cs);
		}
		
		cs.unlockTemp(uniqueId);
		
		return ii;
	}

}
