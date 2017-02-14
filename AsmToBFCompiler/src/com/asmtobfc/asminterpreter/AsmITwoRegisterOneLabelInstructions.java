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

public abstract class AsmITwoRegisterOneLabelInstructions extends AsmIInstruction {
	AsmIRegister registerA;
	AsmIRegister registerB;
	AsmILabel label;
	
	int regADefine = -1;
	int regBDefine = -1;
	
	public AsmITwoRegisterOneLabelInstructions(AsmIRegister registerA, AsmIRegister registerB, AsmILabel label) {
		this.registerA = registerA;
		this.registerB = registerB;
		this.label = label;
	}
	
	public AsmIRegister getRegisterA() {
		return registerA;
	}
	
	public void setRegisterA(AsmIRegister registerA) {
		this.registerA = registerA;
	}
	
	public AsmIRegister getRegisterB() {
		return registerB;
	}
	
	public void setRegisterB(AsmIRegister registerB) {
		this.registerB = registerB;
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
		String regBStr = getRegisterB().getName();
		
		if(regADefine != -1) {
			regAStr = getRegisterA().getDefineName(regADefine);
		}
		if(regBDefine != -1) {
			regBStr = getRegisterB().getDefineName(regBDefine);
		}

		
		return getInstructionName() + " " + regAStr + " " + regBStr + " " + getLabel();
	}

	
	public int getRegADefine() {
		return regADefine;
	}

	public void setRegADefine(int regADefine) {
		this.regADefine = regADefine;
	}

	public int getRegBDefine() {
		return regBDefine;
	}

	public void setRegBDefine(int regBDefine) {
		this.regBDefine = regBDefine;
	}
	
}
