


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


Load r_ZERO LABEL_ZERO
Load r_ONE LABEL_ONE

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




//	for (int r_x = 0; r_x < 2000; r_x++) {
//		mem[r_x] = 0;
//	}

//	r_x = 0;
//	for (; r_x < strlen(prg); r_x++) {
//		mem[r_x+LABEL_PROG_START] = prg[r_x];
//	}



label LABEL_start_program

Add r_x r_ZERO r_ZERO

PrintStmnt "Starting!\n"

label startofmainloop

Load r_z LABEL_PROG_START
Add r_z r_z r_x
LoadI r_progVal r_z

Add r_cmp r_x r_ONE
JumpLte r_cmp r_Zero mainloopinner1
JumpLte r0 r0 mainloop2

label mainloopinner1
	PrintStmnt "error: before program start\n"
	JumpLte r0 r0 endofprogram


label mainloop2
	JumpLte r_progVal r_ZERO mainloopinner2
	JumpLte r0 r0 mainloop3


label mainloopinner2
	PrintStmnt "end of program\n"
	JumpLte r0 r0 endofprogram

label mainloop3
	Load r_cmp LABEL_PLUS
	JumpLte r_progVal r_cmp mainloop3a
	JumpLte r0 r0 mainloop4

label mainloop3a
	JumpLte r_cmp r_progVal mainloopinner3
	JumpLte r0 r0 mainloop4

label mainloopinner3
		Load r_z LABEL_MEM_START
		Add r_z r_z r_currMemPos
		LoadI r_c r_z
		Add r_c r_c r_ONE
		Load r_z LABEL_MEM_START
		Add r_z r_z r_currMemPos
		StoreI r_z r_c
		JumpLte r0 r0 endofinsidemainloop

label mainloop4
	Load r_cmp LABEL_COMMA
	JumpLte r_cmp r_progVal mainloop4a
	JumpLte r0 r0 mainloop5

label mainloop4a
	JumpLte r_progVal r_cmp mainloopinner4
	JumpLte r0 r0 mainloop5

	label mainloopinner4
		PrintStmnt "Unsupported\n"
		JumpLte r0 r0 endofprogram
	

label mainloop5
	Load r_cmp LABEL_MINUS
	JumpLte r_cmp r_progVal mainloop5a
	JumpLte r0 r0 mainloop6

label mainloop5a
	JumpLte r_progVal r_cmp mainloopinner5
	JumpLte r0 r0 mainloop6

	label mainloopinner5
		Load r_z LABEL_MEM_START
		Add r_z r_z r_currMemPos
		LoadI r_c r_z
		Sub r_c r_c r_ONE
		Load r_z LABEL_MEM_START
		Add r_z r_z r_currMemPos
		StoreI r_z r_c
		JumpLte r0 r0 endofinsidemainloop
	

label mainloop6
	Load r_cmp LABEL_PERIOD
	JumpLte r_cmp r_progVal mainloop6a
	JumpLte r0 r0 mainloop7

label mainloop6a
	JumpLte r_progVal r_cmp mainloopinner6
	JumpLte r0 r0 mainloop7

		label mainloopinner6
		Load r_z LABEL_MEM_START
		Add r_z r_z r_currMemPos
		LoadI r_z r_z

		PrintRegValA r_z

		JumpLte r0 r0 endofinsidemainloop
	

label mainloop7

	Load r_cmp LABEL_LT
	JumpLte r_cmp r_progVal mainloop7a
	JumpLte r0 r0 mainloop8

label mainloop7a
	JumpLte r_progVal r_cmp mainloopinner7
	JumpLte r0 r0 mainloop8

		label mainloopinner7
		Sub r_currMemPos r_currMemPos r_ONE
		JumpLte r0 r0 endofinsidemainloop
	

label mainloop8
 
	Load r_cmp LABEL_GT
	JumpLte r_cmp r_progVal mainloop8a
	JumpLte r0 r0 mainloop9

label mainloop8a
	JumpLte r_progVal r_cmp mainloopinner8
	JumpLte r0 r0 mainloop9

		label mainloopinner8
		Add r_currMemPos r_currMemPos r_ONE
		JumpLte r0 r0 endofinsidemainloop
	

label mainloop9
	Load r_cmp LABEL_OPEN_BRACE
	JumpLte r_cmp r_progVal mainloop9a
	JumpLte r0 r0 mainloop10

label mainloop9a
	JumpLte r_progVal r_cmp mainloopinner9
	JumpLte r0 r0 mainloop10

		label mainloopinner9
		Load r_z LABEL_MEM_START
		
		Add r_z r_currMemPos r_z				
		LoadI r_c r_z
		JumpLte r_ZERO r_c mainloop9b
		JumpLte r0 r0 mainloopinner9b

label mainloop9b
		JumpLte r_c r_ZERO mainloopinner9a
		JumpLte r0 r0 mainloopinner9b

			label mainloopinner9a
			Add r_z R_ZERO r_ZERO
			Add r_y r_x r_ZERO

			label innerforloop1start

				Load r_z LABEL_PROG_START
				Add r_z r_y r_z
				LoadI r_progVal r_z
				
				Load r_cmp LABEL_OPEN_BRACE
				JumpLte r_cmp r_progVal innerforloop1starta
				JumpLte r0 r0 innerforloop1start2

			label innerforloop1starta

				JumpLte r_progVal r_cmp innerforloop1start1
				JumpLte r0 r0 innerforloop1start2

					label innerforloop1start1

					Add r_c r_c r_ONE
					JumpLte r0 r0 innerforloop1start2
				
				label innerforloop1start2
				Load r_cmp LABEL_CLOSE_BRACE

				JumpLte r_cmp r_progVal innerforloop1start2a
				JumpLte r0 r0 innerforloop1start4

				label innerforloop1start2a
				JumpLte r_progVal r_cmp innerforloop1start3
				JumpLte r0 r0 innerforloop1start4

					label innerforloop1start3
					Sub r_c r_c r_ONE
					
					JumpLte r_ZERO r_c innerforloop1start3a
					JumpLte r0 r0 innerforloop1start4

					label innerforloop1start3a
					JumpLte r_c r_ZERO innerforloop1start3inner
					JumpLte r0 r0 innerforloop1start4
					
						label innerforloop1start3inner
						Add r_x r_y r_ZERO
						JumpLte r0 r0 endofinsidemainloop

				label innerforloop1start4
				
				Add r_y r_y r_ONE
			JumpLte r0 r0 innerforloop1start

		 
			label mainloopinner9b
			Add r_lastLoopPos r_x r_ONE
			JumpLte r0 r0 endofinsidemainloop

		JumpLte r0 r0 endofinsidemainloop
	

label mainloop10

	Load r_cmp LABEL_CLOSE_BRACE
	JumpLte r_cmp r_progVal mainloop10a
	JumpLte r0 r0 endofinsidemainloop

label mainloop10a
	JumpLte r_progVal r_cmp mainloop10inner1
	JumpLte r0 r0 endofinsidemainloop

		label mainloop10inner1
		JumpLte r_lastLoopPos r_ZERO mainloop10innera
		JumpLte r0 r0 mainloop10inner2

		label mainloop10innera
		JumpLte r_ZERO r_lastLoopPos mainloop10inner3
		JumpLte r0 r0 mainloop10inner2

			label mainloop10inner2
			Sub r_x r_lastLoopPos r_ONE
			Sub r_x r_x r_ONE
			Add r_lastLoopPos r_ZERO r_ZERO
			JumpLte r0 r0 endofinsidemainloop

			label mainloop10inner3
			Add r_c r_ZERO r_ZERO
			Add r_y r_x r_ZERO
			
			label innerforloop2start

				Load r_z LABEL_PROG_START
				Add r_z r_z r_y
				LoadI r_progVal r_z

				Add r_y r_y r_ONE
				JumpLte r_y r_ZERO innerforloop2starta
				JumpLte r0 r0 innerforloop2startb

				label innerforloop2starta

					Sub r_y r_y r_ONE
					JumpLte r0 r0 endofinsidemainloop
	
				label innerforloop2startb
				Sub r_y r_y r_ONE
			
				Load r_cmp LABEL_OPEN_BRACE
				JumpLte r_cmp r_progVal innerforloop2startc
				JumpLte r0 r0 mainloop10inner6

				label innerforloop2startc

				JumpLte r_progVal r_cmp mainloop10inner4
				JumpLte r0 r0 mainloop10inner6

					label mainloop10inner4

					Sub r_c r_c r_ONE					
					JumpLte r_ZERO r_c mainloop10inner4a
					JumpLte r0 r0 mainloop10inner6
					
					label mainloop10inner4a
					JumpLte r_c r_ZERO mainloop10inner5
					JumpLte r0 r0 mainloop10inner6


						label mainloop10inner5
						Sub r_x r_y r_ONE
						JumpLte r0 r0 endofinsidemainloop
					
					JumpLte r0 r0 mainloop10inner6
				

				label mainloop10inner6
				
				Load r_cmp LABEL_CLOSE_BRACE
				
				JumpLte r_cmp r_progVal mainloop10inner6a
				JumpLte r0 r0 mainloop10inner8
				
				label mainloop10inner6a

				JumpLte r_progVal r_cmp mainloop10inner7
				JumpLte r0 r0 mainloop10inner8

					label mainloop10inner7

					Add r_c r_c r_ONE
					JumpLte r0 r0 mainloop10inner8
				

			label mainloop10inner8

			Sub r_y r_y r_ONE
			JumpLte r0 r0 innerforloop2start
			

		JumpLte r0 r0 endofinsidemainloop


label endofinsidemainloop
	Add r_x r_x r_ONE
	JumpLte r0 r0 startofmainloop

label endofprogram
	Halt

