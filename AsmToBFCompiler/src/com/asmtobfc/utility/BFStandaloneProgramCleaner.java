package com.asmtobfc.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** 
 * This standalone program optimizes program size of a given BF program by removing inefficient
 * use of opposite instructions, for example:
 * 
 * 	+ followed by -
 *  - followed by +
 *  > followed by <
 *  < followed by >
 *  
 * Testing reveals this reduces program size by about 4%. 
 **/
public class BFStandaloneProgramCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File inputFile = new File("d:\\out.bf");
		File outputFile = new File("d:\\out-cleaned.bf");
		
		try {
			FileWriter fw = new FileWriter(outputFile);
			FileReader fr = new FileReader(inputFile);
			
			int lastChar = -1;
			int numLastCharSeen = 0;
			
			int val;
			int charsProcessed = 0;
			while(-1 != (val = fr.read())) {
				
				char c = (char)val;
				
				if(lastChar == -1) {
					lastChar = c;
					numLastCharSeen++;
				} else {
					
					if(lastChar == c) {
						numLastCharSeen++;
					} else {
						
						if(isOpposite((char)lastChar, c)) {
							numLastCharSeen--;
							if(numLastCharSeen < 0) {
								numLastCharSeen = Math.abs(numLastCharSeen);
								lastChar = c;
							}
						} else {
							for(int y = 0; y <numLastCharSeen; y++) {
								fw.write(lastChar);
							}
							lastChar = c;
							numLastCharSeen = 1;							
						}
												
					}
										
				}
				
				charsProcessed++;
				
			}
			
			if(lastChar != -1 && numLastCharSeen > 0) {
				for(int y = 0; y <numLastCharSeen; y++) {
					fw.write(lastChar);
				}				
			}
			
			fw.close();
			fr.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

	
	private static boolean isOpposite(char c1, char c2) {
		if(c1 == '+' && c2 == '-') { return true; };
		if(c1 == '-' && c2 == '+') { return true; };
		if(c1 == '<' && c2 == '>') { return true; };
		if(c1 == '>' && c2 == '<') { return true; };
		
		return false;
		
	}
}
