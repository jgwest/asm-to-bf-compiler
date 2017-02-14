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

package com.asmtobfc.asminterpreter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AsmScriptInterpreter {

	public static final int SIZE_ADDR = 2048;
	public static final int NUM_START_ADDR = 256;
	public static final int RANDOM_NUMBER_RANGE = 20; // 0 - x
	public static final int NUM_NUMBERS = 100;
	public static final int MEMORY_SIZE = 10000;
	
	
	static Map<String, AsmILabel> labelMap = null;
	static List<AsmIInstruction> program = null; 
	
	// Memory State
	static AsmIRegister[] registers;
	static int[] memory;

	private static void initializeNumbersToSortInMemoryQS(int size) {
		Random r = new Random();

		memory[SIZE_ADDR] = size;
		for(int x = NUM_START_ADDR; x < NUM_START_ADDR+size; x++) {
			int nextVal =Math.abs(r.nextInt() % RANDOM_NUMBER_RANGE); 
			memory[x] = nextVal;
		}
	}
	
	private static void printMemory() {
//		
//		for(int x = 0; x < registers.length; x++) {
//			
//			System.out.println(registers[x].name+" - " + registers[x].value);
//		}
		
		for(int x = 0; x < memory.length; x++) {
			System.out.println(x+": ["+memory[x]+"]");
		}
	}
	
	public static void printProgram(Map<Integer, AsmIRegister> registerMap, List<AsmIInstruction> instructions) {
		
		for(int x = 0; x < instructions.size(); x++) {
			
			AsmIInstruction ai = instructions.get(x);
			String str = "";
			AsmILabel l = ai.getLabelForLine();
			if(l != null) {
				str += "\nlabel " + l.getName()+"\n";
			}
			str += "\t"+ai;
			System.out.println(str);
		}		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			AsmIParser p;
			p = new AsmIParser("C:\\Coding-Projects\\BF-Compiler\\workspace\\AsmToBFCompiler\\assembly\\bf-interpreter.asm");
			//p = new AsmIParser("C:\\h3.quicksort");
			p.parseFile();
			
			labelMap = p.getLabelMap();
			program = p.getProgramResult();
			Map<Integer, AsmIRegister> registerMap = p.getRegisterMap();
			
			// Copy the registers from the map to an array
			registers = new AsmIRegister[registerMap.size()];
			
			for(int x = 0; x < registerMap.size(); x++) {
				AsmIRegister r = registerMap.get(new Integer(x));
				registers[x] = r;
			}

			// Init registers
			for(int x = 0; x < registers.length; x++) {
				registers[x].setValue(0);
				registers[x].setInitialized(true);
			}

			// Initialize memory and registers
			memory = new int[MEMORY_SIZE];
			
			// Init memory to 0
			for(int x = 0; x < memory.length; x++) {
				memory[x] = 0;
			}
			
			
			String prg1 = "->++>+++>+>+>++>>+>+>+++>>+>+>++>+++>+++>+>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+>+>++>>>+++>>>>>+++>+>>>>>>>>>>>>>>>>>>>>>>+++>>>>>>>++>+++>+++>+>>+++>>>+++>+>+++>+>++>+++>>>+>+>+>+>++>+++>+>+>>+++>>>>>>>+>+>>>+>+>++>+++>+++>+>>+++>+++>+>+++>+>++>+++>++>>+>+>++>+++>+>+>>+++>>>+++>+>>>++>+++>+++>+>>+++>>>+++>+>+++>+>>+++>>+++>>+[[>>+[>]+>+[<]<-]>>[>]<+<+++[<]<<+]>>+[>]+++[++++++++++>++[-<++++++++++++++++>]<.<-<]";

			String prg2 = ">+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.>>>++++++++[<++++>-]<.>>>++++++++++[<+++++++++>-]<---.<<<<.+++.------.--------.>>+.";
			
			int c = 0;
			for(int x = 0; x < prg2.length(); x++) {
				memory[c] = prg2.charAt(x);
				c++;
			}
			

			
//			initializeNumbersToSortInMemoryQS(NUM_NUMBERS);
						
			runProgram();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	private static void runProgram() {
		
		boolean continueRunning = true;
		int pc = 0;
		
		while(continueRunning) {
			AsmIInstruction inst = program.get(pc);
			boolean branched = false;
			
			switch(inst.getInstructionOp()) {
			case ADD:
				AsmIAdd ia = (AsmIAdd) inst;
				
				int sum = ia.getRegisterB().getValue() + ia.getRegisterC().getValue();
				ia.getRegisterA().setValue(sum);
				
				break;
				
			case CONSTANT:
				AsmIConstant ic = (AsmIConstant) inst;
				System.err.println("Cannot execute a constant. Program terminating.");
				continueRunning = false;
				break;
				
			case DEBUGPRINTREG:
				AsmIDebugPrintReg pr = (AsmIDebugPrintReg)inst;
				
				System.out.println(pr.getRegisterA().debugGetAllDefineNames()+" "+ pr.getRegisterA().getValue());
				break;

			case PRINTREGVALA:
				AsmIPrintRegValA prvala = (AsmIPrintRegValA)inst;
				
				System.out.print((char)(prvala.getRegisterA().getValue()));
				break;

			case PRINTREGVAL:
				AsmIPrintRegVal pregval = (AsmIPrintRegVal)inst;
				
				System.out.println("["+pregval.getRegisterA().getValue()+"]");
				break;

				
				
			case PRINTSTMNT:
				AsmIPrintStmnt ds = (AsmIPrintStmnt)inst;
				System.out.print(ds.getDbgText());
				break;
				
			case HALT:
				AsmIHalt ih = (AsmIHalt) inst;
				System.out.println("Normal system termination");
				continueRunning = false;
				break;
				
			case JUMPLTE:
				AsmIJumpLte ijl = (AsmIJumpLte) inst;
				if(ijl.getRegisterA().getValue() <= ijl.getRegisterB().getValue()) {
					AsmIInstruction targetInstruction = ijl.getLabel().getInstruction();
					
					if(targetInstruction != null) {
						pc = targetInstruction.getProgramPosition();
						branched = true;
					} else {
						System.err.println("Invalid target instruction");
						continueRunning = false;
					}
				}
				break;

			case JUMPGTE:
				AsmIJumpGte igte = (AsmIJumpGte) inst;
				if(igte.getRegisterA().getValue() >= igte.getRegisterB().getValue()) {
					AsmIInstruction targetInstruction = igte.getLabel().getInstruction();
					
					if(targetInstruction != null) {
						pc = targetInstruction.getProgramPosition();
						branched = true;
					} else {
						System.err.println("Invalid target instruction");
						continueRunning = false;
					}
				}
				break;

			case GOTO:
				AsmIGoto ig = (AsmIGoto) inst;
				AsmIInstruction gotoTargetInstruction = ig.getLabel().getInstruction();
				
				if(gotoTargetInstruction != null) {
					pc = gotoTargetInstruction.getProgramPosition();
					branched = true;
				} else {
					System.err.println("Invalid target instruction");
					continueRunning = false;
				}
				break;


				
			case JUMPEQ:
				AsmIJumpEq ieq = (AsmIJumpEq) inst;
				if(ieq.getRegisterA().getValue() == ieq.getRegisterB().getValue()) {
					AsmIInstruction targetInstruction = ieq.getLabel().getInstruction();
					
					if(targetInstruction != null) {
						pc = targetInstruction.getProgramPosition();
						branched = true;
					} else {
						System.err.println("Invalid target instruction");
						continueRunning = false;
					}
				}
				break;

				
			case LOAD:
				AsmILoad il = (AsmILoad) inst;
				AsmIConstant c = il.getLabel().getConstant();
				if(c != null) {
					il.getRegisterA().setValue(c.getValue());
				} else {
					System.out.println(il.getProgramPosition());
					System.out.println("Load instruction had no corresponding constant");
					continueRunning = false;
				}
				
				break;
				
			case LOADI:
				AsmILoadI ili = (AsmILoadI) inst;
				int mval = memory[ili.getRegisterB().getValue()];
				ili.getRegisterA().setValue(mval);
				break;
				
			case MULT:
				AsmIMult im = (AsmIMult) inst;
				
				int product = im.getRegisterB().getValue() + im.getRegisterC().getValue();
				im.getRegisterA().setValue(product);
				
				break;
				
			case STORE:
				AsmIStore is = (AsmIStore) inst;
				// TODO: Asm Interpreter - Remove Store?
				System.out.println("Unsupported instruction");
				continueRunning = false;
				break;
				
			case STOREI:
				AsmIStoreI isi = (AsmIStoreI) inst;
				memory[isi.getRegisterA().getValue()] = isi.getRegisterB().getValue();
				break;
				
			case SUB:
				AsmISub isu = (AsmISub)inst;

				int sub = isu.getRegisterB().getValue() - isu.getRegisterC().getValue();
				isu.getRegisterA().setValue(sub);
				
				break;
			}
			
			// If the previous instruction was not a branch (meaning the pc has already changed), then increment the pc
			if(!branched) {
				pc++;
			}
		}
		
//		printMemory();
	}

}
