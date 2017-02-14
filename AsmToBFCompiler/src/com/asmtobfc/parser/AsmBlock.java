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

import java.util.ArrayList;

/** A block is a list of instructions. */
public class AsmBlock {	
	private ArrayList<AsmParserInstruction> ainst = null;
	private String name = null;
	private AsmBlock next = null;
	private boolean blockBranchTarget = false;
	
	AsmBlock(String name) {
		this.ainst = new ArrayList<AsmParserInstruction>();
		this.name = name;
	}

	public ArrayList<AsmParserInstruction> getAInst() {
		return ainst;
	}

	public void setAInst(ArrayList<AsmParserInstruction> ainst) {
		this.ainst = ainst;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AsmBlock getNext() {
		return next;
	}

	public void setNext(AsmBlock next) {
		this.next = next;
	}
	
	
	public String toString() {
		return "{ ("+name+")"+(blockBranchTarget ? "!" : "")+"   "+ainst+"} -> " + (next != null ? next.getName() : "null"); 
	}
	
	/** Is this block the target of a branch instruction in another part of the program. If a block was
	 * created by splitting another existing block, it is necessarily not a target.  */
	public boolean isBlockBranchTarget() {
		return blockBranchTarget;
	}
	
	/** Set whether this block is a target of a branch instruction in another part of the program */
	public void setBlockBranchTarget(boolean blockBranchTarget) {
		this.blockBranchTarget = blockBranchTarget;
	}
}
