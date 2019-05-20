package Xbot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class MinMax {

	private Move[] savedMove = new Move[4];
	private Move backUpMove[] = new Move[4];
	private int targetDepth = 2;
	public int[] alive;
	public Board board;
	public final int maxsum = 21;
	public final int maxp = 7;
	private int prune = 0;
	private float[] weights;


	MinMax(Config conf, float[] weights) {
		this.board = new Board();
		this.board.setStateConfig(conf);
		this.weights = weights;
	}

	public Move run(int player) throws Exception {
		float[] rating = new float[4];

		// Multiply by 3 because we are 4 players
		rating = Shallow(board.getStateConfig(), player, maxsum, targetDepth * 3); // maxN(player, targetDepth * 3);

		//System.out.println("Player" + player + ", Rating of the move:" + rating[player]);
		//System.out.println("Rating of All" + ":" + rating[0] + " " + rating[1] + " " + rating[2] + " " + rating[3]);
		//System.out.println("MAXN pruned (" + prune + ") times!");
		if (savedMove[player] == null) {
			//System.out.println("BACKUPMOVE: X = " + backUpMove[player].x + " || Y = " + backUpMove[player].y);
			return backUpMove[player];
		} else {

			//System.out.println("MAXN MOVE: X = " + savedMove[player].x + " || Y = " + savedMove[player].y);
			return savedMove[player];
		}

	}

	/**
	 * deprecated
	 * @param player
	 * @param depth
	 * @return
	 * @throws Exception
	 */
	public float[] maxN(int player, int depth) throws Exception {
		float ratings[] = new float[4];
		// get all free moves
		List<Move> posMoves = board.calcFreeMoves(player, board);
		// if I am the final node, evaluate my Configuration and return the rating
		if (depth == 0 || posMoves == null) {
			ratings = rateAll();
			return ratings;
		}
		Config cache;
		// make a List/ array for all the ratings of the possible nodes
		ArrayList<float[]> ratingList = new ArrayList<float[]>();
		int j = 0;

		// iterate through all possible moves
		for (Move move : posMoves) {
			// save the board to the cache
			cache = board.getStateConfig().clone();

			// make the move to create new config / Node
			board.makeMove(move);
			// check to see if Node is Terminal
			ratings = rateAll();
			if (ratings[player] == Integer.MAX_VALUE) {
				// savedMove = move;
				return ratings;
			} else if (ratings[player] == Integer.MIN_VALUE) {
				// cut tree if possible
				return ratings;
			}
			ratings = maxN(nextPlayer(player), depth - 1);
			// If I have finished searching my child Nodes I take the final evaluation for
			// this Node
			ratingList.add(ratings);
			// if i am at the root node
			if (depth == targetDepth * 3) {
				if (ratings[player] > ratingList.get(j)[player] || savedMove[player] == null) {
					savedMove[player] = move;
				}
			}
			// reset config
			board.setStateConfig(cache); // macheZugRueckgaengig();
			j++;
		}
		float[] bestRating = getBestRating(player, ratingList);

		return bestRating;
	}

	private float getOurBestMove(int player, ArrayList<Float[]> list) {
		float cache = 0;
		for (Float[] item : list) {
			if (item[player] >= cache) {
				cache = item[player];
			}
		}
		return cache;
	}

	private static float[] getBestRating(int player, ArrayList<float[]> list) {
		float[] cache = new float[4];
		for (float[] item : list) {
			if (item[player] >= cache[player]) {
				cache = item;
			}
		}
		return cache;
	}

	int nextPlayer(int player) {
		if (player < 3) {
			player++;
		} else {
			player = 0;
		}
		return player;
	}

	float[] rateAll() {
		float ratings[] = new float[4];
		ratings[0] = rate(0);
		ratings[1] = rate(1);
		ratings[2] = rate(2);
		ratings[3] = rate(3);
		return ratings;
	}
	
	// x = jumpWeight
	float rate(int player) {
		int freeStones = 0;
		float jumps = 0;
		Config conf = board.getStateConfig();
		Board bo = board;
		float weightJump = 2*weights[0];
		float weightScore = 2*weights[1];
		float weighthasJumped =  2 * weights[2];
		for (int i = player * 7; i < (player + 1) * 7; i++) { // for all stones of this player
			if (!conf.stones[i].inStack && !conf.stones[i].isScored && !conf.stones[i].isBlocked(board)) { // Stone not
																											// in stack
																											// or
				freeStones++;
				jumps += (conf.stones[i].canJump(bo)+(conf.stones[i].hasJumped*weighthasJumped));
			}
		}

		for (int i = 0; i < 4; i++) {
			if (board.getScore(i) == maxp) {
				// if Terminal check for the winner
				if (i == player) {

					return Integer.MAX_VALUE;

				} else {
					// if we lost make sure to rate this config the worst possible
					return Integer.MIN_VALUE;

				}
			}
		}
		if (bo.isAllBlocked(player,board)) {
			int secondPlayer = nextPlayer(player);
			int thirdPlayer = nextPlayer(secondPlayer);
			int fourthPlayer = nextPlayer(thirdPlayer);
			// TODO if all my stones are blocked and nobody has won yet i lost,?

			if (bo.getScore(player) < bo.getScore(secondPlayer) || bo.getScore(player) < bo.getScore(thirdPlayer)
					|| bo.getScore(player) < bo.getScore(fourthPlayer) || bo.isAllBlocked(player,board)) {
				// if anybody has a higher score
				return Integer.MIN_VALUE; // lost
			} else if (bo.getScore(player) > bo.getScore(secondPlayer) && bo.getScore(player) > bo.getScore(thirdPlayer)
					&& bo.getScore(player) > bo.getScore(fourthPlayer)) {
				// if i have the highest score
				return Integer.MAX_VALUE; // Won
				// if at least two people have the same score its a draw
			} else if (bo.getScore(player) == bo.getScore(secondPlayer)
					|| bo.getScore(player) == bo.getScore(thirdPlayer)
					|| bo.getScore(player) == bo.getScore(fourthPlayer)) {
				// TODO check
				return -1; // Draw
			}
			System.err.println("recheck rate function");
			return -1; // probably also a Draw
		}	
	    return ((jumps * weightJump) + (bo.getScore(player) * weightScore));
	}
	

	float[] Shallow(Config Node, int Player, float Bound, int depth) throws Exception {
		// add something here so that we can check if a player has a score = 7
		if (Bound < 0) {

			Bound = 0;
		}
		// get all possible moves that can be made for this player
		List<Move> posMoves = board.calcFreeMoves(Player, board);

		if (Node.isTerminal() || posMoves.size() == 0 || depth == 0) {

			/// this should indicate best or worst case scenarios ( iE win or lose)
			return rateAll();
		}
		// get the first child node
		Move firstMove = posMoves.get(0);
		// create cache and make the first possible move
		Config cache;
		cache = board.getStateConfig().clone();
		board.makeMove(firstMove);
		// populate the ratings table by evaluating the first leaf node

		float Best[] = Shallow(board.getStateConfig(), nextPlayer(Player), maxsum, depth - 1);

		// reset the board so that it can be used again
		board.setStateConfig(cache);
		int i = 0;

		// now for the remaining child nodes board.setStateConfig(cache);
		float Current[];
		for (Move move : posMoves) {
			if (i != 0) {

				if (Best[Player] >= Bound) {
					if (move.player == Player && depth == targetDepth * 3) {
						savedMove[Player] = move;
					}
					prune++;
					return Best;
				}

				cache = board.getStateConfig().clone();
				board.makeMove(move);

				Current = Shallow(board.getStateConfig(), nextPlayer(Player), maxsum - Best[Player], depth - 1);
				if (depth == targetDepth * 3) {

					//System.out.println("Move " + i + " Score:: " + Current[0] + " " + Current[1] + " " + Current[2]
						//	+ " " + Current[3]);

				}
				if (Current[Player] > Best[Player] && depth == targetDepth * 3) {

					backUpMove[Player] = move;

					Best = Current;

				}
				// reset the board so that it can be used again
				board.setStateConfig(cache);
			} else {
				if (depth == targetDepth * 3) {

					
					//System.out.println("Move " + i + " Score:: " + Best[0] + " " + Best[1] + " " + Best[2] + " " + Best[3]);

				}

			}

			i++;

		}
		if (depth == targetDepth * 3) {
			if (backUpMove[Player] == null && savedMove[Player] == null) {
				savedMove[Player] = firstMove;
				//System.out.println("this is fucked");
			} else if (backUpMove[Player] != null) {

				if (!Board.isValidMove(backUpMove[Player], board)) {

					//System.out.println("this is fucked2");
				}
			} else if (savedMove[Player] != null) {

				if (!Board.isValidMove(savedMove[Player], board)) {

					//System.out.println("this is fucked3");
				}
			}
		}
		return Best;
	}

}
