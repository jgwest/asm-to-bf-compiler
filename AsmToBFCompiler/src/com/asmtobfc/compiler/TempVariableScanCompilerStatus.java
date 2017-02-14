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

package com.asmtobfc.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asmtobfc.instructions.intermediate.IBF;

public class TempVariableScanCompilerStatus extends CompilerStatus {

	public static int MAX_TEMP_VARS = 10000; 
	
	int totalNumTempVars = 0;
	int currStackNumTempVars = 0;
	

	// Temporary-variable location calculation algorithm variables ------ 

	// (Work-in progress: tempVarClosestMap needs to be reworked, for instance, children should be a list of ItrAddresses.)
	
	Map<ItrAddress /*from (parent)*/, ItrAddress /*to (child)*/> _tempVarClosestMap = new HashMap<ItrAddress, ItrAddress>();

	List<ItrAddress> _nonTempVarAddresses = new ArrayList<ItrAddress>();
	
	// -----------------------

	
	
	public TempVariableScanCompilerStatus() {
		super(MAX_TEMP_VARS);
	}
	
	public int getTotalNumTempVars() {
		return totalNumTempVars;
	}
	
	
	// Memory Level -------------------
	
	@Override
	public AsmTempMemoryAddress newAsmMemoryTemp(AsmMemoryAddress closestRealValue) {
		
		if(closestRealValue != null) {
			
			if(closestRealValue instanceof AsmTempMemoryAddress) {
//				_tempVarClosestMap.put(closestRealValue, result);				
			} else {
				_nonTempVarAddresses.add(closestRealValue.getSign());
				for(ItrAddress a : closestRealValue.getContents()) {
					_nonTempVarAddresses.add(a);
				}
//				_nonTempVarAddresses.add(closestRealValue);
			}
		}
		
		AsmTempMemoryAddress result = super.newAsmMemoryTemp(closestRealValue);
		
		return result;
	}
	
	@Override
	public void assignAsmTempMemoryAddress(AsmTempMemoryAddress tma) {
		super.assignAsmTempMemoryAddress(tma);
	}
	
	@Override
	public void unlockAsmTemp(AsmTempMemoryAddress tma) {
		super.unlockAsmTemp(tma);
	}

	// Intermediate Level -------------------
	
	
	/** Called whenever a temp var is needed, with the object then being
	 * passed into ITempLock */
	@Override
	public ItrTempAddress newTemp(ItrAddress closestRealValue) {
		
		ItrTempAddress result = super.newTemp(closestRealValue);
		
		if(closestRealValue != null) {
			
			if(closestRealValue instanceof ItrTempAddress) {
				_tempVarClosestMap.put(closestRealValue, result);				
			} else {
				_nonTempVarAddresses.add(closestRealValue);
			}

		}

		
		return result;
	}
	
	/** Only called by ITempLock and MTempLock on decomposition. */
	@Override
	public void assignTemp(int uid, ItrAddress closetRealValue) {
		super.assignTemp(uid, null);
		
		if(currStackNumTempVars == 0) {
			totalNumTempVars++;
		} else {
			currStackNumTempVars--;
		}

	}

	@Override
	public void unlockTemp(int uid) {
		super.unlockTemp(uid);
		
		currStackNumTempVars++;
		if(currStackNumTempVars > totalNumTempVars) {
			throw new RuntimeException("Invalid number of temporary variables");
		}
		
	}

	@Override
	public int getTempPos(int uid) {
		return super.getTempPos(uid);
	}

	@Override
	public IBF changeToMemPos(ItrAddress itr) {
		return super.changeToMemPos(itr);
	}
	
	/** Pushes the current memory location on the memory return stack. */
	@Override
	public void pushCurrOntoReturnStack() {
		super.pushCurrOntoReturnStack();
	}

	/** Pops the current memory location from the memory return stack. */
	@Override
	public int popCurrFromReturnStack() {
		return super.popCurrFromReturnStack();
	}
	
	/** Peeks at the current memory location from the memory return stack. */
	@Override
	public int peekCurrFromReturnStack() {
		return super.peekCurrFromReturnStack();
	}
		
	
	// ---------------------------------------------------------------------
	
	public void calculateTempVarHotspots() {
		
		System.out.println("Temporary variable hotspots:");
		
		int[] locations = new int[30000];
		for(int x = 0; x < locations.length; x++) {
			locations[x] = 0;
		}
		
		
		int[] byHundreds = new int[locations.length/100];
		
		long total = 0;
		for(ItrAddress i : _nonTempVarAddresses) {
			locations[i.getPosition()]++;
			total++;
			byHundreds[(int)(i.getPosition() /100)]++;
		}
		
		
		for(int x = 0; x < byHundreds.length; x++) {
			if(byHundreds[x] > 0) {
				System.out.println((x*100)+": "+((double)byHundreds[x]/(double)total));
			}
		}
				
		
		for(Map.Entry<ItrAddress, ItrAddress> e : _tempVarClosestMap.entrySet()) {
			
		}

	}
	
}
