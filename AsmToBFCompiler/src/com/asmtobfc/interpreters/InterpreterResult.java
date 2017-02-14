/*
	Copyright 2011 Jonathan West

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

package com.asmtobfc.interpreters;

public class InterpreterResult {
	private int[] memory = null;
	private String result = null;
	

	public int[] getMemory() {
		return memory;
	}
	public void setMemory(int[] memory) {
		this.memory = memory;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public boolean equals(Object o) {
		if((o instanceof InterpreterResult)) {
			InterpreterResult ir = (InterpreterResult)o;
			return compareMemory(this.getMemory(), ir.getMemory()) && this.getResult().equals(ir.getResult());
		} else return false;
	}
	
	public static boolean compareMemory(int[] one, int[] two) {
		for(int x = 0; x < one.length; x++) {
			if(one[x] != two[x]) {
				return false;
			}
		}
		
		return true;
	}

}