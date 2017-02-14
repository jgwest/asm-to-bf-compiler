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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsmIParser {
	Map<String, AsmILabel> labelMap = new HashMap<String, AsmILabel>();
	
	Map<Integer, AsmIRegister> registerMap = new HashMap<Integer, AsmIRegister>();

	List<AsmIInstruction> programResult = null;
	
	File file;
	
	public void createRegisters() {
		
		for(int x = 0; x < 16; x++) {
			AsmIRegister r = new AsmIRegister();
			r.setName("r"+x);
			r.setRegisterNumber(x);
			registerMap.put(x, r);
		}
	}
	
	private AsmIRegister getRegister(String name, HashMap<String /* name*/, Integer /* register #*/> defines) {
		
		if(name.startsWith("r") && name.length() == 2) {
			
			// If matches r0-r9 pattern
			char c = name.charAt(1);
			if(Character.isDigit(c)) {
				return registerMap.get(Integer.parseInt(""+c));
			}
		}

		if(name.startsWith("r") && name.length() == 3) {
			
			// If matches r00-r99 pattern
			String n = name.substring(1,3);
			boolean areDigits = true;
			for(int x = 0; x < 2; x++) {
				char c = n.charAt(x);
				if(!Character.isDigit(c)) {
					areDigits = false;
				}
			}

			if(areDigits) {
				return registerMap.get(Integer.parseInt(""+n));
			}
		}

		
		Integer i = defines.get(name.toLowerCase());
		if(i == null) {
			System.err.println("Unable to resolve define ["+name+"]");
			System.exit(0);
		}
		
		return registerMap.get(i);

	}
	
	public AsmIParser(String fileName) throws IOException {
		file = new File(fileName);
		if(!file.exists()) {
			throw new FileNotFoundException("Unable to find file");
		}
	}
	
	public void parseFile() {
		
		ArrayList<String> a = parseFile(file);
		ArrayList<AsmIInstruction> program = new ArrayList<AsmIInstruction>();

		HashMap<String /* name*/, Integer /* register #*/> defines = new HashMap<String, Integer>();

		createRegisters();
		
		// Whether or not there was a label on the previous line, and what it's name was
		String lastLineLabel = null;
		
		for(int x = 0; x < a.size(); x++) {
			
			String action = a.get(x); x++;
			ArrayList<String> operands = new ArrayList<String>();
			
			// Additional items after the first instruction are parameters, terminated by null
			
			while(a.get(x) != null) {
				operands.add(a.get(x));
				x++;
			}
//			x--; // The x++ of the for will still happen, so we need to be one back to allow it to properly hit the next element
			
			if(action.equalsIgnoreCase("#define")) {
				String defineName = operands.get(0);
				Integer registerNumber = new Integer(operands.get(1).trim().substring(1));
				
				registerMap.get(registerNumber).addDefineName(defineName);
				defines.put(defineName.toLowerCase(), registerNumber);
				continue;
			}
	
			if(action.equalsIgnoreCase("label")) {
				lastLineLabel = operands.get(0);
				continue;
			}
			
			boolean instructionMatched = false;
			
			AsmIInstruction currInstruction = null;

			// Three register operand instructions
			if(action.equalsIgnoreCase("Add") || action.equalsIgnoreCase("Mult") || action.equalsIgnoreCase("Sub")) {
				
				AsmIThreeRegisterInstructions i = null;
				
				AsmIRegister ra = getRegister(operands.get(0), defines);
				AsmIRegister rb = getRegister(operands.get(1), defines);
				AsmIRegister rc = getRegister(operands.get(2), defines);
				
				instructionMatched = true;
				
				if(action.equalsIgnoreCase("Add")) {
					i = new AsmIAdd(ra, rb, rc);
				}

				if(action.equalsIgnoreCase("Mult")) {
					i = new AsmIMult(ra, rb, rc);
				}

				if(action.equalsIgnoreCase("Sub")) {
					i = new AsmISub(ra, rb, rc);
				}
				
				// If any of the operands are defines, find them and set the appropriate field
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				i.setRegBDefine(rb.findDefineName(operands.get(1)));
				i.setRegCDefine(rc.findDefineName(operands.get(2)));
				
				currInstruction = i;
				
			}
			
			// Two register operand instructions
			if(action.equalsIgnoreCase("LoadI") || action.equalsIgnoreCase("StoreI")) {
				AsmITwoRegisterInstructions i = null;
				AsmIRegister ra = getRegister(operands.get(0), defines);
				AsmIRegister rb = getRegister(operands.get(1), defines);
			
				instructionMatched = true;
				
				if(action.equalsIgnoreCase("LoadI")) {
					i = new AsmILoadI(ra, rb);
				}
				
				if(action.equalsIgnoreCase("StoreI")) {
					i = new AsmIStoreI(ra, rb);
				}

				// If any of the operands are defines, find them and set the appropriate field
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				i.setRegBDefine(rb.findDefineName(operands.get(1)));
				
				currInstruction = i;

			}
			
			if(action.equalsIgnoreCase("PrintStmnt")) {
				instructionMatched = true;
				String dbgText = "";
				for(String str : operands) {
					dbgText += str + " ";
				}
				
				dbgText = dbgText.trim();
				if(dbgText.startsWith("\"")) {
					dbgText = dbgText.substring(1);
				}
				
				if(dbgText.endsWith("\"")) {
					dbgText = dbgText.substring(0, dbgText.length()-1);
				}
				
				dbgText = dbgText.replace("\\n", "\n");
				
				currInstruction = new AsmIPrintStmnt(dbgText);
			}
			
			// One register instructions
			if(action.equalsIgnoreCase("DebugPrintReg")) {
				instructionMatched = true;
				
				AsmIRegister ra = getRegister(operands.get(0), defines);
				
				AsmIDebugPrintReg i = new AsmIDebugPrintReg(ra);
				
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				
				currInstruction = i;
			}
			
			if(action.equalsIgnoreCase("PrintRegValA")) {
				instructionMatched = true;
				
				AsmIRegister ra = getRegister(operands.get(0), defines);
				
			    AsmIPrintRegValA i = new AsmIPrintRegValA(ra);
				
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				
				currInstruction = i;
				
			}

			if(action.equalsIgnoreCase("PrintRegVal")) {
				instructionMatched = true;
				
				AsmIRegister ra = getRegister(operands.get(0), defines);
				
			    AsmIPrintRegVal i = new AsmIPrintRegVal(ra);
				
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				
				currInstruction = i;
				
			}

			
			// One register, one label operand instructions
			if(action.equalsIgnoreCase("Goto")) {
				
				instructionMatched = true;
				
				String labelName = operands.get(0);
				
				AsmILabel label = labelMap.get(labelName.toLowerCase());
				
				if(label == null) {
					label = new AsmILabel(labelName);
					label.setResolved(false);
					labelMap.put(labelName.toLowerCase(), label);
				}
				
				AsmIGoto i = new AsmIGoto(label);
				
				currInstruction = i;
				
			}

			
			
			// One register, one label operand instructions
			if(action.equalsIgnoreCase("Store") || action.equalsIgnoreCase("Load")) {
				
				AsmIRegisterLabelInstructions i = null;
				instructionMatched = true;
				
				AsmIRegister ra = getRegister(operands.get(0), defines);
				
				String labelName = operands.get(1);
				
				AsmILabel label = labelMap.get(labelName.toLowerCase());
				
				if(label == null) {
					label = new AsmILabel(labelName);
					label.setResolved(false);
					labelMap.put(labelName.toLowerCase(), label);
				}
				
				if(action.equalsIgnoreCase("Store")) {
					i = new AsmIStore(ra, label);
				}

				if(action.equalsIgnoreCase("Load")) {
					i = new AsmILoad(ra, label);
				}

				// If any of the operands are defines, find them and set the appropriate field
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				
				currInstruction = i;
				
			}
			
			// Two register operands, 1 label, instruction
			if(action.equalsIgnoreCase("JumpLte") || action.equalsIgnoreCase("JumpGte") || action.equalsIgnoreCase("JumpEq")) {
				AsmIRegister ra = getRegister(operands.get(0), defines);
				AsmIRegister rb = getRegister(operands.get(1), defines);

				instructionMatched = true;
				
				String labelName = operands.get(2);
				AsmILabel label = labelMap.get(labelName.toLowerCase());
				
				if(label == null) {
					label = new AsmILabel(labelName);
					label.setResolved(false);
					labelMap.put(labelName.toLowerCase(), label);
				}

				AsmITwoRegisterOneLabelInstructions i = null;
				if(action.equalsIgnoreCase("JumpLte")) {
					i = new AsmIJumpLte(ra, rb, label);
				}
				
				if(action.equalsIgnoreCase("JumpGte")) {
					i = new AsmIJumpGte(ra, rb, label);
				}
				
				if(action.equalsIgnoreCase("JumpEq")) {
					i = new AsmIJumpEq(ra, rb, label);
				}
				
								// If any of the operands are defines, find them and set the appropriate field
				i.setRegADefine(ra.findDefineName(operands.get(0)));
				i.setRegBDefine(rb.findDefineName(operands.get(1)));
				
				currInstruction = i;
			}
			
			if(action.equalsIgnoreCase("Halt")) {
				instructionMatched = true;
								
				currInstruction = new AsmIHalt();
			}

			if(!instructionMatched) {
				try {
					int value = Integer.parseInt(action.trim());
					currInstruction = new AsmIConstant(value);
				} catch(NumberFormatException e) {
					// If we haven't matched the instruction, then the last possibility is 
					// that it's a number; therefore, this NFE is an unrecognized instruction
					System.err.println("Instruction not matched");
					System.exit(-1);
				}
			}
			
			if(lastLineLabel != null) {
				String labelName = lastLineLabel;
				AsmILabel label = labelMap.get(labelName.toLowerCase());
				if(label == null) {
					label = new AsmILabel(labelName);
					label.setResolved(false);
					labelMap.put(labelName.toLowerCase(), label);					
				}
				
				currInstruction.setLabelForLine(label);
				
				if(currInstruction instanceof AsmIConstant) {
					label.setConstant((AsmIConstant)currInstruction);
				} else {
					label.setInstruction(currInstruction);
				}
				label.setResolved(true);
				
				lastLineLabel = null;
			}
			
			program.add(currInstruction);
			
		}
		
		// Update the position field of each instruction to reflect its position in the list
		for(int x = 0; x < program.size(); x++) {
			AsmIInstruction i = program.get(x);
			i.setProgramPosition(x);
		}
		
		programResult = program;
		
	}
	
	/** Returns a list of Strings that represent the program, where each string is a token, and each line is terminated by a null. 
	 * ex: 		LoadI r3 r0 ' r3 = m[x]
	 * becomes [LoadI] [r3] [r0] [null] */
	private static ArrayList<String> parseFile(File f) {
		ArrayList<String> al = new ArrayList<String>();
		
		try {

			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			while(br.ready()) {	
				String str = br.readLine();
				String[] toks = str.split(" ");
				boolean isValid = false;
				for(int x = 0; x < toks.length; x++) {
					String t = toks[x].trim();
					
					// Skip comments
					if(t.startsWith("'") || t.startsWith("//")) {
						break;
					}
					if(t.length() == 0) continue;
					al.add(t);
					
					isValid = true;
				}
				if(isValid) {
					al.add(null);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return al;
	}

	public Map<String, AsmILabel> getLabelMap() {
		return labelMap;
	}
	
	public List<AsmIInstruction> getProgramResult() {
		return programResult;
	}
	
	public Map<Integer, AsmIRegister> getRegisterMap() {
		return registerMap;
	}
}

