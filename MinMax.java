package Xbot;

import java.util.List;

import lenz.htw.sawhian.Move;

public class MinMax {
	
	private Move savedMove = null;
	private int targetDepth = 2;
	public int[] alive;
	public Board board; 
	
//	public int weight1;
//	public int weight2;
//	public int weight3;
	
	MinMax(Board config) {		
		board = config;
	
	}
			
	public Move run(int player) throws Exception{
		int rating = 0;
		rating = max(player, targetDepth,Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		System.out.println("Player"+player+", Rating of the move:" +rating);
		
		return savedMove;
	}


	public int maxN(int player, int depth, int alpha) throws Exception {
		int ratings[]= new int[4];
		int secondPl = nextPlayer(player);
		int thirdPl = nextPlayer(secondPl);
		int fourthPl = nextPlayer(thirdPl);
		List<Move> posMoves = board.calcFreeMoves(player, board);
		if (depth == 0 || posMoves == null) {
			ratings = rateAll();
		}
		Config cache;
		for (Move move : posMoves) {
			cache = board.getStateConfig().clone();
			board.makeMove(move);
			ratings = rateAll();
			//Maybe Maxsum value different
			if(ratings[secondPl] == Integer.MAX_VALUE || ratings[thirdPl] == Integer.MAX_VALUE  || ratings[fourthPl] == Integer.MAX_VALUE ) {
				
			}
			int wert = maxN(player, depth - 1, alpha);	
			board.setStateConfig(cache); //macheZugRueckgaengig();
			//Math.max(a, b)
			//if() {
			if (depth == targetDepth)
				savedMove = move;
		}		
		return 0;
	}
	
	int max(int player, int depth, int alpha, int beta) throws Exception {
		// get all free  moves for this configuration
		List<Move> posMoves = board.calcFreeMoves(player, board);
		if (depth == 0 || posMoves == null)
			return rate(player);
		Config cache;
		int maxWert = alpha; //ab
		//Max as the next player in line 
		player = nextPlayer(player);
		for (Move move : posMoves) {
			cache = board.getStateConfig().clone();
			board.makeMove(move);
			int wert = max(player, depth - 1, maxWert, beta);	
			board.setStateConfig(cache); //macheZugRueckgaengig();
			
			if (wert > maxWert) {
				maxWert = wert; 
				if(maxWert >= beta )break; //ab
				if (depth == targetDepth)
					savedMove = move;
			}
		}		
		return maxWert;
	}

	private int min(int player, int depth, int alpha, int beta) throws Exception {
		List<Move> posMoves =  board.calcFreeMoves(player, board);
		if (depth == 0 || posMoves == null)
			return rate(player);
		//Board cache;
		Config cache;
		int minWert = beta;
		player = nextPlayer(player);
		for (Move move : posMoves) {
			cache = board.getStateConfig().clone();			
			board.makeMove(move);
			int wert = 0;
			wert = minMin(player, depth - 1, minWert, beta); 
			board.setStateConfig(cache); //macheZugRueckgaengig();
			if (wert < minWert) {
				minWert = wert;
			}
		}
		return minWert;
	}
	
	private int minMin(int player, int depth, int alpha, int beta) throws Exception { //NOT
		List<Move> posMoves = board.calcFreeMoves(player, board);
		if (depth == 0 || posMoves == null)
			return rate(player);
		//Board cache;
		Config cache;
		int minWert = Integer.MAX_VALUE;
		player = nextPlayer(player);
		for (Move move : posMoves) {
			cache = board.getStateConfig().clone();			
			board.makeMove(move);
			int wert = 0;
			wert = max(player, depth - 1, alpha, minWert);
			board.setStateConfig(cache); //macheZugRueckgaengig();
			
			if (wert < minWert) {
				minWert = wert;
			}
		}
		return minWert;
	}

	int nextPlayer(int player) {
		if(player < 4) {
			player++;
		}else {
			player=0;
		}
		return player;
	}
	

	int[] rateAll(){
		int ratings[]= new int[4];
		ratings[0]=rate(0);
		ratings[1]=rate(1);
		ratings[2]=rate(2);
		ratings[3]=rate(3);
		return ratings;
	}
	
	int rate(int player){
		int freeStones = 0;
		int jumps = 0;
		Config conf = board.getStateConfig();
		Board bo = board;
		for (int i = player * 5; i < (player + 1) * 5; i++) { // for all stones of this player
			if (conf.stones[i].inStack && (conf.stones[i].x != 7 || conf.stones[i].x != 7)) { //Stone not in stack or 
				freeStones++;
				jumps += conf.stones[i].canJump(bo);
			}
		}
		if(freeStones == 0 ) {
			int secondPlayer = nextPlayer(player);
			int thirdPlayer = nextPlayer(secondPlayer);
			int fourthPlayer = nextPlayer(thirdPlayer);
			
			if(bo.getScore(player) < bo.getScore(secondPlayer) || bo.getScore(player) < bo.getScore(thirdPlayer) || bo.getScore(player) < bo.getScore(fourthPlayer) ) {
				//if anybody has a higher score
				return Integer.MIN_VALUE; //lost
			}else if(bo.getScore(player) > bo.getScore(secondPlayer) && bo.getScore(player) > bo.getScore(thirdPlayer) &&  bo.getScore(player) > bo.getScore(fourthPlayer) ) {
				//if i have the highest score
				return Integer.MAX_VALUE; //Won
			}else if(bo.getScore(player) ==  bo.getScore(secondPlayer) && bo.getScore(thirdPlayer) == bo.getScore(fourthPlayer) ) {
				//TODO finish check
				//Check all scores with each other
				return 0; //Draw
			}
		}
		return  freeStones+jumps;
	 }
	
}
	

	/**
	 * Basically like wikipedia, except player number is increased
	 * @param player
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 *//*
	int NegaMaxAB(int player, int depth, int alpha, int beta) {
		List<Move> posMoves = copyBoard.calcFreeMoves(player,copyBoard);
		if (depth == 0 || posMoves == null)
			return rate2Pl(player);
	    int maxWert = alpha;
	    Board cache;
		for (Move move : posMoves) {
	    	cache = copyBoard.clone();
			copyBoard.update(move);
	        int wert = -NegaMaxAB(-player, depth-1, -beta, -maxWert);	
			copyBoard = cache;
	       if (wert > maxWert) {
	          maxWert = wert;
	          if (maxWert >= beta)
	             break;
	          if (depth == targetDepth)
	             savedMove = move;
	       }
	    }
	    return maxWert;
	}
	
	/*
	int AlphaBeta(int depth, int alpha, int beta)
	{
	    if (depth == 0)
	        return rate();
	    boolean PVgefunden = false;
	    int best = Integer.MIN_VALUE;
		List<Move> posMoves =  CalcGameTree.calcFreeMoves(nextPlayer(this.copyBoard.playersDraw),copyBoard);
	    while (posMoves.size() >0)
	    {
	    	Board cache = copyBoard.clone();
	        copyBoard.update(posMoves.get(0));
	        int wert;
	        if (PVgefunden)
	        {
	            wert = -AlphaBeta(depth-1, -alpha-1, -alpha);
	            if (wert > alpha && wert < beta) {
	                wert = -AlphaBeta(depth-1, -beta, -wert);
	            }
	        } else {
	            wert = -AlphaBeta(depth-1, -beta, -alpha);
	        }
	        copyBoard = cache;
	        if (wert > best)
	        {
	            if (wert >= beta) {
	                return wert;
	            }
	            best = wert;
	            if (wert > alpha)
	            {
	                alpha = wert;
	                PVgefunden = true;
	            }
	        }
	    }
	    return best;
	}
}*/
