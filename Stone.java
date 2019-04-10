package Xbot;

public class Stone {

	byte x,y;
	boolean isBlocked(){
		//if two stones ahead
		return true;
	}
	
	/**
	 * 
	 * @param stone
	 * @return return negative if blocked after jump
	 */
	byte canJump(Stone stone) {
		//hasone ahead and space
		//hastwo ahead and two space
		//hasthree ahead and three space
		return 3;
	}
	
	byte distanceToGoal(){
		return this.x; //spieler 0
	}
	boolean checkIfMoveValid() {
		return false;
	}
}
