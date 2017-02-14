using GLib;
using Gee;


/**
 * This interpreter speeds the processing of BF programs by:
 * - Interpreting [-] as setting the current cell to 0 (rather than looping continuously)
 * - Scanning for and interpreting long sequences of symbols, and running all 
 *   simultaneously. For instance, ++++++++++ would be interpreted by the program
 *   as a single +10, rather than 10 +1s.
 */
public class FastInterpreterNew : GLib.Object {

	public static void fp(string s) {
		var file = File.new_for_path("/tmp/log");
		var file_stream = file.append_to(FileCreateFlags.NONE);
		
		var data_stream = new DataOutputStream (file_stream);
		data_stream.write((s+"\n").data);
		data_stream.close();
		
		file_stream.close();
		
	}

	public static void jgwrun(string[] args) {
		FastInterpreterNew instance = new FastInterpreterNew();
		
		instance.run_engine(File.new_for_path(args[1]));
		stdout.printf("Complete.\n");
	}
	
	public void run_engine(File f) {
		int[] m = new int[30000];
		run_engine_full(f, m);
	}
	
	public void run_engine_full(File f, int[] m ) {
		string result = "";
		
		// This pattern necessarily means set the current cell to 0, so replace it with a
		// new symbol to be recognized below as set to 0
//		s = s.replace("[-]", "%");

		stdout.printf("Engine loading file...\n");
		stdout.flush();
		
		ArrayList<Instruction ?> program= new ArrayList<Instruction ?>();

		try {
		
			// Open file for reading and wrap returned FileInputStream into a
			// DataInputStream, so we can read line by line
			var dis = new DataInputStream (f.read ());
		
			uint8[] chars_read = new uint8[1024];
			
			StringBuilder sb = new StringBuilder("");
			int c = -1;
			do {
				c = (int)dis.read(chars_read);				
				if(c > 0) {
					for(int x = 0; x < c; x++) {
						sb.append_c((char)chars_read[x]);
					}
					
					parse_string(sb.str, program);
				}
				sb = null; sb = new StringBuilder("");
			} while(c > 0);
			
		} catch(Error ioe) {
			stderr.printf("Error: %s\n", ioe.message);
		}
		
		stdout.printf("File loaded.\n"); stdout.flush();

		Gee.LinkedList<int> st = new Gee.LinkedList<int>();
		Instruction i;
		
		int[] a = new int[program.size];
		for(int x = 0; x < program.size; x++) {
			i = program.get(x);
			switch(i.bf_type) {
			
				case '(':
					st.offer_tail(x);
					break;
				case ')':
					a[x] = st.peek_tail();
					a[st.peek_tail()] = x;
					st.poll_tail();
					break;

				case '[':
					st.offer_tail(x);
					break;
				case ']':
					a[x] = st.peek_tail();
					a[st.peek_tail()] = x;
					st.poll_tail();
					break;
			}
		}

		int curr = 0;

		stdout.printf("program size:%d\n", program.size);

		for(int x = 0; x < m.length; x++) {
			
			m[x] = 0;			
		}
		
		Instruction[] prg = new Instruction[program.size];
		for(int x = 0; x < prg.length; x++) {			
			prg[x] = program.get(x);
			
		}
		
		uint64 start = ValaUtil.curr_time();
		
		for(int x = 0; x < prg.length && x>= 0; x++) {
		
			i = prg[x];
			
			switch(i.bf_type) {
				case '>':
					curr = (curr + i.num_inst) % m.length;
					break;
				case '<':
					curr = (curr - i.num_inst) % m.length;
					break;
				case '+':
					m[curr] = (m[curr]+ i.num_inst)%256;
					break;
				case '-':
					m[curr] = (m[curr] - i.num_inst) % 256;
					
					if(m[curr] < 0) {
						m[curr] += 256;
					}
					else m[curr] = m[curr]%256;
					break;
				case '.':
					stdout.printf("[");
					stdout.printf("%c", (char)m[curr]);
					stdout.printf("]");					
					stdout.flush();
					result += ((char)m[curr]).to_string();
					break;
				case ',':
					x = -9999;
					break;
				case '[':
					if(m[curr] == 0) { 
						x = a[x];
					}
					break;
				case ']':
					x = a[x] - 1;
					break;

				case '(':
					if(m[curr] != 0) { 
						x = a[x];
					}
					break;
				case ')':
					x = a[x] - 1;
					break;

				case '%':
					m[curr] = 0;
					break;
					
			}
		}
		
		stdout.printf("time to run: %d\n", (int)(ValaUtil.curr_time()- start));		
		
	}

	private static void parse_string(string s, ArrayList<Instruction ?> program) {
		// Parse the program into instructions, noting repetition of ><+-.
		Instruction i = Instruction();
		bool i_defined = false;
	
		for(int x = 0; x < s.length; x++) {
			unichar c = s.get_char(x);
			
			if(i_defined) {
				
				if(i.bf_type != c || (i.bf_type != '>' && i.bf_type != '<' && i.bf_type != '+' && i.bf_type != '-')) {
					i_defined = true;
					program.add(i);
					i = new Instruction();
					i.num_inst = 1;
					i.bf_type = c;
				} else {
					i.num_inst++;
				}
			} else {
				i_defined = true;
				i = new Instruction();
				i.num_inst = 1;
				i.bf_type = c;
			}
		}

		// Add the const instruction
		if(i_defined) {
			program.add(i);
		}

	}
	
	struct Instruction {
		public unichar bf_type;
		public int num_inst;
	}

}
