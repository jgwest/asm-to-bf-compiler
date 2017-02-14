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

package com.asmtobfc.instructions.memory;

import com.asmtobfc.compiler.AsmMemoryAddress;
import com.asmtobfc.compiler.ItrAddress;
import com.asmtobfc.instructions.ICollection;
import com.asmtobfc.utility.BFCUtil;
import com.asmtobfc.utility.testutil.UtilTestCase;

public class MDownConvertToBinaryTest extends UtilTestCase {

	public void testDownConvToBinary() {
		
		final int DEST_SIZE = 20;
		
		AsmMemoryAddress a = createMemAddress(10);
		
		ItrAddress[] dest = new ItrAddress[DEST_SIZE];
		for(int x = 0; x < dest.length; x++) {
			dest[x] = new ItrAddress(30+x);
		}
		
		for(int num = 0; num < 2048; num++) {
			ICollection ic = new ICollection();
			ic.addInstruction(new MSet(a, BFCUtil.convInt(num)));
			ic.addInstruction(new MDownConvertToBinary(a, dest));
			
//			int tempVars = BFCLaunchUtil.calculateTempVarsReqd(ic);

			int tempVars = 100;
//			System.out.println(tempVars);
			
			int[] r = runWMemory(ic, tempVars, 100);
			
			System.out.print(num + " - ");
			
			for(int x = 0; x < dest.length; x++) {
				System.out.print(r[dest[x].getPosition()]);
			}
			System.out.println();			
			

			String s = Integer.toBinaryString(num);
			for(int x = 0; x < dest.length; x++) {
				
				int valShouldBe = 0;
				int strPos = s.length()-x-1; 
				if(strPos >= 0) {
					valShouldBe = s.charAt(strPos) == '1' ? 1 : 0;
				}
				
				assertTrue(r[dest[x].getPosition()] == valShouldBe);
				
			}
			
		}
		
		
		
	}
	
}
