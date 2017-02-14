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

package com.asmtobfc.compiler;

public class AsmMemoryAddress {
	/** Position in BF memory of the sign bit*/
	private ItrAddress sign = null;
	
	/** Position in BF memory of the contents */
	private ItrAddress[] contents = null;

	/** Label the cell; used for debugging purposes only */
	private String debugLabel = null;

	public AsmMemoryAddress(ItrAddress sign, ItrAddress[] contents) {
		this.sign = sign;
		this.contents = contents;
	}

	/** Get the constituent field at position x*/
	public ItrAddress getField(int x) {
		return contents[x];
	}
	
	/** Return the contents as an array */
	public ItrAddress[] getContents() {
		return contents;
	}

	/** Get the sign bit */
	public ItrAddress getSign() {
		return sign;
	}

	public String getDebugLabel() {
		return debugLabel;
	}
	
	public void setDebugLabel(String debugLabel) {
		this.debugLabel = debugLabel;
	}
	
	public String toString() {
		String result = "";
		result += "(memaddr: "+this.debugLabel+") ";
		
		for(int x = 0; x < contents.length; x++) {
			ItrAddress c = contents[x];
			result += c+" ";
		}
		
		return result;
	}
	
}
