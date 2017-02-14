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

package com.asmtobfc.parser;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.utility.MemoryLayoutUtil;

public class AsmMemoryMap {
	
	/** The register portion of full memory (in this implementation, a register is just another mem address) */
	AsmMemoryAddress registers[] = null;
	
	/** Full Asm memory of the program  */
	AsmMemoryAddress fullMemory[] = null;
	
	/** Full memory minus the pointer copy, minus the registers */
	AsmMemoryAddress programMemory[] = null;
	
	/** Single address for pointer copy */
	AsmMemoryAddress pointerCopy = null;
	
	/** Whether or not the program should continue running (1 if continue, 0 is terminate) */
	ItrAddress continueProgram = null;
	
	/** Address used for load/store operations, see those docs for details*/
	private AsmMemoryAddress memoryPointer = null;
	
	ItrAddress[] memoryPointerArr = new ItrAddress[14];
	
//	ItrAddress memoryPointerMSB = null;
//	ItrAddress memoryPointerLSB = null;
	ItrAddress memoryRW = null; // 0 if neither, 1 if read, 2 if write

	/** Address contains the next function value to be executed*/
	ItrAddress funcVal = null;  
	
	/** In the case of a load or store, this is the value to return to on completion of that operation. */
	ItrAddress memNextFuncVal = null; 
	
	public static final int MEMORY_RW_NONE = 0;
	public static final int MEMORY_RW_READ = 1;
	public static final int MEMORY_RW_WRITE = 2;

	
	/** The last address in memory used by full memory or any of the above ItrAddresses. 
	 * Addresses after this address are available for other uses.*/
	ItrAddress lastMemoryAddress = null;
	
	// TODO: LOWER - All throughout the program am I using magic numbers for the number of labels in a memory address, may want to change.
	
	public AsmMemoryMap(int numRegisters, int sizeOfMemory, int startPoint) {
		final int POINTERCOPY = 1; // 1 is added to the array of memory addresses to allocate a memory address for the pointer copy register
		
		this.registers = new AsmMemoryAddress[numRegisters];
		this.fullMemory = new AsmMemoryAddress[numRegisters+sizeOfMemory+POINTERCOPY+1/*memory pointer*/+3/*memory pointer arr*/];
		this.programMemory = new AsmMemoryAddress[sizeOfMemory];
		
		init(startPoint);
	}
	
	public MemoryLayoutUtil generateLayoutUtil() {
		MemoryLayoutUtil result = new MemoryLayoutUtil();
	
		result.addAddress(this.continueProgram);
		 
		result.addAddress(this.memoryRW);
		
		result.addAddress(this.funcVal);
		
		result.addAddress(this.memNextFuncVal);

		// Re-assign all the MemoryAddresses ------------------------
		
		for(int x = 0; x < fullMemory.length; x++) {
			result.addAddress(fullMemory[x].getSign());
			for(ItrAddress i : fullMemory[x].getContents()) {
				result.addAddress(i);
			}
		}
		
		return result;

	}
	
	public void incrementAllMemoryLocations(int delta) {

		// Re-assign all the ItrAddresses -----------------------------
		this.continueProgram.incrementPosition(delta); 
		
		this.memoryRW.incrementPosition(delta);
		
		this.funcVal.incrementPosition(delta);
		
		this.memNextFuncVal.incrementPosition(delta);

		// Re-assign all the MemoryAddresses ------------------------
		
		for(int x = 0; x < fullMemory.length; x++) {
			fullMemory[x].getSign().incrementPosition(delta);
			for(ItrAddress i : fullMemory[x].getContents()) {
				i.incrementPosition(delta);
			}
		}
		
	}
	
	private int nextPoint(final int x) {
		int result = x+1;
		if(result % 100 == 0) {
			SingleTempHelper.getInstance().add(result);
			result +=1;
		}
		return result;
	}	
	
	private void init(int startPoint) {
 

		startPoint = nextPoint(startPoint);
		


		// Assign all the ItrAddresses -----------------------------
		this.continueProgram = new ItrAddress(startPoint); 
		startPoint = nextPoint(startPoint);
		
		this.memoryRW = new ItrAddress(startPoint);
		startPoint = nextPoint(startPoint);
		
		this.funcVal = new ItrAddress(startPoint);
		startPoint = nextPoint(startPoint);
		
		this.memNextFuncVal = new ItrAddress(startPoint);
		startPoint = nextPoint(startPoint);
		
		// Assign all the MemoryAddresses ------------------------
		
		for(int x = 0; x < fullMemory.length; x++) {
			
			ItrAddress sign = new ItrAddress(startPoint);
			startPoint = nextPoint(startPoint);
			
			ItrAddress[] vals = new ItrAddress[6];
			
			for(int y = 0; y < vals.length; y++) {
				vals[y] = new ItrAddress(startPoint);
				startPoint = nextPoint(startPoint);
			}
						
			fullMemory[x] = new AsmMemoryAddress(sign, vals);
		}
		
		// Assign registers (from fullMemory)
		int c = 0;
		for(int x = 0; x < registers.length; x++) {
			registers[x] = fullMemory[c++];
			registers[x].setDebugLabel("r"+x);
		}
		
		// Assign memory pointer
		this.memoryPointer = fullMemory[c++];
		this.memoryPointer.setDebugLabel("Memory Pointer");
		

		// Fill the memory pointer array with valid values
		int valuesInArr = 0;
		while(valuesInArr < memoryPointerArr.length) {
			AsmMemoryAddress t = fullMemory[c++];
			for(ItrAddress a : t.getContents()) {
				
				if(valuesInArr < memoryPointerArr.length) {
					memoryPointerArr[valuesInArr] = a;
				}
				valuesInArr++;
			}
		}
				
		
		// Assign pointer copy (from fullMemory)
		pointerCopy = fullMemory[c++]; 
		this.pointerCopy.setDebugLabel("Pointer Copy");
		
		// Assign program memory (from fullMemory)
		for(int x = 0; x < programMemory.length; x++) {
			programMemory[x] = fullMemory[c++];
			programMemory[x].setDebugLabel("mem["+x+"]");
		}

		// Verify that we have used up all memory that we expected to use
		if(fullMemory.length > c) {
			System.err.println("WARNING: There seems to be empty space at end of full memory.");
			throw new RuntimeException("WARNING.");
		}

		ItrAddress[] last = fullMemory[c-1].getContents();
		
		for(ItrAddress i : last) {
			if(lastMemoryAddress == null) {
				lastMemoryAddress = i;
			}
			
			if(lastMemoryAddress.getPosition() < i.getPosition()) {
				lastMemoryAddress = i;
			}
		}

	}


	public ItrAddress getMemoryRW() {
		return memoryRW;
	}	

	
	public ItrAddress[] getMemoryPointerArr() {
		return memoryPointerArr;
	}
	
//	public AsmMemoryAddress getMemoryPointer() {
//		return memoryPointer;
//	}
	

	public AsmMemoryAddress getRegister(int x) {
		return registers[x];
	}
	
	public AsmMemoryAddress getProgMemory(int x) {
		return programMemory[x];
	}
	
	public AsmMemoryAddress getPointerCopy() {
		return pointerCopy;
	}	

	public ItrAddress getContinueProgram() {
		return continueProgram;
	}

	public ItrAddress getFuncVal() {
		return funcVal;
	}


	public ItrAddress getMemNextFuncVal() {
		return memNextFuncVal;
	}
	
	public int getProgramMemorySize() {
		return programMemory.length;
	}
	
	public int getNumRegisters() {
		return registers.length;
	}
	

	public ItrAddress getLastMemoryAddress() {
		return lastMemoryAddress;
	}
	
	public void printAssignments() {

		System.out.println("--------------------------------------");
		
		System.out.println("Registers:");
		/** The register portion of full memory (in this implementation, a register is just another mem address) */
		for(int x = 0; x < registers.length; x++) {
			System.out.println("r"+x+":" + registers[x]);
		}
		
		System.out.println("Pointer Copy:");
		/** Single address for pointer copy */
		System.out.println(pointerCopy.toString());
		
		System.out.println("Continue Program (ItrAddress):");
		/** Whether or not the program should continue running (1 if continue, 0 is terminate) */
		System.out.println(continueProgram);
		
		System.out.println("Memory Pointer:");
		/** Address used for load/store operations, see those docs for details*/
		System.out.println(memoryPointer);
		
		
		System.out.println("MemoryRW: (ItrAddress)");
		System.out.println(memoryRW);

		/** Address contains the next function value to be executed*/
		System.out.println("FuncVal: (ItrAddress)");
		System.out.println(funcVal);
		
		System.out.println("memNextFuncVal: (ItrAddress)");
		/** In the case of a load or store, this is the value to return to on completion of that operation. */
		System.out.println(memNextFuncVal); 

		System.out.println("Full Memory:");
		/** Full Asm memory of the program  */
		for(int x = 0; x < fullMemory.length; x++) {
			System.out.println(x+": "+fullMemory[x]);
		}
		
		System.out.println("Program Memory:");
		/** Full memory minus the pointer copy, minus the registers */
		for(int x = 0; x < programMemory.length; x++) {
			System.out.println(x+": "+ programMemory[x]);
		}
		

	}
}
