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

package com.asmtobfc.instructions;

import java.io.IOException;
import java.io.OutputStream;

import com.asmtobfc.compiler.CompilerStatus;

public interface IInstruction {
	
	/** This method decomposes the instruction, but does not change the state of the instruction object. */
	public IInstruction decompose(CompilerStatus cs);
	
	public boolean canDecompose();
	
	public void addInstruction(IInstruction i);
	public IInstruction getInstruction(int x);
	public void replaceInstruction(int x, IInstruction i);
	
	public int size();
	
	public String toBF();
	
	public void toBF(StringBuilder b);
	
	public void toBF(OutputStream os) throws IOException ;
}
