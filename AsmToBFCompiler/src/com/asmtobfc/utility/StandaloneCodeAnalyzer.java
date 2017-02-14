/*
	Copyright 2012, 2013 Jonathan West

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

package com.asmtobfc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;

/** Standalone utility: Allows the analysis of annotations in the code 
 * in order to determine the size (in BF)of instructions. Useful for trimming
 * down instruction implementations. 
 */
public class StandaloneCodeAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File f = new File("d:/out.bf");
		
		Stack<KeyInfo> currActive = new Stack<KeyInfo>();

		try {
			FileInputStream fis = new FileInputStream(f);
			
			int c;
			
			long charsSeen = 0;
			
			byte[] b = new byte[1024 * 256];
			
			String currKey = null;
			
			while (-1 != (c = fis.read(b))) {
				
				for(int x = 0; x < c; x++) {

					char ch = (char)b[x];
					
					if(ch == '>' || ch == '<' || ch == '.' || ch == ',' ||
							ch == '+' || ch == '-' || ch == '[' || ch == ']' ||
							ch == '(' || ch == ')') {
						charsSeen++;
					} else {
						
						if(ch == '{') {
							KeyInfo ki = new KeyInfo();
							ki.key = currKey;
							ki.startPos = charsSeen;
							currActive.push(ki);
							
							currKey = null;
							
						} else if(ch == '}') {
							KeyInfo ki = currActive.pop();
							System.out.println("["+ki.key + "] - " + (charsSeen - ki.startPos));
							
							
						} else { 
							if(currKey == null) {
								currKey = "";
							}
							currKey += ch;

						}
						
					}

					
				}
				
				
			}
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


class KeyInfo {
	String key;
	long startPos;
}