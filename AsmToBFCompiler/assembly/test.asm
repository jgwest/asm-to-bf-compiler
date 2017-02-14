


#define r_currMemPos r0

#define r_c r1
#define r_y r2
#define r_lastLoopPos r3
#define r_x r4
#define r_z r5

#define r_progVal r6

#define r_cmp r7

#define r_ZERO r8 
#define r_ONE  r9

PrintStmnt "Started:\n"



Load r_ZERO LABEL_ZERO
Load r_ONE LABEL_ONE



// -------------------------------
		
		Load r_z LABEL_MEM_START
		
		Add r_z r_currMemPos r_z				
		LoadI r_c r_z

		PrintStmnt "r_z:\n"
		PrintRegVal r_z
		PrintStmnt "r_c "
		PrintRegVal r_c 
		PrintStmnt "}\n"
		




// ---------------------


Halt



JumpLte r0 r0 LABEL_start_program


label LABEL_PROG_START 
0

label LABEL_MEM_START 
500

label LABEL_ZERO 
0

label LABEL_ONE
1

label LABEL_PLUS
43

label LABEL_COMMA
44

label LABEL_MINUS
45

label LABEL_PERIOD
46

label LABEL_LT
60

label LABEL_GT
62

label LABEL_OPEN_BRACE
91

label LABEL_CLOSE_BRACE
93


label LABEL_start_program
	Halt