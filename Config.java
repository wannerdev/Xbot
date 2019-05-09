package Xbot;


class Config implements Cloneable {
	Stone stones[] = new Stone[28];
	byte stackSto[] = { 7, 7, 7, 7 };
	byte ptr = 0; // basically a counter how many stones on board.
	
	/**
	 * 
	 * set up the stones and assign them a player number
	 */
	public Config(){
		for (int i = 0; i < this.stones.length; i++) {
			this.stones[i] = new Stone((byte) -1, (byte) -1, (byte) 0);
			this.stones[i].inStack = true;
			if (i < 7) {
				this.stones[i].player = 0;

			} else if (i >= 7 && i < 14) {
				this.stones[i].player = 1;

			} else if (i >= 14 && i < 21) {
				this.stones[i].player = 2;
				
			} else if (i >= 21 && i < 28) {
				this.stones[i].player = 3;
			}
		}
	}
	
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
	
	@Override
	protected Config clone(){
		Config c = new Config();
		c.ptr = this.ptr;
		//c.stones = this.stones.clone();
		for(int i=0; i< stones.length; i++) {
			c.stones[i] = this.stones[i].clone();
		}
		for(int i=0; i< stackSto.length; i++) {
			c.stackSto[i] = this.stackSto[i];
		}
		//c.stackSto = this.stackSto.clone();
		
		return c;		
	}
}