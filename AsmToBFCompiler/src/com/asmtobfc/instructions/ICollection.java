/*
	Copyright 2011, 2012 Jonathan West

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.asmtobfc.compiler.CompilerStatus;
import com.asmtobfc.instructions.intermediate.IBF;

public class ICollection implements IInstruction {
	List<IInstruction> instructions = null;

	public ICollection() {
		instructions = new ArrayList<IInstruction>(10);
	}

	public void addInstruction(IInstruction i) {
		instructions.add(i);
	}

	public boolean canDecompose() {
		for (IInstruction ii : instructions) {
			if (ii.canDecompose()) {
				return true;
			}
		}
		return false;
	}

	
	public IInstruction decompose(CompilerStatus cs) {

		ICollection r = new ICollection();
		for (int x = 0; x < instructions.size(); x++) {
			r.addInstruction(instructions.get(x));
		}

		while (r.canDecompose()) {
			for(ListIterator<IInstruction> it = r.instructions.listIterator(); it.hasNext();) {
				IInstruction ii = it.next();

				while (ii.canDecompose()) {
					ii = ii.decompose(cs);
					if(ii != null) {
						it.set(ii);
//						r.replaceInstruction(x, ii);
					} else {
						it.remove();
//						r.removeInstruction(x);
//						x--;
						break;
					}
				}

			}
		}

		List<IInstruction> result = compressCollections(r.instructions, 200);
//		result = compressIBF(result, 100);

		r.instructions.clear();
		r.instructions = result;

		return r;
	}

	
//	public IInstruction decompose2(CompilerStatus cs) {
//		depth++;
//
//		ICollection r = new ICollection();
//		for (int x = 0; x < instructions.size(); x++) {
//			r.addInstruction(instructions.get(x));
//		}
//
//		while (r.canDecompose()) {
//			for (int x = 0; x < r.size(); x++) {
//				IInstruction ii = r.getInstruction(x);
//
//				// if(depth >= 1 && depth <= 1) {
//				// System.out.println("["+depth+"] "+ii);
//				// }
//
//				// if(depth == 3) {
//				// System.out.println("------");
//				// System.out.println(ii);
//				// }
//				while (ii.canDecompose()) {
//					// System.out.println("decomposing... "+ii);
//					ii = ii.decompose(cs);
//					if(ii != null) {
//						r.replaceInstruction(x, ii);
//					} else {
//						r.removeInstruction(x);
//						x--;
//						break;
//					}
//				}
//				// System.out.println("-----");
//			}
//		}
//
//		List<IInstruction> result = compressCollections(r.instructions, 200);
//		// result = compressIBF(result, 100);;
//
//		r.instructions.clear();
//		r.instructions = result;
//
//		depth--;
//		return r;
//	}

	private static final List<IInstruction> compressIBF(List<IInstruction> i,
			int targetSizeParam) {

		List<IInstruction> result = new ArrayList<IInstruction>(targetSizeParam+1);
		int targetSize = targetSizeParam;

		StringBuilder lastBFString = null;

		for (Iterator<IInstruction> it = i.iterator(); it.hasNext();) {
			IInstruction inst = it.next();
			if (inst instanceof IBF && ((IBF) inst).getRepetitions() <= 0) {
				IBF bf = (IBF) inst;
				if (lastBFString != null) {
					// continuation of bf parade
					lastBFString.append(bf.toBF());
				} else {
					// first in bf parade
					lastBFString = new StringBuilder(bf.toBF());
				}
			} else {
				if (lastBFString != null) {
					// End of current bf parade
					result.add(new IBF(lastBFString.toString()));
					// System.out.println("lastBFString:"+lastBFString);
					lastBFString = null;
				}

				result.add(inst);

			}

		}

		if (lastBFString != null) {
			// End of current bf parade
			result.add(new IBF(lastBFString.toString()));
		}

		return result;

	}

	private static final List<IInstruction> compressCollections(
			List<IInstruction> i, int targetSizeParam) {

		List<IInstruction> result = new ArrayList<IInstruction>(
				targetSizeParam + 1);

		int targetSize = targetSizeParam;

		int totalShallowInstructions = 0;
		for (Iterator<IInstruction> it = i.iterator(); it.hasNext();) {
			IInstruction inst = it.next();
			totalShallowInstructions++;
		}
		targetSize -= totalShallowInstructions;

		for (Iterator<IInstruction> it = i.iterator(); it.hasNext();) {
			IInstruction inst = it.next();
			if (inst instanceof ICollection) {
				ICollection c = (ICollection) inst;
				if (c.instructions.size() < targetSize) {
					result.addAll(c.instructions);
					targetSize -= c.instructions.size();
				} else {
					result.add(c);
				}

			} else {
				result.add(inst);
			}
		}

		return result;

	}

	public void clear() {
		instructions.clear();
	}

	public void replaceInstruction(int x, IInstruction i) {
		instructions.set(x, i);
	}

	public void removeInstruction(int x) {
		instructions.remove(x);
	}

	
	public IInstruction getInstruction(int x) {
		return instructions.get(x);
	}

	public int size() {
		return instructions.size();
	}

	public String toBF() {
		if (canDecompose()) {
			throw (new UnsupportedOperationException());
		}
		StringBuilder sb = new StringBuilder();

		for (int x = 0; x < instructions.size(); x++) {
			IInstruction i = getInstruction(x);
			sb.append(i.toBF());
		}

		return sb.toString();
	}

	public void toBF(StringBuilder sb) {

		for (int x = 0; x < instructions.size(); x++) {
			IInstruction i = getInstruction(x);
			i.toBF(sb);
		}

	}

	public void toBF(OutputStream os) throws IOException {
		for (int x = 0; x < instructions.size(); x++) {
			IInstruction i = getInstruction(x);
			i.toBF(os);
		}
	}

	public String toString() {
		String r = "";
		for (int x = 0; x < size(); x++) {
			IInstruction i = getInstruction(x);
			r += i.toString() + "\n";
		}
		return r;
	}

}
