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
package com.asmtobfc.utility;

import java.util.Arrays;

import com.asmtobfc.compiler.AsmMemoryAddress;

public class BFCUtil {
	
	public static final int ZERO_CONSTANT = 40000;
	
//	public static String newRepeat(String str, int r) {
//		if(r == 0) return "";
//		if(r == 1) return str;
//		
//		boolean oddNumber = r % 2 == 1;
//		if(oddNumber) {
//			r--;
//		}
//		
//		StringBuffer result = new StringBuffer(str + str);
//		
//		while(result.length() != r) {
//			result.append(result.toString());
//		}
//		
//		if(oddNumber) {
//			result.append(str);
//		}
//		
//		return result;
//		
//	}
	
	/** Repeat str r times*/
	public static String repeat(char c, int r) {
		
		char[] arr = new char[r];
        Arrays.fill(arr, c);
        return new String(arr);

		
//		StringBuilder sb = new StringBuilder(r);
//		for(int x = 0; x < r; x++) {
//			sb.append(str);
//		}
//		return sb.toString();
	}
	
	/** Convert an integer to the big mem equivalent int */
	public static int convInt(int x) {
		return x + ZERO_CONSTANT;
	}
	
	
	public static boolean isInt(String s){
		try {
			Integer.parseInt(s);
			return true;
		} catch(NumberFormatException nfe) {
			return false;
		}
	}

	/** Splits an integer into its decimal components */
	public static int[] split(int x) {
		int[] r = new int[6];
		
		int a = x;
		int n = 0;
		while(a != 0) {
			int b = a - ((a / 10) * 10);
			
			a = a/10;
			r[n] = b;
			n++;
		}
		
		return r;		
	}

	/** Prints the 'ma' memory address in mem */
	public static void printAtAddr(AsmMemoryAddress ma, int[] mem) {
		String str = "";
		for(int x = 0; x < 6; x++) {
			str = mem[ma.getField(x).getPosition()] + " " +str;
		}
		System.out.println((mem[ma.getSign().getPosition()] == 1 ? "-" : "+")+" "+ str);
	}

}
