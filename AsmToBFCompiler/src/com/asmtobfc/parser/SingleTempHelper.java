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

package com.asmtobfc.parser;

import java.util.ArrayList;
import java.util.List;

public class SingleTempHelper {
	private static final SingleTempHelper instance = new SingleTempHelper();
	
	List<Integer> tempList = new ArrayList<Integer>();
	
	private SingleTempHelper() {
	}
	
	public static SingleTempHelper getInstance() {
		return instance;
	}
	
	public void add(int i) {
		tempList.add((Integer)i);
	}
	
	
	public int calculateClosestTo(int i) {
		int closestDistance = Integer.MAX_VALUE;
		int closestInt = -1;
		
		for(Integer x : tempList) {
			int range = Math.abs((i - x));
			if(range < closestDistance) {
				closestDistance = range;
				closestInt = x;
			}
		}
		
//		System.out.println("calculateClosestTo"+" i:"+i+"  result:"+closestInt);
		
		return closestInt;
	}
}
