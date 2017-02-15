## Introduction

This is a Java-based compiler that converts programs that are written in a minimal register-and-heap-based assembly language instruction set, into an even simpler Turing-complete "bytecode-like" program ("BF"), which can be interpreted in a specialized interpreter. The purpose of the program is to prove whether or not a register and heap-based system (a simplified version of a modern computer architecture) could be fully mapped into a different and drastically simplified instruction set.

This simplified instruction set contains only 8 instructions (each designated as a single character <>.,+-), and these instructions do not contain any operands:

This simplified instruction set contains only 8 instructions (each designated as a single character <>.,+-), and these instructions do not contain any operands:

* `>` = move forward in memory
* `<` = move backwards in memory
* `+` = increment the current memory value
* `-` = decrement the current memory value
* `.` = output a character
* `,` = read a character
* `[` = beginning of loop, jump if zero,
* `]` = end of loop, return to beginning of loop 

The higher level instruction set, from the translation occurs, looks like this:

	#define r_x r0
	#define r_totalSum r1
	#define r_numValues r2
	
	label LABEL_num_values_addr 
	2048
	
	
	label loop_start
	
	        LoadI r_temp1 r_x
	
	        Add r_totalSum r_totalSum r_temp1
	
	        Add  r_x  r_x  r_one
	
	        Add r_temp1 r_x r_one
	
	        Load r_temp2 LABEL_num_values_start_addr
	        
	        Add r_temp2 r_temp2 r_numValues
	
	        JumpLte r_temp1 r_temp2 loop_start
	
	(...etc...)

The challenge was stretching the models of these two entirely different languages into one another to create a workable compiler.

The program itself decomposes the high level instructions (the registers and heaps), into low level instructions (the above symbols) in three steps: high level => medium level (represented by classes beginning with 'M') medium => intermediate level (represented by classes beginning with 'I') intermediate => (simple translation of 'I' classes) 



### History

The genesis of this project came in my final year of university while completing a Computer Architecture course. As part of this course, students were asked to implement a set of programs running on a simulated CPU, with a limited registered-based assembly-language instruction set. Those ASM instructions that are natively supported by the BF compiler are the ones that were supported by this simulated CPU. These instructions are a minimal set: some instructions are not present, as these instructions can be implemented using the existing instructions in the minimal set. For example, there is no GOTO (branch) instruction; rather one can use the JUMPLTE instruction with the first and second operand as the same parameter. This ensures that the jump will always succeed as a == a is a tautology and is true for any a. The final set of supported instructions were two loads, two stores, add/sub/mult, and JumpLte. Using various combinations of these one can express other instructions like JumpEq/JumpGte/JumpGt/JumpLt, and the various loop/branch types that these support.

It was as a result of completing the work for this course that I now had in my possession a number of simple programs that are written in this existing simple instruction set, which made it a obvious target for reuse in this project. [[The complexity inherent in translating a register and heap-based assembly-like language would be neither increased nor decreased by an increased number of instructions.]] It is the fundamental model mismatch between BF and traditional assmebly languages that is the central challenge of this project, not the specific expression of either language representation. 

### The Target Language ###

For many years, BF has been used the world over by nerds as a language that stretches one's individual capacity to develop useful applications (hence the official name). Inherent limitations of BF require a creative and careful approach to program design, even while the language itself is amenable to traditional procedural programming (albeit without formal procedures).

The complexity of the language is manifested in a few important ways:

- Incomplete branching/loop instructions, only jump on zero within a loop
- Movement through program memory and assigning values within memory, are extremely verbose which can produce large programs. (For example, setting memory position 50 to value 50 takes 100 separate instructions.)
- No support for functions
- No support for pointer referencing/dereferencing
- Memory cursor position is entirely relative with no direct support for absolute positioning
- Memory and program space are entirely separate
- Limited support for representation of negative/positive integers

Many of these, such as lack of support for functions, lack of pointer support, and memory positioning limitations are key architectural elements in the language design, requiring explicit support for these features in the design of the compiler itself.


### The Source Language: Assembly Instructions ###

The assembly language instructions of the virtual machine are as follows:

	add (register A) (register B) (register C)
		- val(A) + val(B) is stored in C
	
	sub (register A) (register B) (register C)
		- val(A) - val(B) is stored in C
	
	jumpgte (register A) (register B) (label)
		- if val(A) >= val(B) then jump to label, otherwise continue to next instruction
	
	jumpeq (register A) (register B) (label)
		- if val(A) == val(B) then jump to label, otherwise continue to next instruction
	
	jumplte (register A) (register B) (label)
		- if val(A) <= val(B) then jump to label, otherwise continue to next instruction
	
	load (register A) (label)
		- load the value stored at label into the specified register
	
	loadi (register A) (register B) 
		- register B contains a value representing a position in memory, retrieve a value from that position in memory, and store it in A	
	
	store (register A) (label)
		- store the value stored at label into register A
	
	storei (register A) (register B)
		- register A contains a value representing a position in memory; store the value of B in that position in memory
	
	halt
		- halt the application.


The virtual machine that runs these instructions has 16 registers (r0 to r15), and an arbitrary amount of memory storing 16-bit signed integer values. Programs may use labels to jump to specific points in the program, or to retrieve integer constants from the program.


### Compiler Layers ###

The compiler was developed to operate on a numbrer of distinct layers. Transformation of code from a higher-layer to a lower-layer is called 'decomposition'.

In order to support compilation of an assembly-like language into BF, the compiler iteratively decomposes the code through a number of layers. The first layer is the assembly language layer, and is known as the ___ASM Instruction layer___. On this layer, source files are parsed and converted into a POJO representation of their instructions values. Example: On this layer ```"Add r_totalSum r_totalSum r_temp1"``` is converted to an MAdd instruction, with ```r_totalSum``` and ```r_temp1``` as operands. This instruction will add the contents of ```r_totalSum``` and ```r_temp1```, and store the result in ```r_totalSum```.

The second layer is the __Multi-cell Instruction layer__. Registers are mapped to positions in memory, and instructions are mapped to a memory model that represents positive and negative values using multiple cells of BF memory. A cell is a single 'unit' of data (eg a byte) in the BF heap, which is the fundamental storage primitive of BF.The number of cells that are contained in each unit of memory affect the range of integers that can be stored at that cell. For example, with 4 cells we could store (0-9), (0-9), (0-9), (0-9), or any integer from 0 to 9999.

The third layer is the __Single Cell Instruction layer__. Here, each of the instructions that target the multi-cellular memory are converted into a series of instructions that operate on single cells. This layer implements the machinery of multi-cellular memory using the single-cell form. [Example needed]. The size of a cell is not canonically defined, so we assume a minimum of -128 and a maximum of 127. An example of a single cell instruction is ICopy, which copies one cell of memory to another (ICopy: src -> dest)

The fourth and final layer are the raw BF instructions ( < > + - , . [ ] ). Single cell instructions are processed and decomposed into BF, ready to be run by any BF interpreter. 

The instructions of every layer implement the IInstruction interface. This interface implements the composite design pattern, allowing for instructions to act as both instruction containers (parents, see ICollection) and concrete instructions (children).

The Java implementation of instructions of every layer implement the IInstruction interface.

## Layers and Memory Representation ##

### Asm Memory Layer ###
The pseudo-assembly language operates on a flat contiguous array of 16 bit signed integers.


### Multi-cell instruction layer ###

While BF has existed since 1993, aspects of it have never been standardized. There exists no formal/informal standard of the size of a single addressible "cell" of BF memory: BF intepreters will use 0-255, -128 to 127, and various signed/unsigned 32-bit values. We cannot rely on any specific cell size in our compiler. If we want to represent numbers greater 0 to 127, we must use multiple cells chained-together. The BF compiler current supports combining seven cells together to represent a single 16-bit value, where the first cell is a sign bit (0 or 1), and the remaining cells contain decimal values from 0 to 9 (inclusive). The ordering of cells after the sign cell is most-significant first (big-endian). These seven cells in aggregate are referred to as a "asm memory address", because this is the layer at which the assembly code acts, and whcih seek to simulate the single contigous space used by asm.

The purpose of the the sign cell  is only an indicator for optimization purposes, as the actual contents of the 6 value cells are stored as an unsigned integer. 

Simulating 16-bit values requires the ability to store a range of integers form -32768 to -32767. This is accomplished by setting the "zero" value at 40000, and representing the 16-bit range as (40,000-32,768) to (40,000+32,768), or 7232 to 72768. 

Instructions that operate at the multi-cell layer are: MAdd, MCopy, MDoubleDownConvert, MDownConvert, MDownConvertToBinary, MEqual, MGt, MGte, MLt, MMove, MMult, MPrint, MPrintA, MPrintString, MSet, MSub.


### Single Cell Layer ###

The intermediate layer contains instructions that act on individual BF cells, rather than on multiple cells as is the case with the higher-layer memory layer. 

In the compiler source code all classes that implement these instructions begin with an 'I', this is not to be confused with interface classes which also begin with an I in this codebase (in practice these two are rarely confused).

Some single cell instructions are simple wrappers over BF instructions, with some additional syntatic sugar for the programmer:

- ISet, IMove, IClear, ICopy, IInc / IDec, IGoto, IPrint
- All of these move to a specific position in memory, and then perform an action (set a constant, increment, decrement, print, etc)

Addition and subtract are slightly more complex:

- IDAdd / IDSub
- These operate on two given values (0 to 127), and store the result in the first value.

Equality checking instructions exist for all the expected value comparisons:

- IBitCheckIfElse
- IDEqual / IEqual
- IDGtle / IGT / ILT (Greater than / less than)
- There are no IGTE or ILTE, because x >= y can be rewritten as x + 1 > y, likewise for LT

The branching instructions are fairly complex:

- IBinaryRange [Describe]
- IJumpNZ (Not to be confused with jump on zero already in BF)

Additonal miscellaneous instructions:

- IPrintVal
- (Print the integer value at a cell; useful for debugging)

Some instructions contain both destructive and non-destructive variants, as indicated by a capital D prefix in the instruction name above. The destructive variant will irrevocably alter the contents of inputs provided to the instruction, while the non-destructive variant will preserve the input values. For instance, IDEqual is the destructive version of IEqual. IDEqual takes two values as inputs, and once complete this instruction will have altered the contents of both. One should therefore not call the destructive equal on addresses which must be used elsewhere in the program. The counterpart to IDEqual is IEqual, which acts as a wrapper around IDEqual, by making temporary copies of the inputs before passing those values to IDEqual.

The destructive variant exists because it is always more efficient than the non-destructive variant. The non-destructive instruction must utilize additional BF stack memory and CPU cycles to create a copy of the values to be provided, while the destructive variant does not. 


### Sample Single Cell Layer Instruction ###

IDAdd(ItrAddress left, ItrAddress right):

- Addition (Destructive variant)
- input is left and right values
- result of addition will be stored in the left value.

- The IDAdd instruction is a collection of instructions, with child instructions implementing the addition functionality. 
- Each IDAdd instruction has this structure:


		ICollection() {			// the entire instruction is contained inside a collection
			IReturnPush()		// the starting position of the instruction is stored in the stack (this is a meta instruction is used by the compiler)
			IGoto(right)		// goto the position of the right operand
			IBF("[")			// while(right is non-zero)
				IInc(left)		// 		add 1 to left
				IDec(right)		// 		sub 1 from right
			IBF("]")			// 		goto top of loop
			IReturnPop()		// pop the starting position from the stack, and return memory cursor to that position (this meta instruction is used by the compiler)
		}



### BF Layer ###

As described above, the BF layer is the most portable interpretation of the existing BF memory model. Cells are assumed to hold only positive values between 0 and 127. 


## Compiler Overview ##

The compiler workflow is as follows:

- ASM programs are read into the compiler by a simple custom parser. The parser removes constants, acquire labels as branch targets, and applies #defines. 
- Next, the code is split into blocks, where a block is a sequential list of instructions that must end with (and contain only one) instance of JumpLTE/Load/Store. This allows us to support functions, and to support pointer load/stores at a later step in the compiler.
- Next, a store tree function  and load tree function are created which allow support for the storage and retrieval of specific points in ASM memory (pointer reference support).
- Each block of ASM instructions are converted into the multi-cell-memory layer instructions that are required to implement that instruction. For instance, the Add instruction has a direct mapping to the MAdd big memory. Any block that contained the Add asm instruction will have it replaced by an MAdd instruction.

- The number of temporary variables needed in memory is calculated (eg size of required stack) 

- The final result is decomposed, and written to a file.


Sample implementation of a multi-cell Add operation:

	ICollection ic = ICollection();
	IReturnPush();
	
	// Temporarily claim a portion of memory for use to store temporary variables (this is the "stack")
	AsmTempMemoryAddress t1 = cs.newAsmMemoryTemp(left); MTempLock(t1);
	AsmTempMemoryAddress t2 = cs.newAsmMemoryTemp(right); MTempLock(t2);
	AsmTempMemoryAddress t3 = cs.newAsmMemoryTemp(left); MTempLock(t3);
	
	ItrTempAddress ten = cs.newTemp(left.getSign(); ITempLock(ten);
	
	MCopy(left, t1); // set t1 = left operand
	MCopy(right, t2); // set t2 = right operand
	
	MSet(t3, BFCUtil.ZERO_CONSTANT); // t3 = 40,000
	
	// Add 40000 to the left operand
	for(int x = 0; x < t1.getContents().length; x++) {

		// For each cell in the left operand..
		ItrAddress l = t1.getField(x);
		
		ItrAddress ln = null; // ln is set to the value to the left of L (eg L[x-1])
		if(x+1 < t1.getContents().length)  {
			ln = t1.getField(x+1);
		}
		
		ItrAddress r = t3.getField(x); // Get the corresponding cell in t3 (eg R[x], for L[x])
		
		IDAdd(l, r); // L = L + R
		
		
		ISet(ten, 9); // set ten = 9
		IGT(l, ten, inner); // If the result is >= 10, subtract 10, and "carry the one" 
			ICollection inner {
				ISet(ten, 10); // set ten = 10
				IDSub(l, ten); // L = L - 10
				if(ln != null) IInc(ln); // ln = ln + 1
			}
		
		ISet(ten, 10); // set ten = 10
		IDAdd(l, ten); // L = L + 10. At this point, 10 <= L <= 19, which makes the following step easier
	}		
	
	// Subtract right operand from left operand 
	for(int x = 0; x < t1.getContents().length; x++) {

		// For each cell in the left operand....
		ItrAddress l = t1.getField(x);
		
		ItrAddress ln = null; // ln is set to the value to the left of L (eg L[x-1] )
		if(x+1 < t1.getContents().length)  {
			ln = t1.getField(x+1);
		}
		
		ItrAddress r = t2.getField(x); // Get the corresponding cell in R, which contains the value to subtract

		IDSub(l, r); //  set L = L - R
		
		ISet(ten, 10); // set ten = 10
		
		ILT(l, ten, inner); // if (L < 10)  {
			ICollection inner()
				if(ln != null) inner.addInstruction(IDec(ln);	 // ln = ln - 1, but only if ln exists
		// }
		
		
		ISet(ten, 9); // set ten = 9
		IGT(l, ten, inner2); // if (L >= 10) {
			ICollection inner2();
				inner2.addInstruction(ISet(ten, 10); // set ten = 10
				inner2.addInstruction(IDSub(l, ten); // L = L - 10
		// }
	
	}
	
	MCopy(t1, result); // copy t1 to result
	
	// Release our ownership of the temporary variables that we claims at the beginning of the program (free our portion of the stack)
	ITempUnlock(ten); 
	MTempUnlock(t1);
	MTempUnlock(t2);
	MTempUnlock(t3);
	IReturnPop();


### Decomposition process / Meta-instructions ###

As previously noted, transformation of a higher-layer program to lower-layer instruction program is referred to here as decomposition. At the high level this means taking multi-cell instructions and transforming them into a set of single-cell instructions, while at the lower layers this means taking single cell instructions and turning them into raw BF code. 

The process of conversion from multi-cell instructions to single cell, then single cell to BF, is an iterative process that begins with a single program, and ends with a shallow tree containing only BF instructions which are ready to be written as output. 

All instructions implement the following interface:

	/** This method decomposes the instruction, but does not change the state of the instruction object. */
	public IInstruction decompose(CompilerStatus cs);

	/** Whether or not the instruction can be further decomposed; this is true for all instructions that are not fully composed of raw BF */	
	public boolean canDecompose();

All instructions are decomposable as long as they are not BF instructions. BF instructions are the final atomic form of the transformation, and cannot be split further. All non-BF instructions will return 'true' when canDecompose(...) is called. 

When an instruction indicates that is decomposable, a call to decompose(...) with an instance of the CompilerStatus class will return the resulting decomposed instruction. All instruction implementations must know how to decompose themselves, and this knowledge is contained within the instruction's decompose(...) method. 

At a high-level, the decomposition algorithm looks like this:

	ICollection r = current list of instructions;

	while (r still contains decomposable instructions) {

		for(for each instruction ii in r) {

			while (if ii can be decomposed) {
				ii2 = ii.decompose(cs);

				replace ii with ii2 in the list  (eg. remove ii from the list; insert ii2 in the previous spot of ii)
			}

		}
	}

The "nitty gritty" details of the transformation process are handled by the CompilerStatus class, which is passed into every decompose(...) method. The CompilerStatus class contains the internal state of the compiler itself, and provides global state information about the transformation process to individual decompose methods. However, Decompose(...) methods are limited to the information that is provided to them by the CompilerStatus class.

The CompilerStatus class provides:

- Access to temporary variables (stack), as implemented by MetaInstructions
	- Creation of new temporary variables (both cell-layer and multi-layer)
	- Locking of temporary variables for use by the instruction instance
	- Unlocking of temporary variable for use by the instruction instance
- Ability to maintain pointer to current position in BF memory, implemented by MetaInstructions
	- push/pop/peek current BF memory position 
- Changing to a specific absolute BF memory position

The most important of these are temporary variables. These are a finite number of memory cells dedicated to serving as temporary variables for temporary use by instructions as the program runs. This is equivalent to data allocated on the stack in C/C++, and which will be freed once execution has left the execution scope.

For example, the greater-than instruction (IGT) requires four cells of memory in order to complete its operation. These cells of memory must be initialized to zero when IGT runs, and they must not be already have been used by any other instructions higher in the call stack (eg other instructions that have called IGT, in the same way that in C stack memory of parent calls should not be reused by children). These cells values must also not be used by any child instructions of IGT. This is why when an instruction is executing, it must own a set amount of memory (the stack) for the duration of the instruction's execution. Once the instruction has executed and returned a result, the memory is unlocked and can be used again by other instructions.

Like a stack, the scope of these temporary variables is limited to instruction execution.

##### Example:

	Instruction 
		A 			- A needs 4 memory cells, locks memory position 1-4
		|			- calls instructions B
		|
		 ---B 		- B needs 2 memory cells, locks memory position 5-6
		 	|		- B shouldn't touch memory positions 1-4, as this will affect instruction A
		    |		- B completed, releases lock on memory positions 5-6
		  	|		
		.___C		- C needs 3 memory cells, locks memory position 5-7.
		|			_ C is free to use the same memory positions as B, because B no longer needs them (unlocked them)
		|			- C completes, releases locks.
		|			
		|			-  A completes, releases memory lock 1-4
		|		
		D 			- D needs 2 memory cells, locks memory position 1-2.

Temporary variables are acquired and released by instruction decompose(...) method by including meta-instructions in their decomposition result.
Meta-instructions do not produce BF code when decomposed; instead, when decomposed they only alter the state of the CompilerStatus.

The meta-instructions are:

- ITempLock/ITempUnlock - When decomposed: ITempLock locks a specific address cell, ITempLock unlocks that address cell
- MTempLock/MTempUnlock - When decomposed: MTempLock locks a block of seven address cell (for use as multi-cell memory), MTempLock unlocks that block of cells
- IReturnPush/IReturnPop/IReturnPeek - Will discuss below; briefly, these maintain a stack of BF memory positions

Hence, if an instruction needs temporary variables to perform its task, it will declare and lock these at the beginning of its decompose(...) method, and release them at the end of its decompose(...) method. Releasing them ensures that the temporary variable memory can be used by other instructions once the current instruction has completed.

When decomposed, the meta instructions will update the CompilerStatus appropriately. The meta-instructions themselves maintain no state; they only act on state inside the CompilerStatus itself.

### Memory position meta-instructions - IReturhPush/IReturnPop/IReturnPeek ###

Some instructions may wish to alter the memory cursor position (using changeToMemPos or goto). However, because BF has no capacity to do absolute memory positioning (it only has relative memory movement), the compiler cannot leave the memory cursor in an arbitrary position from where we found it. The simple solution to this is to add an IReturnPush as the first instruction in the instruction decomposition, and an IReturnPop as the last instruction. When decomposed this pushes the current memory position to a special stack, which will later be popped to return us to our original position. With these to meta-instructions in place, we can safely move about memory without affecting calling instructions and without worrying about what instructions we call are doing.

Meta Instruction Example - GT:
	IReturnPush()								// Push our current BF memory position to the stack

		ItrTempAddress t1 = cs.newTemp(...);	// Acquire a new temporary address t1
		ItrTempAddress t2 = cs.newTemp(...);  	// ... t2
		ItrTempAddress t3 = cs.newTemp(...);  	// ... t3
		ItrTempAddress t4 = cs.newTemp(..);  	// ... t4

		ITempLock(t1));							//  Lock t1-t4 to prevent use by child instructions (like IDGtle/IDEqual, below)
		ITempLock(t2));							
		ITempLock(t3));							 
		ITempLock(t4));							 

			ISet(t4, IDGtle.A_GREATERTHAN_B)
			ICopy(left, t1)
			ICopy(right, t2)

			IDGtle(t1, t2, t3)
			IDEqual(t3, t4, inner)

		ITempUnlock(t1)							//  Release t1-t4, to allow reuse by future instructions
		ITempUnlock(t2)
		ITempUnlock(t3)
		ITempUnlock(t4)

	IReturnPop()								// Pop our current BF memory position from the stack, and return us to that position


### Challenges: Pointer Referencing and Dereferencing ###

As previously noted, BF itself has no support for pointers/pointer dereferencing of any kind. There is no simple way to access a specific value in memory based on the contents of a pointer address. 

For our purposes, this is problematic: our simple assembly language requires the ability to load and store to memory, where the address of the target memory location is stored inside a register. This is not possible without the ability to dereference a value.

This can be solved using a generated binary tree, where each of the branches of the tree correspond to a particular address in memory to access. We assign each position in memory a leaf node on the binary tree.

IBinaryRange is generic instruction that takes as input a set of memory cells to compare, and a set of branches to execute based on the result of the comparison.

IBitCheckIfElse is an instruction that examines a value at a given cell, and runs one block of code if 0 or a second block of code if 1.

Here is an example of a binary range on 3 cells, which lets it run one of 8 branches (the branch contains one or more instructions) depending on the values that are in the cells.

For example, if our cell values are 101 (cell[0] = 1, cell[1] = 0, cell[2] = 1), then the branch that corresponds to 101 will be called, which is branch 5. Branch 5 will only be called if the cells contains 101.

Figure:

	IBinaryRange
		IBitCheckIfElse 
			IBitCheckIfElse 		 			// (cell[0] == 0)
				IBitCheckIfElse 				// (cell[1] == 0)
					IBitCheckIfElse 			// (cell[2] == 0)
						[branch 0 code ...]		// run branch 0 (binary 000)
	
					IBitCheckIfElse  			// (cell[2] == 1)
						[branch 1 code ...]		// run branch 1 (binary 001)
	
				IBitCheckIfElse 	 			// (cell[1] == 1)
					IBitCheckIfElse 			// (cell[2] == 0)
						[branch 2 code...]		// run branch 2 (binary 010)
	
					IBitCheckIfElse  			// (cell[2] == 1)
						[...]		 			// run branch 3 (binary 011)
	
			IBitCheckIfElse 	 				// (cell[0] == 1)
				IBitCheckIfElse  				// (cell[1] == 0)
					IBitCheckIfElse	 			// (cell[2] == 0)
						[...]	 				// run branch 4 (binary 100)
					IBitCheckIfElse 			// (cell[2] == 1)
						[...]		 			// run branch 5 (binary 101)
				IBitCheckIfElse		 			// (cell[1] == 1)
					IBitCheckIfElse 			// (cell[2] == 0)
						[...]		 			// run branch 6 (binary 110)
					IBitCheckIfElse	 			// (cell[2] == 1)
						[...]		 			// run branch 7 (binary 111)

With the ability to create this tree structure in place, we can use it to solve the problem of storing and retrieving from arbitrary positions in memory. Each of the leaf nodes contain a load or store instruction, depending on whether a load or store tree is generated.

For example, here is the store algorithm:

Input:

- address register (address to store at -- a register containing a pointer, eg a memory position in integer )
- value register (contains the value to store at the address referenced by the pointer)

Algorithm:

- Copy the value to store to a single central location, STORE_VAL_ADDR.
- Convert the target address to it's binary form (currently we use 14 cells for this, to express a 16k range, 0-16383)
	- See MDownConvertToBinary for implementation.
- Create a leaf branch for each possible target address, 0 to 16383.
	- Each leaf node will contain this code block:
		- Copy from STORE_VAL_ADDR to memory address x, where x corresponds to the branch number
			- MCopy(STORE_VAL_ADDR, x);
- Create a binary tree that takes the binary form of the address, and the list of branches, as its input
- Pass values and execute binary tree instruction

The load algorithm is written in a similar way. The load address is converted to binary, and a set of leaf nodes are created for each possible load address. Both the address value and the branches are passed into a IBinaryTree instruction, which will call the appropriate branch, based on the given instruction.

Why use a binary tree? This approach was taken for both speed and expression efficiency. In BF, the only available branch instruction is jump-on-zero. Ostensibly the most efficient use of branching is then one branch for is-zero, and then a second branch for non-zero. This leads logically to a binary tree constructed from both these branch conditions. 


One disadvantage of this approach to pointer dereferencing is that the size of the load/store trees created can grow quite large, based on the number of memory addresses we wish to dereference pointers to. For this reason, we cannot construct a load/store tree for every load/store operation that is required by the program. We need to centralize a single instance of a load tree, and a single instance of a store tree, and share access to them throughout the BF program. This ensures that the resulting BF program does not contain multiple instances of this large structure (ballooning the size of the resulting program).

The next problem to solve is how to allow access to the store and load tree structure from anywhere within the BF code. The solution to this is a function tree, where each function or "block" of code to run is assigned a particular leaf node in the tree, and program flow is determined by specifying a target function value to execute, one at a time.


### Function Tree ###

With the function tree approach, all blocks/functions of the assembly application are assigned a value from 0 to x, where x is the number of functions.

When the function tree is executed, it takes as input the function value (0 to x), traverses the tree, and then executes the function that corresponds to that value. 

By ensuring that each function call sets the next function to run, we can simulate normal program flow between blocks, as well as jumps, and also including necessary special cases like load and store blocks.

##### Algorithm:

- Pre-requisite: Break assembly language code into blocks, where a block is defined as a sequential list of instructions that must end with JumpLTE/Load/Store (and must contains only one instance of them).
	- This ensures that once a load/store instruction is executed, we can next jump to the two special functions: load function or the store function.
	- The purpose of the special load or store function are to read data from/write data to the given point in memory (based on the address specified in the pointer), and then return to the next block after the calling function, which can then handle the loaded/stored data.


##### Code:
	
	int current_function = initial function to call; (eg the first block of code in the program)	
	while(the end of the program has not been reached) {
			
		Invoke function tree with current_function value
			- function that corresponds to the current_function value is located in the function tree (tree search) and launched
			- current function runs
			- current_fuction = the next function to be run, based on either normal program flow, a jump, or a load/store call
	}



In the context of the above algorithm, the load and store trees are just functions that will load/store a given value when a calling function requests it. Once the load/store is complete, the load/store function will automatically return to the next function to be called (as originally set by the function that called the load/store function). eg. function 0 -> load -> function 1.

##### Example:

Here is an example of normal program flow from block 0 to block 1, with a special load block in between to allow to load data from memory into a given register, as requested by block 0.

Function 0 runs, and attempts to load from memory position 100 into register A:

- function 0 sets the 'current_function' value to the special load function id
- the special load function takes a few parameters:
	- function 0 sets the destination register to load into
	- function 0 sets the memory address it wants to load from
	- function 0 sets the block it wants to jump to after the load has completed (eg the next function in the program flow after the store completes). Example: Function 1 is the next block after function 0, so we will flow there.

Next, the special Load function runs:

- It takes as input the given memory address from function 0, and copies the value from the memory address to the destination register (also provided as input). This is accomplished w/ load tree.
- It clears the parameters that were used as input
- Finally, it sets the 'current_function' value to the next block in the program flow 

Function 1 runs:

- Function 1 can now assume that the value has successfully loaded in register A from position 100, and can continue execution as normal.


## Optimization techniques ##


One of the sample programs that was written during the test process was a BF interpreter program in the ASM assembly language, such that I could compile the assembly language down to BF, and consequently have a BF interpreter written in BF (note that many other such interpreters exist that are written in BF). 

I ran the assembly language program through the compiler, and it produced a working BF program that would correctly (albeit slowly) interpret BF. However, the program size, without optimization was approximately 2.1GB. Given that executables for much larger programs in higher level languages often don't exceed several megabytes, this BF program output is very very large.

While BF language programs are very verbose, this doesn't explain the large size difference. Some amount of that must be placed at the feet of the compiler implementation. 

A number of optimization techniques were thus employed.

### Optimization Technique - Reducing Program Size - Temporary Variable Placement ###

When I analyzed the initial program output, I discovered that a large percentage of the code was dedicated to moving the cursor memory position back and forth between temporary variables in memory (eg long strings of < and >.) As initially designed, all temporary variables would be allocated off the end of main memory, such that the layout looked like this:

Memory layout:

- 0 to x - single-cell variables used by the program, where x is the number of single variables needed (see compiler implementation for details)
- x to y - Main memory (Multi cell memory), where y-x is the number of big memory cells needed by the assembly language program (fixed number, see implementation for details).
- y to z - Stack, all temporary variables are allocated beginning at y (the end of big memory cell)

This proved to be problematic, as the resulting BF "binary" was forced to travel long distances in memory between temporary and non-temporary variables, whenever there was an interaction between these two types of variables. This would produce long strings of ">"s and "<"s as the cursor memory position was moving back and forth between large memory sections.

The solution to this problem was to rearrange the temporary variables in memory, such their position in memory was as close as possible to the non-temporary variable memory positions in which they were most often used. 

With this new temporary variable placement algorithm in place, the temporary variables were no longer one contiguous block at the end of memory space, but instead were interleaved throughout the entire memory space:

- 0 to x - single-cell variables used by the program, where x is the number of single variables needed
- x to (x+y+z) - where y is the number of memory cells, and z is the number of temporary variables. 

The variables are interleaved such that a set number of multicell memory cells will then be followed by a smaller number of temporary variables, which will be then followed by more memory cells and more temporary variables, until the necessary memory requirements are fulfilled.

How do we determine where to place the temporary variables in memory? As the compiler runs, it keeps track of the closest "real" (non-temporary) variable that interacts with a temporary variable. This allows us to generate a mapping from our temporary variables to the locations in memory where they are most often used. With this technique, we can identify areas of memory that are high-usage hot spots (and which should have more temporary variables), and areas that are low-usage (and should have proportionately fewer temporary variables.)

Next, we traverse this mapping, and assigned a greater number of temporary variables into high-density areas of memory, and a smaller number of temporary variables into lower-density areas of memory (where density is usage).

This generally produces a large number of variables which are clustered right at the beginning of memory, which is fine for our needs.

With temporary memory addresses now assigned in closer proximity to where they are used, the compiler's temporary variable selection algorithm had to be updated to intelligently locate the closest temporary variable to the non-temporary values in use, rather than merely selecting the next available temporary variable on the stack (see CompilerStatus.assignTemp(...)).

With both changes in place, program size was reduced by well over 6x, from 2.1GB to 0.3GB.

### Optimization Technique - Reducing Compiler Memory Usage - Reducing resident memory by flushing finished program parts to file

The initial design of the compiler required every step of the decomposition process take place in memory. As discussed above, initial program sizes ballooned to well over 2GB, which would quickly consume the heap of a 32-bit JVM.

The first attempt to fix this was to use StringBuilder as much as possible. This improved memory usage, but still resulted in large compiler memory use in the case where the outputted BF program was large. E.g. the end result of this optimization process was a single efficiently packed 2.1GB StringBuilder; efficient, but still way too large for resident memory.

On analyzing the Java heap contents, by far the vast majority of the compiler was byte arrays used by Strings. During the decomposition process, many small strings would be eliminated and eventually moved into one final large string, which was the fully compiled program.

The solution to this problem was to move as much of the generated program to disk as possible after it was generated. Replacing the toBF(StringBuilder) output function, with one that took a FileOutputStream, allowed the compiler to write the finished parts of the program to disk, rather than keeping it resident in memory.

Fortunately this approach was amenable to the existing design of the compiler, and reduced compiler memory usage without affecting compile speed.

For the implementation details of these techniques, see usage of the methods 'String toBF()', 'void toBF(StringBuilder)' and 'void toBF(OutputStream)' in the in IInstruction interface.


### Optimization  - Reducing compiler memory usage - Reducing resident memory through deduplication of Strings ###

Even with the above optimization, the compiler would still consume a large amount of heap space, with most of that space occupied by String objects. The decompose(...) methods of the instructions tend to generate a large number of short-lived Strings as part of the decompilation process.

The JVM itself already performs some level of String deduplications (see String.intern(...) ), but it is not sufficient for our purposes, due to the wide variety of programmatically generated Strings that we use. The solution to this problem was to integrate some form of String deduplication into the decomposition process, with a shared cache called IntermediateFactory. 

The IntermediateFactory class is used by especially verbose instructions. It is a singleton which maintains a cache of previously created instructions, and which will be substituted in place of new instructions, to allow those new instructions (and their strings) to be garbage collected. It also recognizes simple single-character IBF(...) constants ("+", "-", "<", etc) , and replaces them with a pointer to an immutable single instance of that constant, rather than maintaining many separate instances of that simple single character constant.


### Optimization - Non-standard BF generation mode ###

A key missing instruction in BF is jump on non-zero (JNZ). While the lack of this instruction appeals the ones minimalist sensibilities, it is detrimental to program size and (to a lesser extent) execution speed. While it is straightforward to implement a JNZ instruction with existing jump-on-zero BF instructions, but it is significantly more verbose (>21 instructions -- see IJumpNZ). Since branch on non-zero is a fundamental and necessary operation in many cases, the JNZ pattern occurs many many times in BF code generated by the compiler.

For this reason, the compiler also supports generating non-standard BF code that includes a new JNZ branch instruction, designated with the "(" and ")" characters (and also includes a BF intrepreter that handles this instruction). 




### Optimization - Execution speed is a factor too ###

The most efficient BF programs will stick to the semantics of the BF machine, and not deviate from them. We deviate fairly dramatically from BF semantics by simulating higher-level concepts for which there is no native support from the BF language itself, such as: big memory cells, pointer dereferencing, absolutely memory positioning, function stacks, and blocks.
Any time you are simulating language/platform semantics for which there is not native support, you will have code that run much slower than expected.

As an area for future research, an optimizing interpreter/compiler could potentially recognize these constructs and optimize according.


