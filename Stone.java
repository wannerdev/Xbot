package Xbot;

import lenz.htw.sawhian.Move;

public class Stone implements Cloneable {

	
	byte x, y, player;
	public boolean inStack;
	public boolean isScored;
	
	Stone(byte x, byte y, byte player){
		this.x = x;
		this.y = y;
		this.player = player;
		inStack = true;
		isScored = false;
	}
	/**
	 * if two stones ahead	
	 * if in stack stone is not blocked
	 * @param bo
	 * @return
	 */
	boolean isBlocked(Board bo) {		
		Move dir = KoordHelper.playerToDirection(player);	    
		if (!inStack) {
			// if i'm not in stack get my forward neighbor
			Stone neighbor = bo.getStoneAtKoord((x+dir.x), (y+dir.y));
			if (neighbor != null) {
				// if that neighbor exists
				if (neighbor.player == player) {
					// if the neighbor is one of my stones retrun true
					return true;
								
				}else {
					// if he isnt check if the field behind him is free 
					
					Stone farNeighbor = bo.getStoneAtKoord((x+dir.x*2), (y+dir.y*2));
					if (farNeighbor != null) {
						
						return true;
					}					
				}
			}
			
		}
		
		return false;
		//return !inStack||(bo.getStone((x+dir.x), (y+dir.y)) != null) && (bo.getStone((x+dir.x*2), (y+dir.y*2)) != null);
	}
	
	/**
	 * 
	 * @param board 
	 * @returns amount of possible jumps 0,1,2,3
	 */
	byte canJump(Board bo) {
		Move dir = KoordHelper.playerToDirection(player);	
		byte result = 0;
		//has one ahead and space behin
		if((bo.getStoneAtKoord((x+dir.x), (y+dir.y)) != null) && bo.getStoneAtKoord((x+dir.x), (y+dir.y)).player !=this.player && (bo.getStoneAtKoord((x+dir.x*2), (y+dir.y*2)) == null)) {
			result = 1;
		}
		//hastwo ahead and two space
		if((result ==1 && bo.getStoneAtKoord((x+dir.x*3), (y+dir.y*3)) != null) && bo.getStoneAtKoord((x+dir.x), (y+dir.y)).player !=this.player &&  (bo.getStoneAtKoord((x+dir.x*4), (y+dir.y*4)) == null) ) {
			result++;	
		}
		//hasthree ahead and three space
		if(result ==2 &&(bo.getStoneAtKoord((x+dir.x*5), (y+dir.y*5)) != null)&& bo.getStoneAtKoord((x+dir.x), (y+dir.y)).player !=this.player && (bo.getStoneAtKoord((x+dir.x*6), (y+dir.y*6)) == null) ) {
			result++;
		}
		return result;		
	}	
	
	byte distanceToGoal(){
		//switch()
		Move dir = KoordHelper.playerToDirection(player);
		if(dir.x != 0) {
			return (byte) ((byte)7-this.x); 	
		}
		if(dir.y !=0) {
			return (byte) ((byte)7-this.y);			
		}
		return 7;
	}
	
	public Stone clone() {
		Stone st = new Stone((byte)-1,(byte)-1,(byte)-1);
		st.inStack = this.inStack;
		st.isScored = this.isScored;
		st.player = this.player;
		st.x = this.x;
		st.y = this.y;
		
		return st;
		
		
	}
}
