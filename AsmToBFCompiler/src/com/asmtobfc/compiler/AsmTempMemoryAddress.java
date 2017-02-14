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

/** Temporary Asm memory address, itself composing of temporary Itr memory addresses. */
public class AsmTempMemoryAddress extends AsmMemoryAddress {
//	CompilerStatus cs = null;
	
	/** Address of sign bit */
	ItrTempAddress tempSign = null;
	
	/** Addresses of contents bits */
	ItrTempAddress[] tempContents = null;
	
	/** Used by the tempvar location optimization algorithm; a hint as to 
	 * where the best spot to the put address is. */
	AsmMemoryAddress closestRealAddr;
	
	
	public AsmTempMemoryAddress(int[] uids, int signUid, CompilerStatus cs, AsmMemoryAddress closestRealAddr) {
		super(null, null);
		
		// Create a new temporary address for the sign bit
		this.tempSign = new ItrTempAddress(signUid, cs, closestRealAddr != null ? closestRealAddr.getSign() : null);
		
		// Create new temporary addresses for the contents bits
		this.tempContents = new ItrTempAddress[uids.length];
		for(int x = 0; x < uids.length; x++) {
			this.tempContents[x] = new ItrTempAddress(uids[x], cs, closestRealAddr != null ? closestRealAddr.getField(x) : null);
		}
		
		this.closestRealAddr = closestRealAddr;
	}
	
	public ItrTempAddress getTempSign() {
		return tempSign;
	}
	
	public ItrTempAddress[] getTempContents() {
		return tempContents;
	}
	
	public ItrAddress getField(int x) {
		return tempContents[x];
	}
	
	public ItrAddress[] getContents() {
		return tempContents;
	}

	public int getIndex() {
		return -12;
	}

	public ItrAddress getSign() {
		return tempSign;
	}

	public AsmMemoryAddress getClosestRealAddr() {
		return closestRealAddr;
	}
}
