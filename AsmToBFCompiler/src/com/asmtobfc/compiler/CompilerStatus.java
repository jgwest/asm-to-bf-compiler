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

package com.asmtobfc.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.asmtobfc.instructions.intermediate.IBF;
import com.asmtobfc.instructions.intermediate.IntermediateFactory;

public class CompilerStatus {
	protected int memoryPosition = 0;
	
	/** A finite number of memory cells dedicated to serving as temp vars (e.g. stack). 
	 * Each represents a single cell in memory, which is set on object creation. */
	protected List<ItrAddress> tempVarStack = new ArrayList<ItrAddress>();
	
	/** Map: Match a temporary variable's unique Id -> a concrete ItrAddress location */
	protected HashMap<Integer /** uid */, ItrAddress /** addr of uid */> tempVarMap = null;

	/** Used by return push/pop/peek to return to/from addresses*/
	protected Stack<Integer> returnMemPosition = null;
	
	/** Monotonically increasing unique id, used for assigning temporary variable uids */
	private int nextuid = 0;
	
	
	/** Create a compiler status with x temporary vars, starting at memory start. */
	public CompilerStatus(int numTempVars) {
		
		initTempVars(numTempVars, 0);
		constructorInit();
		 
	}

	/** Create a compiler status with x temporary vars, starting at location y */
	public CompilerStatus(int numTempVars, int tempVarAddrStart) {
		initTempVars(numTempVars, tempVarAddrStart);
		constructorInit();
		 
	}
	
	/** Create a compiler status with the given concrete temporary variables (address positions
	 * in memory should be specified inside the ItrAddress)*/
	public CompilerStatus(List<ItrAddress> tempVars) {
		for(ItrAddress i : tempVars) {
			tempVarStack.add(i);
		}
		constructorInit();
		 
	}
	
	
	/** Generic constructor initialization; called by the constructors. */
	private void constructorInit() {
		memoryPosition = 0;
		tempVarMap = new HashMap<Integer, ItrAddress>();
		returnMemPosition = new Stack<Integer>();
	}

	
	
	// Memory Level -------------------
	
	/** 
	 * Create a temporary asm memory variable; the parameter is a hint for where
	 * to locate the temporary variable in memory (this is used by the tempvar location
	 * algorithm)
	 */
	public AsmTempMemoryAddress newAsmMemoryTemp(AsmMemoryAddress closestRealAddr) {
		int[] uids = new int[6];
		for(int x = 0; x < uids.length; x++) {
			uids[x] = nextuid;
			nextuid++;
		}
		int sign = nextuid++;
		
		return new AsmTempMemoryAddress(uids, sign, this, closestRealAddr);
	}
	
	/** Assign the given address a permanent position in memory. */
	public void assignAsmTempMemoryAddress(AsmTempMemoryAddress tma) {
		
		ItrTempAddress[] itl = tma.getTempContents();
		for(int x = 0; x < itl.length; x++) {
			assignTemp(itl[x].getUniqueId(), itl[x].getClosestRealValue());
		}
				
		assignTemp(tma.getTempSign().getUniqueId(), tma.getTempSign().getClosestRealValue());
	}

	/** Unlock the specified temporary variable location, to mark it as available for use. */
	public void unlockAsmTemp(AsmTempMemoryAddress tma) {
		
		unlockTemp(tma.getTempSign().getUniqueId());
		
		ItrTempAddress[] itl = tma.getTempContents();
		for(int x = 0; x < itl.length; x++) {
			unlockTemp(itl[x].getUniqueId());
		}
				
	}

	// Intermediate Level -------------------
	
	/** Called whenever a temp var is needed, with the object then being
	 * passed into ITempLock; the parameter is used as a hint to allow the temporary 
	 * variable to be positioned in memory close to where it is used. 
	 * */
	public ItrTempAddress newTemp(ItrAddress closestRealValue) {
		int uid = nextuid;
		nextuid++;
		return new ItrTempAddress(uid, this, closestRealValue);
	}

	
	/** Only called by ITempLock and MTempLock on decomposition. Look
	 * at the temporary variables that are currently available (not used) 
	 * and assign the one that is closest to the closestRealValue param. 
	 * 
	 * This is to ensure that there is minimal travel distance between the 
	 * temporary variable and other values it is to be used with. */
	public void assignTemp(int uid, ItrAddress closestRealValue) {
				
		ItrAddress itr;
		if(closestRealValue != null) {

			ItrAddress closestAddr = null;
			int currDiff = Integer.MAX_VALUE;
			int currPos = -1;

			int x = 0;
			// For each of the addresses available in the stack...
			for(ItrAddress ia : tempVarStack) {
				
				// ... find the closest one to closestRealValue
				
				if(closestAddr == null) {
					// First entry...
					closestAddr = ia;
					currDiff = Math.abs(ia.getPosition() - closestRealValue.getPosition());
					currPos = x;
				} else {
					// The rest of the entries...
					int tmpDiff = Math.abs(ia.getPosition() - closestRealValue.getPosition());
					
					if(tmpDiff < currDiff) {
						currDiff = tmpDiff;
						closestAddr = ia;
						currPos = x;
					}
				}
				
				x++;
				
			}
			
			
			// Now that we have found the closest available temporary variable, remove it
			// for our use. 
			
			itr = tempVarStack.remove(currPos);
		} else {
			
			int x = (int)(Math.random()*tempVarStack.size());
			itr = tempVarStack.remove(x);	
		}
		
		tempVarMap.put(uid, itr);
	}
	
	/** Return the memory address (position) of the temporary variable with the given uid */
	public int getTempPos(int uid) {
		return tempVarMap.get((Integer)uid).getPosition();
	}

	/** Unlock a given temporary variable address, adding it back to the stack for
	 * others to use. */
	public void unlockTemp(int uid) {
		ItrAddress itr = tempVarMap.remove(uid);
		tempVarStack.add(itr);
	}
	
	
	/** Initializes temporary variables with the given configuration. */
	private void initTempVars(int numTempVars, int tempVarAddrStart) {
		for(int x = 0; x < numTempVars; x++) {
			ItrAddress il = new ItrAddress(x+tempVarAddrStart, "t"+x);
			tempVarStack.add(il);
		}
	}

	/** Generate BF code which changes the position in memory, from our current
	 * position, to the given location. */
	public IBF changeToMemPos(ItrAddress itr) {
		IBF result = null;

		int c = itr.getPosition() - memoryPosition;
		
		if(c > 0) {
			result = new IBF(">", c);
		} else if(c < 0) {
			result = new IBF("<", (c*-1));
		} else if(c == 0) {
			result = null;
		}
		
		if(result != null) {
			result = (IBF)IntermediateFactory.getInstance().createInstruction(result);
		}
		
		this.memoryPosition = itr.getPosition();
		
		return result;
	}
	
	/** Pushes the current memory location on the memory return stack. */
	public void pushCurrOntoReturnStack() {
		returnMemPosition.push(memoryPosition);
	}

	/** Pops the current memory location from the memory return stack. */
	public int popCurrFromReturnStack() {
		return returnMemPosition.pop();
	}
	
	/** Peeks at the current memory location from the memory return stack. */
	public int peekCurrFromReturnStack() {
		return returnMemPosition.peek();
	}
	
}
