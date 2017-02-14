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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This interpreter speeds the processing of BF programs by:
 * - Interpreting [-] as setting the current cell to 0 (rather than looping continuously)
 * - Scanning for and interpreting long sequences of symbols, and running all 
 *   simultaneously. For instance, ++++++++++ would be interpreted by the program
 *   as a single +10, rather than 10 +1s.
 */
public class FastInterpreterNew {

	/*
	private static void fp(String s) {
		try {
			FileWriter fw = new FileWriter(new File("/tmp/jlog"), true);
			fw.write(s+"\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	
	public static void main(String[] args) {
		FastInterpreterNew instance = new FastInterpreterNew();
		InterpreterResult ir = instance.runEngine(new File(args[0]));
		System.out.println(ir.getResult());
	}
	
	public InterpreterResult runEngine(File f) {
		int m[] = new int[30000];
		return runEngine(f, m);
	}
	
	// TODO: INTERPRETER - This interpreter differs from the other in that it is not -127 to 128.
	public InterpreterResult runEngine(File f, int[] m ) {
		StringBuilder result = new StringBuilder();
		Stack<Integer> st = new Stack<Integer>();
		
		// This pattern necessarily means set the current cell to 0, so replace it with a
		// new symbol to be recognized below as set to 0
//		s = s.replace("[-]", "%");

		System.out.println("Engine loading file...");
		
		ArrayList<Instruction> program= new ArrayList<Instruction>();

		try {
			FileReader fr = new FileReader(f);

			char[] charsRead = new char[1024];
			int c = -1;
			do {
				c = fr.read(charsRead);
				if(c > 0) {
					parseString(new String(charsRead, 0, c), program);
				}
			} while(c != -1);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println("File loaded. Instructions:" +program.size());
		
		Instruction i;
		
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
		
		Instruction[] prg = new Instruction[program.size()];
		for(int x = 0; x < prg.length; x++) {
			prg[x] = program.get(x);
		}
		
		long start = System.currentTimeMillis();
		
		for(int x = 0; x < prg.length && x>= 0; x++) {
			i = prg[x];

			int currTmp;
			
			switch(i.type) {
				case '>':
					currTmp = (curr + i.num);
					curr = currTmp % m.length;
					break;
				case '<':
					currTmp = (curr - i.num);
					curr = currTmp % m.length;
					break;
				case '+':
					currTmp = m[curr] + i.num;
					m[curr] = (currTmp)%256;
					break;
				case '-':
					currTmp = m[curr] - i.num;
					m[curr] = (currTmp) % 256;
					
					if(m[curr] < 0) {
						m[curr] += 256;
					} else { 
						m[curr] = m[curr]%256;
					}
					break;
				case '.':
					System.out.print((char)m[curr]);
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
		
		System.out.println("Total: "+(System.currentTimeMillis() - start));
		
		InterpreterResult ir = new InterpreterResult();
		ir.setMemory(m);
		ir.setResult(result.toString());
		
		return ir;
	}

	private static void parseString(String s, List<Instruction> program) {
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

	}
	
	private static class Instruction {
		public char type = 0;
		public int num = 0;
	}

}
