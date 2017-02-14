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

import java.util.Map;
import java.util.WeakHashMap;

import com.asmtobfc.instructions.IInstruction;

public class IntermediateFactory {

//	long cacheRequest = 0;
//	long cacheHit = 0;

	Map<IInstruction, IInstruction> _cache = new WeakHashMap<IInstruction, IInstruction>(
			10 * 1024 * 1024);

	// Map<ItrAddress, IGoto> _gotoCache = new WeakHashMap<ItrAddress, IGoto>(1024 * 1024);

	static IntermediateFactory _instance = new IntermediateFactory();

	private IntermediateFactory() {
	}

	public static IntermediateFactory getInstance() {
		return _instance;
	}

	public IInstruction createInstruction(IInstruction i) {
		IInstruction result = i;

		// if(i instanceof IGoto) {
		// IGoto g = (IGoto)i;
		//
		// result = _gotoCache.get(g.dest);
		// if(result == null) {
		// _gotoCache.put(g.dest, g);
		// result = g;
		// }
		// return result;
		//
		// }

		if (i instanceof IBF) {
			IBF bf = (IBF) i;

			if (bf.repetitions >= 2) {
				IBF returnInst = (IBF) _cache.get(bf);
				if (returnInst == null) {
					_cache.put(bf, bf);
					returnInst = bf;
				} 
				
				return returnInst;
			}

			if (bf.text.length() == 1) {
				char c = bf.text.charAt(0);
				switch (c) {
				case '>':
					return IBF.GT;

				case '<':
					return IBF.LT;

				case '+':
					return IBF.PLUS;

				case '-':
					return IBF.MINUS;

				case '[':
					return IBF.OP_SQ;

				case ']':
					return IBF.CL_SQ;

				case '.':
					return IBF.PERIOD;

				case ',':
					return IBF.COMMA;

				case '(':
					return IBF.OP_ROUND;

				case ')':
					return IBF.CL_ROUND;
				} // end switch

			}

			if (bf.text.equals("[-]")) {
				return IBF.CLEAR;
			}

		}

		return result;

	}

}
