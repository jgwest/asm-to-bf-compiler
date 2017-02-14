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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class InterpreterMain {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("* Invalid number of parameters.");
			return;
		}
		
		String filename;
		filename = "C:\\quine.b";
		filename = "C:\\mandelbrot.b";
		filename = "C:\\bottlesofbeer.b";
		File f = new File(filename);
		String s = loadFile(f);
		
		InterpreterEngine si = new FastInterpreter();
		InterpreterResult irfast = si.runEngine(s);

		si = new SlowInterpreter();
		InterpreterResult irslow = si.runEngine(s);
		
		System.out.println(irfast.equals(irslow));
		
		/*
		Calendar c = Calendar.getInstance();
		System.out.println(Calendar.getInstance().getTimeInMillis() - c.getTimeInMillis());

		c = Calendar.getInstance();
		System.out.println(Calendar.getInstance().getTimeInMillis() - c.getTimeInMillis());*/

	}

	/*
	public static void runFast(String s) {
		int m[] = new int[30000];
		Stack<Integer> st = new Stack<Integer>();
		
		s = s.replace("[-]", "%");
		
		ArrayList<Instruction> program= new ArrayList<Instruction>(s.length());
		
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
		
		if(i != null) {
			program.add(i);
		}
		
		
		int[] a = new int[program.size()];
		for(int x = 0; x < program.size(); x++) {
			i = program.get(x);
			switch(i.type) {
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
		
		for(int x = 0; x < program.size(); x++) {
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
					System.out.print((char)m[curr]);
					break;
				case ',':
					throw(new RuntimeException("Unsupported operation."));
				case '[':
					if(m[curr] == 0) { 
						x = a[x];
					}
					break;
				case ']':
					x = a[x] - 1;
					break;
				case '%':
					m[curr] = 0;
					break;
					
			}
		}
	}*/
	/*
	public static  void runSlow(String s) {
		byte m[] = new byte[30000];
	
		int ai[] = new int[s.length()];
		Stack<Integer> st = new Stack<Integer>();
		
		for(int x = 0; x < s.length(); x++) {
			char c = s.charAt(x);
			switch(c) {
				case '[':
					st.push(x);
					break;
				case ']':
					ai[x] = st.peek();
					ai[st.peek()] = x;
					st.pop();
					break;
			}
		}
		
		st.clear();

		
		int i = 0;
		for (int j = 0; j < s.length(); j++)
			switch (s.charAt(j)) {
			default:
				break;

			case 60: // '<'
				i = (i + 8095) % 8096;
				break;

			case 62: // '>'
				i = (i + 1) % 8096;
				break;

			case 43: // '+'
				m[i]++;
				break;

			case 45: // '-'
				m[i]--;
				break;

			case 46: // '.'
				System.out.print((new Character((char) m[i])).toString());
				break;

			case 44: // ','
				throw (new RuntimeException("Not supported"));

			case 91: // '['
				if (m[i] == 0)
					j = ai[j];
				break;

			case 93: // ']'
				j = ai[j] - 1;
				break;
			}

		System.out.println();
	}*/
	
	public static String loadFile(File f) {
		
		try{
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			StringBuilder sb = new StringBuilder(); 
			while(br.ready()) {
				String l = br.readLine().toLowerCase().trim();
				for(int x = 0; x < l.length(); x++) {
					String s = l.substring(x, x+1);
					if(isSymbol(s)) {
						sb.append(s);
					}
				}
			}
			String s = sb.toString();
			return s;
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
			return null;
			
		}

	}
	
	public static boolean isSymbol(String s) {
		String symb = ".,[]<>-+";
		return symb.contains(s);
	}

	
}

/*
public static void runOld(String s) {
	int m[] = new int[30000];
	int a[] = new int[s.length()];
	Stack<Integer> st = new Stack<Integer>();

	for(int x = 0; x < s.length(); x++) {
		char c = s.charAt(x);
		switch(c) {
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

	st.clear();

	int curr = 0;

	for(int x = 0; x < s.length(); x++) {
		char c = s.charAt(x);
		switch(c) {
		
			case '>':
				curr = (curr + 1) % m.length;
				break;
			case '<':
				curr = (curr - 1) % m.length;
				break;
			case '+':
				m[curr] = (m[curr]+ 1)%256;
				break;
			case '-':
				m[curr] = (m[curr]- 1)%256;
				break;
			case '.':
				System.out.print((char)m[curr]);
				break;
			case ',':
				throw(new RuntimeException("Unsupported operation."));
			case '[':
				if(m[curr] == 0) { 
					x = a[x];
				}
				break;
			case ']':
				x = a[x] - 1;
				break;
		}
	}

	}
*/