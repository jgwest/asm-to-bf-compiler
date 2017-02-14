/*
	Copyright 2011, 2013 Jonathan West

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

import java.util.HashMap;
import java.util.Map;

public class BFCStringUtil {
	
	static Map<Integer, String> LTMap = new HashMap<Integer, String>();
	static Map<Integer, String> GTMap = new HashMap<Integer, String>();
	static Map<Integer, String> PlusMap = new HashMap<Integer, String>();
		
	static long total = 0;
	static long hit = 0;
	
	public static String genRepeat(char c, int count) {
		
		// Disable BFCStringUtil repeater
		if(1 == 1) return BFCUtil.repeat(c, count);
		
		if(count > 1024) {
			return BFCUtil.repeat(c, count);
		}
		
		String result = null;
		Map<Integer, String> map = null;
		
		switch(c) {
		case '<':
			map  = LTMap;
			break;
		case '>':
			map = GTMap;
			break;
		case '+':
			map = PlusMap;
			break;
		default:
			return BFCUtil.repeat(c, count);
		}
		
		if(map.containsKey(count)) {
			return map.get(count);
		}
		
		result = BFCUtil.repeat(c, count);
		
		map.put(count, result);
		
		return result;
	}
	
}
