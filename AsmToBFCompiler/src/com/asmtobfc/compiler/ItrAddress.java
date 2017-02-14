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

/** Corresponds to a position in BF memory. The label is for debug purposes.*/
public class ItrAddress {
	private int position;
	private String label;

	public ItrAddress(int position) {
		this.label = "";
		this.position = position;
	}
	
	public ItrAddress(int position, String label) {
		this.position = position;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public int getPosition() {
		return position;
	}
	
	public String toString() {
		return "(L: "+position + " / "+label+" )";
	}
	
	/** Internal use only */
	public void incrementPosition(int delta) {
		this.position += delta;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ItrAddress)) return false;
		ItrAddress a = (ItrAddress)obj;
		
		if(a.getPosition() == getPosition() &&
				(a.getLabel() == getLabel() || a.getLabel().equals(getLabel()))) {
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public int hashCode() {
		return position + ( label != null ? label.hashCode() : 0 ) ;
	}
}
