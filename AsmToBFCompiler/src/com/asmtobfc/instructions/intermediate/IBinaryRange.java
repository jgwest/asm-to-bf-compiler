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
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.meta.IReturnPop;
import com.asmtobfc.instructions.meta.IReturnPush;

public class IBinaryRange extends IConcreteInstruction {
	
	ItrAddress[] dest; // First address is LSB 
	ICollection[] collections;
	
	public IBinaryRange(ItrAddress[] dest, ICollection[] collections) {
		this.dest = dest;
		this.collections = collections;
	}
	
	public IInstruction decompose(CompilerStatus cs) {
		ICollection ic = new ICollection();
		
		
		ic.addInstruction(new IReturnPush());
		
		ic.addInstruction(new IBF("IBinaryRange{"));
		ic.addInstruction(recurse(0, ""));
		ic.addInstruction(new IBF("}"));
		
		ic.addInstruction(new IReturnPop()); // TODO: EASY - Needed?
		
		
		
		return ic;
	}
	
	public ICollection recurse(int currPos, String past) {
		ICollection result = new ICollection();
				
		if(currPos == dest.length) {
			
			int collPos = Integer.parseInt(past, 2);
						
			// We've reached the end!
			
			
			if(collPos < collections.length) {
				result.addInstruction(new IBF("IBinaryRange-Inner-"+collPos+"{"));
				result.addInstruction(collections[collPos]);
				result.addInstruction(new IBF("}"));
			} else {
				result.addInstruction(new IBF(""));
			}
			
			return result;

		}
		
		ICollection isOne = recurse(currPos+1, "1"+past );
		ICollection isZero = recurse(currPos+1, "0"+past);
		
		result.addInstruction(new IBitCheckIfElse(dest[currPos], isOne, isZero));

		return result;
	}

	
	public boolean canDecompose() {
		return true;
	}

}
