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

package com.asmtobfc.utility.testutil;

import junit.framework.TestCase;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.interpreters.SlowInterpreter;
import com.asmtobfc.utility.BFCLaunchUtil;
import com.asmtobfc.utility.BFCUtil;

public abstract class UtilTestCase extends TestCase {
	static final InterpreterEngine ie = new SlowInterpreter();
	
	/** Create a memory address at the given position in memory
	 * NOTE: THE PARAMETER IS AN ITRADDRESS VALUE. */
	public AsmMemoryAddress createMemAddress(int startPos) {
		ItrAddress[] itr = new ItrAddress[6];
		for(int x = 0; x < 6; x++) {
			itr[x] = new ItrAddress(startPos++);
		}
		ItrAddress sign = new ItrAddress(startPos++);
		
		AsmMemoryAddress ma = new AsmMemoryAddress(sign, itr);
		
		return ma;
	}

	/** Run the given instruction w/ default memory and a small number of temporary variables */
	public int[] runWMemory(IInstruction ii) {
		return runWMemory(ii, 25);
	}

	/** Compiles the given instruction to bf */
	public String compileProgramToString(IInstruction ii, int numTempVars) {
		CompilerStatus cs = new CompilerStatus(numTempVars);
		IInstruction program = ii.decompose(cs);
		String f = program.toBF();
		return f;
	}
	
	/** Run the given instruction using memory provided by the interpreter, and with the given num of temp var*/	
	public InterpreterResult runB(IInstruction ii, int numTempVars) {
		CompilerStatus cs = new CompilerStatus(numTempVars);
		IInstruction program = ii.decompose(cs);
		String f = program.toBF();
		
		InterpreterResult ir = ie.runEngine(f);
		return ir;

	}

	/** Run the given instruction using memory provided by the interpreter, and with the given num of temp var*/
	public static int[] runWMemory(IInstruction ii, int numTempVars) {
		return BFCLaunchUtil.runWMemory(ii, numTempVars, 0);
	}

	
	/** Run the given instruction using memory provided by the interpreter, and with the given num of temp var*/
	public static int[] runWMemory(IInstruction ii, int numTempVars, int start) {
		return BFCLaunchUtil.runWMemory(ii, numTempVars, start);
	}

	public static InterpreterResult run(IInstruction ii, int numTempVars) {
		return BFCLaunchUtil.runProgramQuiet(new int[30000], ii, numTempVars);
	}
	
	
	/** Run the given instruction in the provided memory using a small number of temp vars */
	public int[] runWMemory(int[] memory, IInstruction ii) {
		return BFCLaunchUtil.runWMemory(memory, ii);
	}

	/** Runs the given instruction with the given memory and the specified number of temp vars */
//	public static int[] runWMemory(int[] memory, IInstruction ii, int numTempVars) {
//		return BFCLaunchUtil.runProgram(memory, ii, numTempVars).getMemory();
//	}

	/** Prints the 'ma' memory address in mem */
	public void printAtAddr(AsmMemoryAddress ma, int[] mem) {
		String str = "";
		for(int x = 0; x < 6; x++) {
			str = mem[ma.getField(x).getPosition()] + " " +str;
		}
		System.out.println((mem[ma.getSign().getPosition()] == 1 ? "-" : "+")+" "+ str);
	}

//	public void writeFile(String fn, String s) {
//		try{
//			FileWriter fw = new FileWriter(new File(fn));
//			fw.write(s);
//			fw.flush();
//			fw.close();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

	/** Returns true iff  'ma' == 'val' in 'mem'. */
	public boolean checkAddr(int[] mem, AsmMemoryAddress ma, int val) {
		boolean isCorrect = true;
		
		if(val < 0) {
			isCorrect = (mem[ma.getSign().getPosition()] == 1);
			val *= -1;
		}
		int[] r = BFCUtil.split(val);
		
		for(int x = 0; x < 6 && isCorrect; x++) {
//			System.out.println("compare - "+mem[ma.getField(x).getPosition()]+ " - " + r[x]);
			if(mem[ma.getField(x).getPosition()] != r[x]){
				isCorrect = false;
			}
		}
		return isCorrect;
	}
	
	/** Count the number of ints in the array are non-zero (e.g. have been "written" to
	 * in the case of an int memory array) */
	public static int countWrittenPos(int[] fr) {
		int setPositions = 0;
		for(int x = 0; x < fr.length; x++) {
			if(fr[x] != 0) setPositions++;
		}
		return setPositions;
	}

}
