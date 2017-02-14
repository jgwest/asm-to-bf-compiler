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
package com.asmtobfc.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.asmtobfc.utility.BFCUtil;

/** Parse assembly language file into blocks, constants, and defines, for use by the compiler. */
public class AsmParser {
	
	private HashMap<String /* name*/, Integer /* register #*/> defines = new HashMap<String, Integer>();
	private HashMap<String /* label*/, Integer> constants = new HashMap<String, Integer>();
	
	private HashMap<String, AsmBlock> blocksMap = null;
	private ArrayList<AsmBlock> result = null;
	
	public HashMap<String, Integer> getConstants() {
		return constants;
	}

	public HashMap<String, Integer> getDefines() {
		return defines;
	}

	public HashMap<String, AsmBlock> getBlocksMap() {
		return blocksMap;
	}

	public ArrayList<AsmBlock> getResult() {
		return result;
	}

	public AsmParser(String fileName) {
		File f = new File(fileName);
		ArrayList<String> a = parseFile(f);
		ArrayList<AsmParserInstruction> ai = new ArrayList<AsmParserInstruction>();
		
		for(int x = 0; x < a.size(); x++) {
			// First Item is the instruction
			String action = a.get(x);
			ArrayList<String> i = new ArrayList<String>();
			
			// Additional items after the first instruction are parameters, terminated by null
			x++;
			while(a.get(x) != null) {
				i.add(a.get(x));
				x++;
			}

			// Convert to instruction object
			AsmParserInstruction b = new AsmParserInstruction(action.trim().toLowerCase(), i.toArray(new String[i.size()]));
			ai.add(b);
		}
		
		// ------------------------
		// Strip constants, defines.
		
		for(int x = 0; x < ai.size(); x++) {
			AsmParserInstruction i = ai.get(x);
			
			if(i.getAction().startsWith("'")) {
				ai.remove(x);
				x--;
				continue;
			}

			// ex: #define temp2 r5
			if(i.getAction().equalsIgnoreCase("#define")) {
				defines.put(i.getParams()[0], new Integer(i.getParams()[1].trim().substring(1)));
				ai.remove(x);
				x--;
				continue;
			}
			
			// 
			if(i.getAction().equalsIgnoreCase("label") && x + 1 < ai.size() && BFCUtil.isInt(ai.get(x+1).getAction())) {
				constants.put(i.getParams()[0], Integer.parseInt(ai.get(x+1).getAction()));
				ai.remove(x);
				ai.remove(x);
				x--;
				continue;
			}	
		}
		
		// Step 1 - Initial split into blocks
		// 	- split based only on label
		
		ArrayList<AsmBlock> blocks = new ArrayList<AsmBlock>();

		AsmBlock currBlock = new AsmBlock("initial");
		currBlock.setBlockBranchTarget(true);
		blocks.add(currBlock);
		
		for(int x = 0; x < ai.size(); x++) {
			AsmParserInstruction i = ai.get(x);
			
			if(i.getAction().equalsIgnoreCase("label")) {
				AsmBlock newBlock = new AsmBlock(i.getParams()[0]);
				newBlock.setBlockBranchTarget(true);
				blocks.add(newBlock);
				
				currBlock.setNext(newBlock);
				currBlock = newBlock;
			} else {
				currBlock.getAInst().add(i);
			}
		}

		// Output result to screen
		System.out.println("----------------");		
		int z = 0;
		for(AsmBlock q : blocks){
			System.out.println(z+")"+q);
			z++;
		}

		// Step 2 - Second split of blocks
		// 
		
		// For each block...
		for(int x = 0; x < blocks.size(); x++) {
			AsmBlock b = blocks.get(x);
			ArrayList<AsmParserInstruction> ba = b.getAInst();
			
			// For each of the instructions in this block...
			for(int y = 0; y < ba.size(); y++) {
				AsmParserInstruction i = ba.get(y);
				
				// Split on JumpLTE, unless JumpLTE is alone in the block already, or if it last in the block
				if(i.getAction().equalsIgnoreCase("jumplte") 	&& 	(ba.size() > 1) && 	(y+1 != ba.size())	) {

					// Everything after the JumpLTE is required to be in its own block, so split
					splitBlock(blocks, x, y+1);
					x = 0; // Reset outer for to 0
					
					// Output result of split
					System.out.println("----------------");		
					z = 0;
					for(AsmBlock q : blocks) {
						System.out.println(z+")"+q);
						z++;
					}

					break;
				}
				
				// Split on Load/Store, unless instruction is alone in the block already, or if it is the last in the block
				if((i.getAction().startsWith("load") || i.getAction().startsWith("store")) 	&& 	(ba.size() > 1)	 && 	(y+1 != ba.size())	) {
					
					// Everything after the Load*/Store* is required to be in its own block, so split 
					splitBlock(blocks, x, y+1);
					x = 0; // Reset outer for to 0
					
					// Output result of split
					System.out.println("----------------");		
					z = 0;
					for(AsmBlock q : blocks){
						System.out.println(z+")"+q);
						z++;
					}

					break;
				}
				
				// Corner case: If LoadI is the last instruction of a block, or the only instruction of a block, we need to insert an empty
				// block for future use later in the process.
				if( i.getAction().startsWith("loadi") && 	( (ba.size() == 1)	 || 	(y+1 == ba.size())	)	) {
					
					// If this is NOT the last block, AND the next block is NOT empty
					if(blocks.size() > x+1 && blocks.get(x+1).getAInst().size() != 0) {
						AsmBlock nextBlock = blocks.get(x+1);
						
						// We only need to add an empty block if the next block is a target of a branch instruction elsewhere,
						// otherwise the corner case is not an issue
						if(nextBlock.isBlockBranchTarget()) {
							insertEmptyBlock(blocks, x);
						}
					}
				}

			}
		}
		result = blocks;
		
		blocksMap = new HashMap<String, AsmBlock>(); 
		for(int x = 0; x < blocks.size(); x++) {
			AsmBlock b = blocks.get(x);
			blocksMap.put(b.getName().toLowerCase(), b);
		}
		
	}
	
	private static void insertEmptyBlock(ArrayList<AsmBlock> blocks, int posPrevBlock) {
		AsmBlock prevBlock = blocks.get(posPrevBlock);
		
		AsmBlock n = new AsmBlock(nextName(prevBlock.getName()));
		
		n.setNext(prevBlock.getNext());
		prevBlock.setNext(n);
		
		blocks.add(posPrevBlock+1, n);
	}
	
	private static void splitBlock(ArrayList<AsmBlock> blocks, int posOfBlockInArray, int linePosInBlock) {
		AsmBlock newblock = splitBlock(blocks.get(posOfBlockInArray), linePosInBlock);
		blocks.add(posOfBlockInArray+1, newblock);
		
	}
	 
	/** Affects both the block that is passed in as a parameter, and the resulting block. Line will end up being the first line of the second block.*/
	private static AsmBlock splitBlock(AsmBlock ab, int line) {
		AsmBlock n = new AsmBlock(nextName(ab.getName()));
		
		// Copy the lines from the existing block
		ArrayList<AsmParserInstruction> ba = ab.getAInst();
		for(int x = line; x < ba.size(); x++) {
			AsmParserInstruction i = ba.get(x);
			n.getAInst().add(i);
		}
		
		// Remove the lines from the previous block
		int basize = ba.size();
		for(int x = line; x < basize; x++) {
			ba.remove(line);
		}
		
		// Update references
		n.setNext(ab.getNext());
		
		ab.setNext(n);
		
		return n;
	}
	
	private static String nextName(String prevName) {
		
		if(prevName.contains("[") && prevName.contains("]")) {
			
			int x = Integer.parseInt(prevName.substring(prevName.indexOf("[")+1, prevName.indexOf("]")));
			return prevName.substring(0, prevName.indexOf("[")) + "["+(x+1)+"]";
			
		} else {
			return prevName +"[1]";
		}
	}

	/** Returns a list of Strings that represent the program, where each string is a token, and each line is terminated by a null. 
	 * ex: 		LoadI r3 r0 ' r3 = m[x]
	 * becomes [LoadI] [r3] [r0] [null] */
	private static ArrayList<String> parseFile(File f) {
		ArrayList<String> al = new ArrayList<String>();
		
		try {

			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			while(br.ready()) {	
				String str = br.readLine();
				String[] toks = str.split(" ");
				boolean isValid = false;
				for(int x = 0; x < toks.length; x++) {
					String t = toks[x].trim().toLowerCase();
					
					// Skip comments
					if(t.startsWith("'")) {
						break;
					}
					if(t.startsWith("//")) {
						break;
					}
					
					if(t.length() == 0) continue;
					al.add(t);
					
					isValid = true;
				}
				if(isValid) {
					al.add(null);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
		return al;
	}
	
}
