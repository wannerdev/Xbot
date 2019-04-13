package Xbot;

import lenz.htw.sawhian.Move;

public class KoordHelper {

	public static Move rotate(int amount, Move move) {
		System.out.println("Before turn"+amount+" X:"+move.x +" y:"+ move.y);
		for(int i=0; i< amount; i++) {
			int y = move.y;
			int x = (move.x-6)*-1;
			move.x = y;
			move.y = x;//(move.x-6)*-1;
		}
		System.out.println("After X:"+move.x +" y:"+ move.y);
		return move;
	}
}
