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
package com.asmtobfc.interpreters;

import java.util.Stack;

public class SlowInterpreter implements InterpreterEngine {

	public InterpreterResult runEngine(String s) {
		int m[] = new int[30000];
		return runEngine(s, m);
	}
		
	// TODO: INTERPRETER - This interpreter differs from the other in that it is not -127 to 128.
	public InterpreterResult runEngine(String s, int[] mb ) {		
		StringBuilder result = new StringBuilder();
		
		byte[]m = new byte[mb.length];
		for(int x = 0; x < mb.length;x++) { m[x] = (byte)mb[x]; }

		int ai[] = new int[s.length()];
		Stack<Integer> st = new Stack<Integer>();

		for (int x = 0; x < s.length(); x++) {
			char c = s.charAt(x);
			switch (c) {
			case '[':
				st.push(x);
				break;
			case ']':
				ai[x] = st.peek();
				ai[st.peek()] = x;
				st.pop();
				break;
			
			case '(':
				st.push(x);
				break;
			case ')':
				ai[x] = st.peek();
				ai[st.peek()] = x;
				st.pop();
				break;
			}
		}

		st.clear();

		int i = 0;
		for (int j = 0; j < s.length() && j >=  0; j++) {
			switch (s.charAt(j)) {
				default:
					break;
	
				case 60: // '<'
					i = (i + m.length-1) % m.length;
					break;
	
				case 62: // '>'
					i = (i + 1) % m.length;
					break;
	
				case 43: // '+'
					m[i]++;
					break;
	
				case 45: // '-'
					m[i]--;
					break;
	
				case 46: // '.'
					result.append(new Character((char) m[i]));
					break;
	
				case 44: // ','
					j = -9999;
					break;
	
				case 91: // '['
					if (m[i] == 0)
						j = ai[j];
					break;
	
				case 93: // ']'
					j = ai[j] - 1;
					break;

				case '(': // '['
					if (m[i] != 0)
						j = ai[j];
					break;
	
				case ')': // ']'
					j = ai[j] - 1;
					break;					
			}
		}
		
		InterpreterResult ir = new InterpreterResult();
		
		int[] mem = new int[m.length];
		for(int x = 0; x < m.length; x++) {
			mem[x] = (int)m[x];
		}
		
		ir.setMemory(mem);
		ir.setResult(result.toString());
		return ir;
		
	}

}
