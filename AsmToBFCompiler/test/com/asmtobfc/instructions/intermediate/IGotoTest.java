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

package com.asmtobfc.instructions.intermediate;

import junit.framework.TestCase;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.instructions.IInstruction;
import com.asmtobfc.instructions.intermediate.IGoto;

public class IGotoTest extends TestCase {

	public void testGoto() {
		ICollection ic = new ICollection();
		
		IGoto ig = null;
		
		ItrAddress a = new ItrAddress(2, "a");
		ItrAddress b = new ItrAddress(4, "b");
		ItrAddress c = new ItrAddress(6, "c");
		ItrAddress d = new ItrAddress(8, "d");
		
		ig = new IGoto(a); ic.addInstruction(ig);
		ig = new IGoto(b); ic.addInstruction(ig);
		ig = new IGoto(c); ic.addInstruction(ig);
		ig = new IGoto(d); ic.addInstruction(ig);
		ig = new IGoto(c); ic.addInstruction(ig);
		ig = new IGoto(b); ic.addInstruction(ig);
		ig = new IGoto(a); ic.addInstruction(ig);

		CompilerStatus cs = new CompilerStatus(10);
		IInstruction program = ic.decompose(cs);
		assertTrue(program.toBF().equalsIgnoreCase(">>>>>>>><<<<<<"));
		
		ic = new ICollection();
		ig = new IGoto(d); ic.addInstruction(ig);
		ig = new IGoto(a); ic.addInstruction(ig);
		ig = new IGoto(d); ic.addInstruction(ig);
		
		cs = new CompilerStatus(10);
		program = ic.decompose(cs);
		assertTrue(program.toBF().equalsIgnoreCase(">>>>>>>><<<<<<>>>>>>"));

		ic = new ICollection();
		ig = new IGoto(b); ic.addInstruction(ig);
		ig = new IGoto(a); ic.addInstruction(ig);
		ig = new IGoto(c); ic.addInstruction(ig);
		ig = new IGoto(d); ic.addInstruction(ig);
		ig = new IGoto(b); ic.addInstruction(ig);
		ig = new IGoto(d); ic.addInstruction(ig);
		
		cs = new CompilerStatus(10);
		program = ic.decompose(cs);
		assertTrue(program.toBF().equalsIgnoreCase(">>>><<>>>>>><<<<>>>>"));

	}
	
}
