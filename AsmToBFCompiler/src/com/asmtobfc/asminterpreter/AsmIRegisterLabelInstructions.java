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

public abstract class AsmIRegisterLabelInstructions extends AsmIInstruction {
	AsmIRegister registerA;
	AsmILabel label;
	
	int regADefine = -1;
	
	public AsmIRegisterLabelInstructions(AsmIRegister registerA, AsmILabel label) {
		this.registerA = registerA;
		this.label = label;
	}
	
	public AsmIRegister getRegisterA() {
		return registerA;
	}
	
	public AsmILabel getLabel() {
		return label;
	}
	
	public void setLabel(AsmILabel label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		String regAStr = getRegisterA().getName();
		
		if(regADefine != -1) {
			regAStr = getRegisterA().getDefineName(regADefine);
		}
		
		return getInstructionName() + " " + regAStr + " " + getLabel();
	}
	
	public int getRegADefine() {
		return regADefine;
	}
	
	public void setRegADefine(int regADefine) {
		this.regADefine = regADefine;
	}
}
