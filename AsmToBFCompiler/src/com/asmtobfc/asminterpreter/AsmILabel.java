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

package com.asmtobfc.asminterpreter;

public class AsmILabel {
	private String name;
	private AsmIInstruction instruction;
	private AsmIConstant constant;
	private boolean resolved;
	
	public AsmILabel(String name) {
		resolved = false;
		this.name = name;
	}
	
	public AsmILabel(String name, AsmIInstruction instruction) {
		this.name = name;
		this.instruction = instruction;
		resolved = true;
	}

	public AsmILabel(String name, AsmIConstant constant) {
		this.constant = constant;
		this.name = name;
		resolved = true;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AsmIInstruction getInstruction() {
		return instruction;
	}

	public void setInstruction(AsmIInstruction instruction) {
		this.instruction = instruction;
	}

	public AsmIConstant getConstant() {
		return constant;
	}

	public void setConstant(AsmIConstant constant) {
		this.constant = constant;
	}
	
	
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	public boolean isResolved() {
		return resolved;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		str += name;
		
		if(!resolved || (constant == null && instruction == null)) {
			name += "*";
		}
		
		return name;
	}
}
