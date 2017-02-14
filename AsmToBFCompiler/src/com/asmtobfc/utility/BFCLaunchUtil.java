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

package com.asmtobfc.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.compiler.TempVariableScanCompilerStatus;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.interpreters.FastInterpreter;
import com.asmtobfc.interpreters.FastInterpreterNew;
import com.asmtobfc.interpreters.InterpreterEngine;
import com.asmtobfc.interpreters.InterpreterResult;
import com.asmtobfc.interpreters.SlowInterpreter;

public class BFCLaunchUtil {
	static final InterpreterEngine ie = new SlowInterpreter();
	
	/** Run the given instruction w/ default memory and a small number of temporary variables */
	public static int[] runWMemory(IInstruction ii) {
		return runWMemory(ii, 25, 0);
	}
	
	private static long debugLength = 0;
	
	private static void debugOutputProgram(IInstruction prog, int depth, OutputStream os) throws IOException {
		if(depth == 0) {
			debugLength = 0;
		}
		debugLength++;
		
		final String CRLF = "\r\n";
		
		os.write((BFCUtil.repeat(' ', depth*2)).getBytes());
		
		if(prog instanceof ICollection) {
			ICollection collection = (ICollection)prog;
			
			os.write(("Collection: {"+CRLF).getBytes());
			
			for(int x = 0; x < collection.size(); x++ ) {
				IInstruction innerInst = collection.getInstruction(x);
				debugOutputProgram(innerInst, depth+1, os);
			}
			
			os.write(BFCUtil.repeat(' ', depth*2).getBytes());
			os.write("}\r\n".getBytes());
			
			
		} else if(prog instanceof IBF) {
			IBF bf = (IBF)prog;
			if(bf.getRepetitions() < 2) {
				os.write(("IBF - text-size:"+bf.getText().length()+CRLF).getBytes());
			} else {
				os.write(("IBF - text:"+bf.getText()+" reps:"+bf.getRepetitions()+CRLF).getBytes());
			}
			
		} else {
			os.write((prog.getClass().toString()+CRLF).getBytes());
		}
		
		if(depth == 0) {
			System.out.println("debugOutputComplete - Instructions processed:"+debugLength);
		}
	}
	
	/** Run the given instruction using memory provided by the interpreter, and with the given num of temp var*/
	public static int[] runWMemory(IInstruction ii, int numTempVars, int start) {
		CompilerStatus cs = new CompilerStatus(numTempVars, start);
		IInstruction program = ii.decompose(cs);
		String f = program.toBF();
		
		InterpreterResult ir = ie.runEngine(f);
		int[] fr = ir.getMemory();
		
		return fr;
	}

	/** Run the given instruction in the provided memory using a small number of temp vars */
	public static int[] runWMemory(int[] memory, IInstruction ii) {
		return runWMemory(ii, 25, 0);
	}

	
	/** Runs the given instruction with the given memory and the specified number of temp vars */
	public static InterpreterResult runProgramQuiet(int[] memory, IInstruction ii, int numTempVars) {
		
		CompilerStatus cs = new CompilerStatus(numTempVars);

		IInstruction program = ii.decompose(cs);
		
		String f = program.toBF();
		
		
		FastInterpreter fin = new FastInterpreter();
		
		InterpreterResult ir = fin.runEngine(f, memory);
		
		return ir;
	}

	
	/** Runs the given instruction with the given memory and the specified number of temp vars */
	public static InterpreterResult runProgram(int[] memory, IInstruction ii, List<ItrAddress> tempAddrs) {
		
		long startTime = System.currentTimeMillis();
		
		CompilerStatus cs = new CompilerStatus(tempAddrs);
		System.out.println("Decomposing");
		IInstruction program = ii.decompose(cs);
		
		System.out.println("complete: "+(System.currentTimeMillis()-startTime)+"\n");

//		try {
//			FileOutputStream fos = new FileOutputStream(new File("c:\\temp\\bf\\log"));
//		
//			debugOutputProgram(program, 0, fos);
//			fos.close();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		
		startTime = System.currentTimeMillis();
		
		System.out.println("Generating string result");
		
		File f = new File("d:\\out.bf");
		
		try {
			OutputStream os = new FileOutputStream(f);
			program.toBF(os);
			os.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("complete: "+(System.currentTimeMillis()-startTime)+"\n");

		
		startTime = System.currentTimeMillis();
		System.out.println("Running engine");
		
		FastInterpreterNew fin = new FastInterpreterNew();
		
		InterpreterResult ir = fin.runEngine(f, memory);
		
		System.out.println("Engine run time - complete: "+(System.currentTimeMillis()-startTime)+"\n");
		
		System.out.println("Program text output: {{{");
		System.out.println(ir.getResult());
		System.out.println("Program text output: }}}");
		
		return ir;
	}
	
	
	public static int calculateTempVarsReqd(IInstruction ii) {
		TempVariableScanCompilerStatus cs = new TempVariableScanCompilerStatus();
		IInstruction program = ii.decompose(cs);
		
//		cs.calculateTempVarHotspots();
		
		return cs.getTotalNumTempVars();
		
	}

}
