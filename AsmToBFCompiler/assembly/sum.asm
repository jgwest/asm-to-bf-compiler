
#define r_x r0
#define r_totalSum r1
#define r_numValues r2
#define r_zero r3
#define r_one r4
#define r_temp1 r5
#define r_temp2 r6


JumpLte r0 r0 start_program

label LABEL_num_values_addr 
1

label LABEL_num_values_start_addr 
2

label LABEL_zero
0

label LABEL_one
1


label start_program

	// r_totalSum = LABEL_zero;
	Load r_totalSum LABEL_zero
	
	// r_numValues = memory[LABEL_num_values_addr]
	// r_temp1 = LABEL_num_values_addr; 
	Load r_temp1 LABEL_num_values_addr

	//r_numValues = memory[r_temp1]; 
	LoadI r_numValues r_temp1

	//r_x = LABEL_num_values_start_addr; 
	Load r_x LABEL_num_values_start_addr

	//	r_zero = LABEL_zero; 
	Load r_zero LABEL_zero

	// r_one = LABEL_one; 
	Load r_one LABEL_one

// loop_start: 
label loop_start
	// Move through each of the values in memory, and add them to the total

	// r_totalSum = memory[r_x] + r_totalSum
	// r_temp1 = memory[r_x]; 
	LoadI r_temp1 r_x

	// r_totalSum = r_totalSum + r_temp1; 
	Add r_totalSum r_totalSum r_temp1

	// r_x++;
	// r_x = r_x + r_one; 
	Add  r_x  r_x  r_one

	// Left side of comparison
	// r_x +1 
	// r_temp1 = r_x + r_one; 
	Add r_temp1 r_x r_one

	// Right side of comparison
	// was r_temp2 = LABEL_num_values_start_addr + r_numValues;
	// r_temp2 = LABEL_num_values_start_addr; 
	Load r_temp2 LABEL_num_values_start_addr
	
	// r_temp2 = r_temp2 + r_numValues; 
	Add r_temp2 r_temp2 r_numValues

	// The branch needs to be LTE, so
	// r_x < LABEL_num_values_start_addr + r_numValues
	// becomes 
	// r_x + 1 <= LABEL_num_values_start_addr + r_numValues
	//
	// The general rule is (x < y) == (x + 1 <= y).
	// this was: if(r_x +1 <= LABEL_num_values_start_addr + r_numValues) goto loop_start;
	// if(r_temp1 /* LS */ <= r_temp2 /* RS*/) goto loop_start; 
	JumpLte r_temp1 r_temp2 loop_start
// loop_end:
label loop_end

	// memory[r_zero] = r_totalSum; 
	StoreI r_zero r_totalSum

	Halt
