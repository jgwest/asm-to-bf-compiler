/*
	Copyright 2011, 2013 Jonathan West

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
package com.asmtobfc.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.asm.IRange;
import com.asmtobfc.instructions.asm.MGTLTE;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IBinaryRange;
import com.asmtobfc.instructions.intermediate.ICopy;
import com.asmtobfc.instructions.intermediate.IGoto;
import com.asmtobfc.instructions.intermediate.ISet;
import com.asmtobfc.instructions.memory.MAdd;
import com.asmtobfc.instructions.memory.MCopy;
import com.asmtobfc.instructions.memory.MDownConvertToBinary;
import com.asmtobfc.instructions.memory.MMove;
import com.asmtobfc.instructions.memory.MPrint;
import com.asmtobfc.instructions.memory.MPrintA;
import com.asmtobfc.instructions.memory.MPrintString;
import com.asmtobfc.instructions.memory.MSet;
import com.asmtobfc.instructions.memory.MSub;
import com.asmtobfc.utility.BFCLaunchUtil;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.MemoryLayoutUtil;

public class AsmCompiler { 
	
	private HashMap<String/* r(*) _and_ define names */, AsmMemoryAddress /*address of register*/> defines = null;
	
	private HashMap<String /* label constant*/, Integer /* value */> constants = null;
	private HashMap<AsmBlock /* block */, Integer /* id for block */> funcMap = null;
	private AsmMemoryMap map = null;
	private HashMap<String, AsmBlock> blocksMap = null;

	private final int MEMORY_SIZE = 1000; // Refers to the number of memory addresses in memory
	
	/** Converting LoadI instructions requires the ability to prepend an instruction to the start of a following block 
	 * from the LoadI instruction. */
	private HashMap<AsmBlock /* Block to preprend to */, IInstruction /* Instruction to prepend*/> prefixedInstructions = null;
	
	public AsmCompiler() {
		doWork();
	}
	
	public static void main(String[] args) {
		new AsmCompiler();
	}
	
	private void doWork() {
//		AsmParser p = new AsmParser("C:\\Coding-Projects\\BF-Compiler\\workspace\\AsmToBFCompiler\\assembly\\sum.asm");
//		AsmParser p = new AsmParser("C:\\Coding-Projects\\BF-Compiler\\workspace\\AsmToBFCompiler\\assembly\\test.asm");
		AsmParser p = new AsmParser("C:\\Coding-Projects\\BF-Compiler\\workspace\\AsmToBFCompiler\\assembly\\bf-interpreter-new.asm");
//		AsmParser p = new AsmParser("C:\\sum.asm");
//		AsmParser p = new AsmParser("c:\\h3.quicksort");
		// AsmParser p = new AsmParser("c:\\h3.bubblesort");
		
		ArrayList<AsmBlock> blocks = new ArrayList<AsmBlock>();
		
		blocks = p.getResult();
		this.blocksMap = p.getBlocksMap();
		this.constants = p.getConstants();
		this.defines = new HashMap<String, AsmMemoryAddress>();		
		this.map = new AsmMemoryMap(16, MEMORY_SIZE, 0);
		this.funcMap = new HashMap<AsmBlock, Integer>();
		this.prefixedInstructions = new HashMap<AsmBlock, IInstruction>();
		
		
		System.out.println("last memory address:"+map.getLastMemoryAddress());
		
		int x = 0;
		for(AsmBlock b : blocks ) {
			funcMap.put(b, x++);
		}
		
		// Convert definesInt to defines
		HashMap<String,Integer> definesInt = p.getDefines();
		Set<Entry<String, Integer>> s2 = definesInt.entrySet();
		for(Entry<String, Integer> e : s2) {
			defines.put(e.getKey(), map.getRegister(e.getValue()));
		}
		
		// Add register mappings to defines
		for(x = 0; x < map.getNumRegisters(); x++) {
			defines.put("r"+x, map.getRegister(x));
		}
	
		// Create the load and store trees
		int loadFuncId = blocks.size();
		int storeFuncId = blocks.size()+1;
		
		IInstruction storeTree = createStoreFunctionTree();
		IInstruction loadTree = createLoadFunctionTree();
		
		IInstruction[] functions = new IInstruction[2+blocks.size()];
		
		// Compile the blocks into values
		x = 0;
		for(AsmBlock b : blocks ) {
			IInstruction ii =  convBlock(b, loadFuncId, storeFuncId);
			functions[x] = ii;
			x++;
		}	
		
		// Insert the prefixed code (created by convLoadI)
		for(x = 0; x < blocks.size(); x++) {
			AsmBlock block = blocks.get(x);
			if(prefixedInstructions.containsKey(block)) {
				ICollection newInst = new ICollection();
				// Insert the prefix instructions at the beginning of the function block 
				newInst.addInstruction(prefixedInstructions.get(block));
				newInst.addInstruction(functions[x]);
				
				// Replace the old fxn block with the new
				functions[x] = newInst;
			}
		}
		
		// Assign the remaining two instructions
		functions[loadFuncId] = loadTree;
		functions[storeFuncId] = storeTree;
	
		// Create the function tree
		IRange fxntree = new IRange(map.getFuncVal(), 0, functions.length-1, functions);
		
		
		ICollection program = new ICollection();
		
		// Add pre-program setup code (configures memory as needed)
//		program.addInstruction(createSortProgramTestMemory(1, 2, 100));
		program.addInstruction(createBFInterpreterTestMemory());
		
		// Add all the components of the program
		final int INITIAL_FUNC_VAL = 0; // The "initial" block will always be 0
		program.addInstruction(new ISet(map.getFuncVal(), INITIAL_FUNC_VAL));
		program.addInstruction(new ISet(map.getContinueProgram(), 1));
		program.addInstruction(new IBF("["));
			program.addInstruction(new IGoto(map.getFuncVal()));
			program.addInstruction(fxntree);
		
			program.addInstruction(new IGoto(map.getContinueProgram()));
		program.addInstruction(new IBF("]"));
		
		
//		for(x = 0; x < 250/*map.getProgramMemorySize()*/; x++) {
//			program.addInstruction(new MPrintString("mem["+x+"]:"));
//			program.addInstruction(new MPrint(map.getProgMemory(x)));
//		}
				
		program.addInstruction(new MPrintString("BF program complete.\n"));
		

		int tempVarsReqd = BFCLaunchUtil.calculateTempVarsReqd(program)+1;
		System.out.println("Using "+tempVarsReqd+" temp variables.");
		
		
		MemoryLayoutUtil util = map.generateLayoutUtil();
		
		for(x = 0; x < tempVarsReqd; x++) {
			util.addTempVariable(x);
		}
		
		
		
//		int increment = 100/15;
//		int start = 0;
//		for(x = 0; x < 100; x++) {
//			if(start > increment) {
//				util.addTempVariable(x);
//				start = 0;
//			} else {
//				start += increment;
//			}
//		}
//		
//		for(x = 0; x < 264; x++) {
//			util.addTempVariable(x+100);
//		}
//		
//		start = 0;
//		increment = 7000/21;
//		for(x = 400; x < 7000; x++) {
//			if(start > increment) {
//				util.addTempVariable(x);
//				start = 0;
//			} else {
//				start += increment;
//			}
//		}
		
		
		util.calculateAndShiftLayout();
		
		int[] resultMemory = BFCLaunchUtil.runProgram(new int[30000], program, util.getTemporaryVariables()).getMemory();
		

		System.out.println();
		
		System.out.println("Register contents:");
		for(x = 0; x < map.getNumRegisters(); x++) {
			System.out.print("r"+x+": ");
			BFCUtil.printAtAddr(map.getRegister(x), resultMemory);	
		}

		System.out.println();
		System.out.println("Memory Pointer: ");
//		BFCUtil.printAtAddr(map.getMemoryPointer(), resultMemory);
		
		System.out.println();
		System.out.println("Memory contents:");
		for(x = 0; x < map.getProgramMemorySize(); x++) {
			System.out.print(x+": ");
			BFCUtil.printAtAddr(map.getProgMemory(x), resultMemory);	
		}
		
	}

	
	private ICollection createBFInterpreterTestMemory() {

		ICollection ic = new ICollection();

		String prg1 = "->++>+++>+>+>++>>+>+>+++>>+>+>++>+++>+++>+>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+>+>++>>>+++>>>>>+++>+>>>>>>>>>>>>>>>>>>>>>>+++>>>>>>>++>+++>+++>+>>+++>>>+++>+>+++>+>++>+++>>>+>+>+>+>++>+++>+>+>>+++>>>>>>>+>+>>>+>+>++>+++>+++>+>>+++>+++>+>+++>+>++>+++>++>>+>+>++>+++>+>+>>+++>>>+++>+>>>++>+++>+++>+>>+++>>>+++>+>+++>+>>+++>>+++>>+[[>>+[>]+>+[<]<-]>>[>]<+<+++[<]<<+]>>+[>]+++[++++++++++>++[-<++++++++++++++++>]<.<-<]";
		String prg2 = ">+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.>>>++++++++[<++++>-]<.>>>++++++++++[<+++++++++>-]<---.<<<<.+++.------.--------.>>+.";
		String prg3 = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.";
		String prg4 = "[]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.";
		String prg5 = "->++>+++>+>+>++>>+>+>+++>>+>+>++>+++>+++>+>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+>+>++>>>+++>>>>>+++>+>>>>>>>>>>>>>>>>>>>>>>+++>>>>>>>++>+++>+++>+>>+++>>>+++>+>+++>+>++>+++>>>+>+>+>+>++>+++>+>+>>+++>>>>>>>+>+>>>+>+>++>+++>+++>+>>+++>+++>+>+++>+>++>+++>++>>+>+>++>+++>+>+>>+++>>>+++>+>>>++>+++>+++>+>>+++>>>+++>+>+++>+>>+++>>+++>>+[[>>+[>]+>+[<]<-]>>[>]<+<+++[<]<<+]";
		String prg6 = ">---->-->+>++++>++>+>+>+>+>-->->->>>>->-->-->-->-->->>+>-->->>>>>>+>--->++>>>>>>++>->>>>>>>>>>>>>>>+>>>>++>->>>>+>--->++>--->--->--->++>+>+>-->->->->++++>+>>+>+>>++>->->-->->>>>>+>>++>>>>>>-->-->+>+>>->->>++>->>>+>++>->>++++>>>+>+>-->->->>>>>>>>>>>+>+>--->++>>>>>>>->->-->+>++>+>+>-->->-->->++>--->+>+>>++>>++>--->->->>>>>->-->>>>>+>-->+>+>+>>->->->>++>++>>>>++++[-]"; // [[+>>>+<<<]<++++]";
		String prg7 = "-[-]";
		String prg8 = "+[>+]";
		
		String prg = prg2;
		
		for(int x = 0; x < map.getNumRegisters(); x++) {
			ic.addInstruction(new MSet(map.getRegister(x), BFCUtil.convInt(0)));
		}
		
		for(int x = 0; x < 800; x++) {
			if(x < prg.length()) {
				ic.addInstruction(new MSet(map.getProgMemory(x), BFCUtil.convInt(prg.charAt(x))));
			} else {
//				System.out.println("x:"+x+" -- " +map.getProgMemory(x).getContents()[0].getPosition());;
				ic.addInstruction(new MSet(map.getProgMemory(x), BFCUtil.convInt(0)));				
			}
			
		}
		
		return ic;
		
	}
	
	private ICollection createSortProgramTestMemory(int numAddr, int startAddr, int numNumbers) {
		AsmMemoryAddress size = map.getProgMemory(numAddr);
		
		ICollection ic = new ICollection();
		ic.addInstruction(new MSet(size, BFCUtil.convInt(numNumbers)));

		Random rand = new Random(0); // TODO: CURR - Remove me (seed) 
		for(int x = startAddr; x < startAddr+numNumbers; x++) {
			
			ic.addInstruction(new MSet(map.getProgMemory(x), BFCUtil.convInt(rand.nextInt(20))));
		}

		return ic;
		
	}
	
	private int[] createSortProgramTestMemoryArray(int numAddr, int startAddr, int numNumbers) {
		
		ICollection ic = createSortProgramTestMemory(numAddr, startAddr, numNumbers);

		int[] r = BFCLaunchUtil.runWMemory(ic, 10, 0); 
		
		return r;
	}

	
//	private int[] createOldTestMemory() {
//		
//		AsmMemoryAddress size = map.getProgMemory(0);
//		
//		ICollection ic = new ICollection();
//		ic.addInstruction(new MSet(size, BFCUtil.convInt(MEMORY_SIZE)-1));
//		
//		for(int x = 1; x < MEMORY_SIZE; x++) {
//			ic.addInstruction(new MSet(map.getProgMemory(x), BFCUtil.convInt(20-x)));
//		}
//		
//		int[] r = runWMemory(ic, 100); 
//				
//		return r;
//	}
	
	private IInstruction createStoreFunctionTree() {
		
		final int SIZE_OF_PROGRAM_MEM = map.getProgramMemorySize();
		
		// Create the copy array
		ICollection[] ica = new ICollection[SIZE_OF_PROGRAM_MEM];
		for(int x = 0; x < ica.length; x++) {
			AsmMemoryAddress ma = map.getProgMemory(x);
			
			ica[x] = new ICollection();
			ica[x].addInstruction(new MMove(map.getPointerCopy(), ma));
//			ica[x].addInstruction(new MCopy(map.getPointerCopy(), ma));
			ica[x].addInstruction(new ICopy(map.getMemNextFuncVal(), map.getFuncVal()));
		}

		ICollection ic = new ICollection();
		ic.addInstruction(new IBinaryRange(map.getMemoryPointerArr(), ica));

		return ic;
	}

	private IInstruction createLoadFunctionTree() {
		
		final int SIZE_OF_PROGRAM_MEM = map.getProgramMemorySize();
		
		// Create the copy array
		ICollection[] ica = new ICollection[SIZE_OF_PROGRAM_MEM];
		for(int x = 0; x < ica.length; x++) {
			AsmMemoryAddress ma = map.getProgMemory(x);
			AsmMemoryAddress dest = map.getPointerCopy();
			
			ica[x] = new ICollection();
			ica[x].addInstruction(new MCopy(ma, dest));
			ica[x].addInstruction(new ICopy(map.getMemNextFuncVal(), map.getFuncVal()));
		}

		ICollection ic = new ICollection();
		ic.addInstruction(new IBinaryRange(map.getMemoryPointerArr(), ica));

		return ic;
	}
	
	
//	private IInstruction createNewLoadFunctionTree() {
//		
//		final int SIZE_OF_PROGRAM_MEM = map.getProgramMemorySize();
//		
//		
//		
//		
//		// Create the copy array
//		ICollection[] ica = new ICollection[SIZE_OF_PROGRAM_MEM];
//		for(int x = 0; x < ica.length; x++) {
//			AsmMemoryAddress ma = map.getProgMemory(x);
//			AsmMemoryAddress dest = map.getPointerCopy();
//			
//			ica[x] = new ICollection();
//			ica[x].addInstruction(new MCopy(ma, dest));
//			ica[x].addInstruction(new ICopy(map.getMemNextFuncVal(), map.getFuncVal()));
//		}
//
//		ICollection ic = new ICollection();
//		ic.addInstruction(new MRange(map.getMemoryPointer(), 0, SIZE_OF_PROGRAM_MEM-1, ica));
//
//		return ic;
//	}


	
//	private IInstruction createInnerLoadFunctionTree() {
//		
//		final int SIZE_OF_PROGRAM_MEM = map.getProgramMemorySize();
//		
//		// Create the copy array
//		ICollection[] ica = new ICollection[SIZE_OF_PROGRAM_MEM];
//		for(int x = 0; x < ica.length; x++) {
//			AsmMemoryAddress ma = map.getProgMemory(x);
//			AsmMemoryAddress dest = map.getPointerCopy();
//			
//			ica[x] = new ICollection();
//			ica[x].addInstruction(new MCopy(ma, dest));
//			ica[x].addInstruction(new ICopy(map.getMemNextFuncVal(), map.getFuncVal()));
//		}
//
//		ICollection ic = new ICollection();
//		ic.addInstruction(new MRange(map.getMemoryPointer(), 0, SIZE_OF_PROGRAM_MEM-1, ica));
//
//		return ic;
//	}

	
	
	// Instruction Conversion Methods -----------------
	
	
	private ICollection convBlock(AsmBlock block, int loadFuncNum, int storeFuncNum) {
		ICollection ic = new ICollection();
		
		ArrayList<AsmParserInstruction> a = block.getAInst();
				
		boolean lastInstrWasJumpOrHalt = false; // Whether or not the last instruction encountered was a jump
		
		// For each instruction in the block...
		for(int x = 0; x < a.size(); x++) {
			AsmParserInstruction b = a.get(x);
			String act = b.getAction().toLowerCase().trim();

			if(act.equals("loadi") || act.equals("storei") || act.equals("jumplte") || act.equals("halt")) {
				lastInstrWasJumpOrHalt = true;
			} else {
				lastInstrWasJumpOrHalt = false;
			}

			// TODO: Not a real mult, only useful for the Quicksort.
			if(act.equals("mult")) { ic.addInstruction(convBaileyMult(b)); }
			else if(act.equals("add")) { ic.addInstruction(convAdd(b)); }
			else if(act.equals("sub")) { ic.addInstruction(convSub(b)); }
			else if(act.equals("load")) { ic.addInstruction(convLoad(b)); }
			else if(act.equals("loadi")) { ic.addInstruction(convLoadI(b, block, loadFuncNum)); }
			
			else if(act.equals("store")) { ic.addInstruction(convStore(b, block, storeFuncNum)); }
			else if(act.equals("storei")) { ic.addInstruction(convStoreI(b, block, storeFuncNum)); }
			
			else if(act.equals("halt")) { ic.addInstruction(convHalt()); }
			
			else if(act.equals("jumplte")) { ic.addInstruction(convJumpLte(b, block)); }
			
			else if(act.equals("printstmnt")) { ic.addInstruction(convPrintStmnt(b.getParams())); }
			
			else if(act.equals("printregvala")) { ic.addInstruction(convPrintRegValA(b)); }
			
			else if(act.equals("printregval")) { ic.addInstruction(convPrintRegVal(b)); }
			
			else {
				throw(new RuntimeException("Unable to recognize instruction. ["+act+"]"));
			}
		}
		
		if(!lastInstrWasJumpOrHalt) {
			if(funcMap.get(block.getNext()) != null) {
				ic.addInstruction(new ISet(map.getFuncVal(), funcMap.get(block.getNext())));
			} else {
				throw(new RuntimeException("Should this happen?"));
				// ic.addInstruction(convHalt());
			}
		}
		
		return ic;
	}
	

	// TODO: I should go through and ensure that block.getNext() will never throw an exception


	private IInstruction convPrintStmnt(String[] params) {
		ICollection ic = new ICollection();
		
		String r = "";
		
		for(String s : params) {
			String str = s;
			str = str.replace("\"", "");
			str = str.replace("\\n", "\n");
			r += str + " ";
		}
		
		ic.addInstruction(new MPrintString(r));
		
		return ic;
	}

	
	private IInstruction convPrintRegVal(AsmParserInstruction ai) {
		ICollection ic = new ICollection();
		AsmMemoryAddress first = defines.get(ai.getParams()[0]);
		
		ic.addInstruction(new MPrint(first));
		
		return ic;
	}

	
	
	private IInstruction convPrintRegValA(AsmParserInstruction ai) {
		ICollection ic = new ICollection();
		AsmMemoryAddress first = defines.get(ai.getParams()[0]);
		
		ic.addInstruction(new MPrintA(first));
		
		return ic;
	}
	
	private IInstruction convJumpLte(AsmParserInstruction ai, AsmBlock block) {
		ICollection ic = new ICollection();
		
		AsmMemoryAddress left = defines.get(ai.getParams()[0]);
		AsmMemoryAddress right = defines.get(ai.getParams()[1]);
		int funcId = funcMap.get(blocksMap.get(ai.getParams()[2].toLowerCase()));
		
		// Inner is the code that is run in the event that the jump condition is met
		ICollection inner = new ICollection();
			inner.addInstruction(new ISet(map.getFuncVal(), funcId));
		
		// Inner 2 is the code that is run in the event of a non-jump condition is met
		ICollection inner2 = new ICollection();
			if(block.getNext() != null) {
				inner2.addInstruction(new ISet(map.getFuncVal(), funcMap.get(block.getNext())));
			}
			
		ic.addInstruction(new MGTLTE(left, right, inner2, inner));
				
		return ic;
	}
	
	private IInstruction convLoadI(AsmParserInstruction ai, AsmBlock block, int loadFuncNum) {
		ICollection ic = new ICollection();
		
		// register[dest] <--- M[pointer];
		
		
		// Destination register
		AsmMemoryAddress dest = defines.get(ai.getParams()[0]);
		
		// Source register
		AsmMemoryAddress pointer = defines.get(ai.getParams()[1]);
		
		
		// TODO: This means that the value of the pointer (which is the memory address) must be < 1000
		
		// This means that the value of the pointer (which is the memory address) must be < 1000 (see MDownConvert impl)
		ic.addInstruction(new MDownConvertToBinary(pointer, map.getMemoryPointerArr()));
//		ic.addInstruction(new MCopy(pointer, map.getMemoryPointer()));
		
		ic.addInstruction(new ISet(map.getMemoryRW(), AsmMemoryMap.MEMORY_RW_READ));
		ic.addInstruction(new ISet(map.getFuncVal(), loadFuncNum));
		ic.addInstruction(new ISet(map.getMemNextFuncVal(), funcMap.get(block.getNext())));
		
		// The purpose of prefixedInstruction is to prepend an additional instruction to the block that is directly after this block 
		// (prepend an instruction to the block which is directly after the block ending with LoadI)
		//
		// A potential problem could exist if the block that is directly after were to be branched 
		// to by another part of the program. We avoid the problem by adding an empty block directly 
		// after LoadI (added in the parser) in cases where this problem occurs.  That way, we can 
		// be sure that the block directly after the LoadI block will never be branched to, and we 
		// can safely add post LoadI instructions.
		
		// The final instruction is to copy the value from the pointer copy address to the destination register
		prefixedInstructions.put(block.getNext(), new MCopy(map.getPointerCopy(), dest));
		
		return ic;
	}
	
	private IInstruction convLoad(AsmParserInstruction ai) {
		ICollection ic = new ICollection();
		
		AsmMemoryAddress dest = defines.get(ai.getParams()[0]);
		int c = BFCUtil.convInt(constants.get(ai.getParams()[1]));
		
		ic.addInstruction(new MSet(dest, c));
		
		return ic;
	}
	
	private IInstruction convStore(AsmParserInstruction ai, AsmBlock block, int storeFuncNum) {		
		throw(new RuntimeException("Operation not supported."));
		
//		ICollection ic = new ICollection();
//		
//		
//		
//		MemoryAddress dest = defines.get(ai.getParams()[0]);
//		int value = Util.convInt(Integer.parseInt(ai.getParams()[1]));
//		
//		ic.addInstruction(new MSet(map.getPointerCopy(), value));
//		ic.addInstruction(new MDownConvert(dest, map.getMemoryPointer()));
//		ic.addInstruction(new ISet(map.getMemoryRW(), AsmMemoryMap.MEMORY_RW_WRITE));
//		ic.addInstruction(new ISet(map.getFuncVal(), storeFuncNum));
//		ic.addInstruction(new ISet(map.getMemNextFuncVal(), funcMap.get(block.getNext())));
//
//		return ic;		
	}
	
	private IInstruction convStoreI(AsmParserInstruction ai, AsmBlock block, int storeFuncNum) {
		ICollection ic = new ICollection();
		
		AsmMemoryAddress dest = defines.get(ai.getParams()[0]); // register that the memory address value will be stored in
		AsmMemoryAddress value = defines.get(ai.getParams()[1]); // register which holds the memory address
		
		// M[dest] <-- value
		
		ic.addInstruction(new MCopy(value, map.getPointerCopy()));
		ic.addInstruction(new MDownConvertToBinary(dest, map.getMemoryPointerArr()));
//		ic.addInstruction(new MCopy(dest, map.getMemoryPointer()));
		
		ic.addInstruction(new ISet(map.getMemoryRW(), AsmMemoryMap.MEMORY_RW_WRITE));
		ic.addInstruction(new ISet(map.getFuncVal(), storeFuncNum));
		ic.addInstruction(new ISet(map.getMemNextFuncVal(), funcMap.get(block.getNext())));

		return ic;
	}
	
	private IInstruction convAdd(AsmParserInstruction ai) {
		
		AsmMemoryAddress dest = defines.get(ai.getParams()[0]);
		AsmMemoryAddress a = defines.get(ai.getParams()[1]);
		AsmMemoryAddress b = defines.get(ai.getParams()[2]);
		
		return new MAdd(a, b, dest);
	}
	
	// TODO: This is a fake conv mult, useful only for B's quick sort.
	private IInstruction convBaileyMult(AsmParserInstruction ai) {
		AsmMemoryAddress dest = defines.get(ai.getParams()[0]);
		AsmMemoryAddress a = defines.get(ai.getParams()[1]);
		// MemoryAddress b = defines.get(ai.getParams()[2]);
		
		return new MCopy(a, dest);
	}

	private IInstruction convSub(AsmParserInstruction ai) {
		
		AsmMemoryAddress dest = defines.get(ai.getParams()[0]);
		AsmMemoryAddress a = defines.get(ai.getParams()[1]);
		AsmMemoryAddress b = defines.get(ai.getParams()[2]);
		
		return new MSub(a, b, dest);
	}
	
	private IInstruction convHalt() {
		ICollection ic = new ICollection();
		ic.addInstruction(new ISet(map.getContinueProgram(), 0));
		return ic;
	}

	
}
