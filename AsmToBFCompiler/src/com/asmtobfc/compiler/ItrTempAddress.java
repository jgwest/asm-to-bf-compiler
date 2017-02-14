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

public class ItrTempAddress extends ItrAddress {
	int uniqueId = -1;

	CompilerStatus cs = null;
	
	/** Used by the tempvar location optimization algorithm; a hint as to 
	 * where the best spot to the put address is. */	
	ItrAddress closestRealValue;
	
	
	public ItrTempAddress(int uniqueId, CompilerStatus cs, ItrAddress closestRealValue) {
		super(-1);
		this.uniqueId = uniqueId;
		this.cs = cs;
		this.closestRealValue = closestRealValue;
	}
	
	public int getUniqueId() {
		return uniqueId;
	}
	
	public String getLabel() {
		return "temp"+uniqueId;
	}

	public int getPosition() {
		return cs.getTempPos(uniqueId);
	}
	
	public String toString() {
		return "(L: tempvar / "+getLabel()+" )";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ItrTempAddress)) {
			return false;
		}

	
		ItrTempAddress a = (ItrTempAddress)obj;
		
		return a.getUniqueId() == getUniqueId();
		
	}
	
	@Override
	public int hashCode() {
		return uniqueId;
	} 
	
	public ItrAddress getClosestRealValue() {
		return closestRealValue;
	}
}
