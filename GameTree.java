package Xbot;

import java.util.List;

import lenz.htw.sawhian.Move;

public class GameTree {
	
	public Move calculateOneMove(int player, Board b) throws Exception{
		List<Move> li = b.calcFreeMoves(player, b);
		return li.get(0);
	}
	
	public Move MultiMax(int player, Board b, float[] weights) throws Exception{
		MinMax alg = new MinMax(b.getStateConfig().clone(),weights);
		Move m = alg.run(player);
		//If our Alg produces invalid moves
		if(!Board.isValidMove(m, b)) {
		//	System.err.println("MaxN produces invalid move :: MOVE ("+m.x+" || "+ m.y+")");
			m = calculateOneMove(player, b);
		}else {
			
				//System.out.println("MaxN produces valid move :: MOVE ("+m.x+" || "+ m.y+")");
				
			
		}
		return m;
	}
	
	
	public Move randomMove(int playerNumber, Board b) throws Exception {

		List<Move> myPossibleMoves = b.calcFreeMoves(playerNumber, b);
		//System.out.println(myPossibleMoves.size());
		if (myPossibleMoves.size() > 0) {

		    int moveIndex = (int) (Math.random() * myPossibleMoves.size());

			return myPossibleMoves.get(moveIndex);

		} else {
			throw new Exception("myPossibleMoves.size() <0 Game Over - All stones Blocked (If moves possible check calcfreemoves)");
			//System.err.println
			//return null;
		}
	}
}
