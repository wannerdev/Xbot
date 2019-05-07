package Xbot;

import java.util.List;

import lenz.htw.sawhian.Move;

public class GameTree {
	
	public Move calculateOneMove(int player, Board b) throws Exception{
		List<Move> li = b.calcFreeMoves(player, b);
		return li.get(0);
	}
	
	public Move randomMove(int playerNumber, Board b) throws Exception {

		List<Move> myPossibleMoves = b.calcFreeMoves(playerNumber, b);
		//System.out.println(myPossibleMoves.size());
		if (myPossibleMoves.size() > 0) {

		    int moveIndex = (int) (Math.random() * myPossibleMoves.size());

			return myPossibleMoves.get(moveIndex);

		} else {
			throw new Exception("myPossibleMoves.size() <0 WTF ");
		}
	}
}
