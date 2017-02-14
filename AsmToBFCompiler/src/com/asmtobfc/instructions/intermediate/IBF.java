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

package com.asmtobfc.instructions.intermediate;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.instructions.IConcreteInstruction;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.utility.BFCUtil;

public class IBF extends IConcreteInstruction {

	public static final IBF MINUS = new IBF("-");
	public static final IBF PLUS = new IBF("+");
	public static final IBF OP_SQ = new IBF("[");
	public static final IBF CL_SQ = new IBF("]");
	public static final IBF PERIOD = new IBF(".");
	public static final IBF COMMA = new IBF(",");
	public static final IBF GT = new IBF(">");
	public static final IBF LT = new IBF("<");

	
	public static final IBF OP_ROUND = new IBF("(");
	public static final IBF CL_ROUND = new IBF(")");
	
	public static final IBF CLEAR = new IBF("[-]");
	
	public String getText() {
		return text;
	}
	
	protected String text = "";
	int repetitions = -1;
	
	static long debugSize = 0;
	
	public IBF(String text) {
		this.text = text;
		repetitions = -1;
	}

	public IBF(String text, int repetitions) {
		this.text = text;
		this.repetitions = repetitions;
	}
	
	
	
	public IInstruction decompose(CompilerStatus cs) {
		throw(new UnsupportedOperationException());
	}

	public boolean canDecompose() {
		return false;
	}

	public String toBF() {
		if(repetitions == -1) {
			return text;
		} if(repetitions == 0) {
			return "";
		} else {
			if(text.length() > 1) {
				System.err.println("Error");
				throw new RuntimeException("Cannot repeat text of >1 character");
			}
			return BFCUtil.repeat(text.charAt(0), repetitions);
		}
	}
	
	
	public String toString() {
		return "value: {"+toBF()+"}";
	}

	public int getRepetitions() {
		return repetitions;
	}
	
//	private static String containsOnlyOneSymbol(String s) {
//		if(s.length() <= 1) return null;
//		
//		char c = s.charAt(0);
//		for(int x = 1; x < s.length(); x ++) {
//			if(s.charAt(x) != c)  return null;
//		}
//		
//		return ""+c;
//	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IBF)) {
			return false;
		}
		IBF other = (IBF)obj;
		
		return repetitions == other.repetitions && text.equals(other.text);
		
	}
	
	@Override
	public int hashCode() {
		return text.hashCode() + repetitions;
	}
}
