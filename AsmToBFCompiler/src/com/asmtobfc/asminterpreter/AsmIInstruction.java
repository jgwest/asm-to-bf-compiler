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

public abstract class AsmIInstruction {
	
	enum InstructionOp { ADD, CONSTANT, HALT, JUMPLTE, JUMPGTE, JUMPEQ, LOAD, LOADI, MULT, STORE, STOREI, SUB, GOTO, DEBUGPRINTREG, PRINTSTMNT, PRINTREGVALA, PRINTREGVAL };
	
	AsmILabel labelForLine;
	int programPosition = -1;

	public AsmILabel getLabelForLine() {
		return labelForLine;
	}
	
	public void setLabelForLine(AsmILabel labelForLine) {
		this.labelForLine = labelForLine;
	}
	
	public int getProgramPosition() {
		return programPosition;
	}
	
	public void setProgramPosition(int programPosition) {
		this.programPosition = programPosition;
	}
	
	abstract String getInstructionName();
	
	abstract InstructionOp getInstructionOp();
}
