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

import java.util.EmptyStackException;
import java.util.Stack;

public class InterpreterApplet {

	private synchronized void execute(String s, int ai[]) {
		byte abyte0[] = new byte[8096];
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
				abyte0[i]++;
				break;

			case 45: // '-'
				abyte0[i]--;
				break;

			case 46: // '.'
				System.out.print((new Character((char) abyte0[i])).toString());
				break;

			case 44: // ','
				throw (new RuntimeException("Not supported"));

			case 91: // '['
				if (abyte0[i] == 0)
					j = ai[j];
				break;

			case 93: // ']'
				j = ai[j] - 1;
				break;
			}

		System.out.println();
	}

	protected int[] parse(String s) {
		int ai[] = new int[s.length()];
		Stack<Integer> stack = new Stack<Integer>();
		for (int i = 0; i < s.length(); i++)
			switch (s.charAt(i)) {
			case 92: // '\\'
			default:
				break;

			case 91: // '['
				stack.push(new Integer(i));
				break;

			case 93: // ']'
				try {
					int j = ((Integer) stack.pop()).intValue();
					ai[j] = i;
					ai[i] = j;
					break;
				} catch (EmptyStackException _ex) {
					System.out.println("Parse error:\nto many right brackets: " + i);
				}
				return null;
			}

		if (!stack.isEmpty()) {
			System.out.println("Parse error:\nmissing right bracket.");
			return null;
		} else {
			return ai;
		}
	}

	public void run(String in) {
		String s = strip(in);
		if (s != null) {
			int ai[] = parse(s);
			if (ai != null)
				execute(s, ai);
		}
	}

	protected String strip(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++)
			switch (s.charAt(i)) {
			case 43: // '+'
			case 44: // ','
			case 45: // '-'
			case 46: // '.'
			case 60: // '<'
			case 62: // '>'
			case 91: // '['
			case 93: // ']'
				stringbuffer.append(s.charAt(i));
				break;

			default:
				if (!Character.isWhitespace(s.charAt(i))) {
					System.out.println("Parse error:\nIllegal character: " + s.charAt(i) + " at index: " + i + " ascii: " + (int) s.charAt(i));
					return null;
				}
				break;
			}

		return stringbuffer.toString();
	}

}
