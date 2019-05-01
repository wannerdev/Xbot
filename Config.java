package Xbot;

import java.util.Arrays;

class Config {
	Stone stones[] = new Stone[28];
	byte stackSto[] = { 7, 7, 7, 7 };
	byte ptr = 0; // basically a counter how many stones on board.
	
	@Override
	public String toString() {
		String rep="";
		for (int i = 6; i >= 0; i--) {
			rep+=i;
	
	        for (int j = 0; j <= 6; j++) {
	        	boolean isthere=false;
	    		for(Stone st :stones) {
		            if (i == st.y && j == st.x) {
		    			rep+=" X";
		                isthere=true;
		                break;
		            }
	    		}
	    		if(!isthere){
	    			rep+=" .";
	            }
	        }
			rep+="\n";
		}
		return rep;
		//return "Config [stones=" + Arrays.toString(stones) + ", stackSto=" + Arrays.toString(stackSto) + ", ptr=" + ptr
		//		+ "]";
	}
	
}