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

package com.asmtobfc.asminterpreter;

public class AsmIPrintStmnt extends AsmIInstruction {

	String text;
	
	public AsmIPrintStmnt(String dbgText) {
		super();
		this.text = dbgText;
	}
	
	@Override
	String getInstructionName() {
		return "PrintStmnt";
	}

	@Override
	InstructionOp getInstructionOp() {
		return InstructionOp.PRINTSTMNT;
	}

	@Override
	public String toString() {
		return getInstructionName() + " " + text;
	}
	
	public String getDbgText() {
		return text;
	}
}
