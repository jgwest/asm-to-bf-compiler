/*
	Copyright 2011 Jonathan West

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

import java.util.ArrayList;
import java.util.List;

public class AsmIRegister {
	String name;
	int registerNumber;
	int value;
	boolean initialized = false;
	
	List<String> defineNames = new ArrayList<String>();
	
	public AsmIRegister() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(int registerNumber) {
		this.registerNumber = registerNumber;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	@Override
	public String toString() {
		if(!initialized) {
			return name;
		} else {
			return "["+name+"/"+value+"]";
		}
	}

	public void addDefineName(String name) {
		if(!defineNames.contains(name)) {
			defineNames.add(name);
		}
	}

	public int findDefineName(String name) {
		for(int x = 0; x < defineNames.size(); x++) {
			if(defineNames.get(x).equalsIgnoreCase(name)) 
				return x;
		}
		return -1;
	}	
	public String getDefineName(int x) {
		return defineNames.get(x);
	}
	
//	public List<String> getDefineNames() {
//		return defineNames;
//	}
	
	public String debugGetAllDefineNames() {
		if(defineNames.size() == 0) return "";
		
		String r  = "[";
		for(String str : defineNames) {
			r += str +"|";
		}
		// Remove the last |
		r = r.substring(0, r.length()-1);
		r += "]";
		
		return r;
	}
	
	
}
