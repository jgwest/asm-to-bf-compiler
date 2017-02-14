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

import java.util.ArrayList;
import java.util.Stack;

/**
 * This interpreter speeds the processing of BF programs by:
 * - Interpreting [-] as setting the current cell to 0 (rather than looping continuously)
 * - Scanning for and interpreting long sequences of symbols, and running all 
 *   simultaneously. For instance, ++++++++++ would be interpreted by the program
 *   as a single +10, rather than 10 +1s.
 */
public class FastInterpreter implements InterpreterEngine {

	public InterpreterResult runEngine(String s) {
		int m[] = new int[30000];
		return runEngine(s, m);
	}
	
	// TODO: INTERPRETER - This interpreter differs from the other in that it is not -127 to 128.
	public InterpreterResult runEngine(String s, int[] m ) {
		StringBuilder result = new StringBuilder();
		Stack<Integer> st = new Stack<Integer>();
		
		// This pattern necessarily means set the current cell to 0, so replace it with a
		// new symbol to be recognized below as set to 0
		s = s.replace("[-]", "%");
		
		ArrayList<Instruction> program= new ArrayList<Instruction>(s.length());
		
		// Parse the program into instructions, noting repetition of ><+-.
		Instruction i = null;
		for(int x = 0; x < s.length(); x++) {
			char c = s.charAt(x);
			
			if(i != null) {
				
				if(i.type != c || (i.type != '>' && i.type != '<' && i.type != '+' && i.type != '-')) {
					program.add(i);
					i = new Instruction();
					i.num = 1;
					i.type = c;
				} else {
					i.num++;
				}
			} else {
				i = new Instruction();
				i.num = 1;
				i.type = c;
			}
		}
		
		// Add the final instruction
		if(i != null) {
			program.add(i);
		}
		
		int[] a = new int[program.size()];
		for(int x = 0; x < program.size(); x++) {
			i = program.get(x);
			switch(i.type) {
			
				case '(':
					st.push(x);
					break;
				case ')':
					a[x] = st.peek();
					a[st.peek()] = x;
					st.pop();
					break;

				case '[':
					st.push(x);
					break;
				case ']':
					a[x] = st.peek();
					a[st.peek()] = x;
					st.pop();
					break;
			}
		}

		int curr = 0;
		
		for(int x = 0; x < program.size() && x>= 0; x++) {
			i = program.get(x);
			
			switch(i.type) {
				case '>':
					curr = (curr + i.num) % m.length;
					break;
				case '<':
					curr = (curr - i.num) % m.length;
					break;
				case '+':
					m[curr] = (m[curr]+ i.num)%256;
					break;
				case '-':
					m[curr] = (m[curr] - i.num) % 256;
					
					if(m[curr] < 0) {
						m[curr] += 256;
					}
					else m[curr] = m[curr]%256;
					break;
				case '.':
					result.append((char)m[curr]);
					break;
				case ',':
					x = -9999;
					break;
				case '[':
					if(m[curr] == 0) { 
						x = a[x];
					}
					break;
				case ']':
					x = a[x] - 1;
					break;

				case '(':
					if(m[curr] != 0) { 
						x = a[x];
					}
					break;
				case ')':
					x = a[x] - 1;
					break;

				case '%':
					m[curr] = 0;
					break;
					
			}
		}
		InterpreterResult ir = new InterpreterResult();
		ir.setMemory(m);
		ir.setResult(result.toString());
		
		return ir;
	}

	private static class Instruction {
		public char type = 0;
		public int num = 0;
	}

}
