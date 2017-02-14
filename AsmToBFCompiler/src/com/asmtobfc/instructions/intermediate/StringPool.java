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

package com.asmtobfc.instructions.intermediate;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** A pool of strings to reduce memory usage. */
public class StringPool {

	
	private static StringPool _instance = new StringPool();
	
	private StringPool() {
	}
	
	public static StringPool getInstance() {
		return _instance;
	}
	
	
	final String TMP_DIR = "c:\\temp\\bf";
	Map<String, String> strings = new WeakHashMap<String, String>();
	
	
	String getString(String s) {
		String result;
		
		result = strings.get(s);
		if(result == null) {
			strings.put(s, s);
			result = s;
		}
		return result;
	}
	
	
	File convertStringToFile(String s) {
		String uuid = UUID.randomUUID().toString();
		
		try {
			File f = new File(TMP_DIR+"\\bf-"+uuid);
			f.deleteOnExit();
			FileWriter fw = new FileWriter(f);
			fw.write(s.trim());
			fw.close();
			
			return f;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	String convertFileToString(File f) {
		try {
			FileReader fr = new FileReader(f);
			
			StringBuilder sb = new StringBuilder();
			char[] r = new char [1024];
			int c;
			do {
				c = fr.read(r, 0, r.length);
				if(c > 0) {
					sb.append(r, 0, c);
				}
			} while(c != -1);
			
//			f.delete();
			
			return sb.toString();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
