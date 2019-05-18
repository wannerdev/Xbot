package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class MinMax {

	private Move[] savedMove = new Move[4];
	private Move backUpMove[] = new Move[4];
	private int targetDepth = 2;
	public int[] alive;
	public Board board;
	public final int maxsum = 5;
	public final int maxp = 7;

	// public int weight1;
	// public int weight2;
	// public int weight3;

	MinMax(Config conf) {
		this.board = new Board();
		this.board.setStateConfig(conf);
	}

	public Move run(int player) throws Exception {
		int[] rating = new int[4];

		// Multiply by 3 because we are 4 players
		rating = Shallow(board.getStateConfig(), player, maxsum, targetDepth * 3); // maxN(player, targetDepth * 3);

		System.out.println("Player" + player + ", Rating of the move:" + rating[player]);
		System.out.println("Rating of All" + ":" + rating[0] + " " + rating[1] + " " + rating[2] + " " + rating[3]);
		if (savedMove[player] == null) {
			System.out.println("BACKUPMOVE: X = " + backUpMove[player].x + " || Y = " + backUpMove[player].y);
			return backUpMove[player];
		} else {

			System.out.println("MAXN MOVE: X = " + savedMove[player].x + " || Y = " + savedMove[player].y);
			return savedMove[player];
		}

	}

	public int[] maxN(int player, int depth) throws Exception {
		int ratings[] = new int[4];
		// get all free moves
		List<Move> posMoves = board.calcFreeMoves(player, board);
		// if I am the final node, evaluate my Configuration and return the rating
		if (depth == 0 || posMoves == null) {
			ratings = rateAll(player);
			return ratings;
		}
		Config cache;
		// make a List/ array for all the ratings of the possible nodes
		ArrayList<int[]> ratingList = new ArrayList<int[]>();
		int j = 0;

		// iterate through all possible moves
		for (Move move : posMoves) {
			// save the board to the cache
			cache = board.getStateConfig().clone();

			// make the move to create new config / Node
			board.makeMove(move);
			// check to see if Node is Terminal
			ratings = rateAll(player);
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
		int[] bestRating = getBestRating(player, ratingList);

		return bestRating;
	}

	private int getOurBestMove(int player, ArrayList<int[]> list) {
		int cache = 0;
		for (int[] item : list) {
			if (item[player] >= cache) {
				cache = item[player];
			}
		}
		return cache;
	}

	private static int[] getBestRating(int player, ArrayList<int[]> list) {
		int[] cache = new int[4];
		for (int[] item : list) {
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

	int[] rateAll(int player) {
		int ratings[] = new int[4];
		ratings[0] = rate(0);
		ratings[1] = rate(1);
		ratings[2] = rate(2);
		ratings[3] = rate(3);
		for (int i = 0; i < 4; i++) {
			if (ratings[i] == maxp) {
				// if Terminal check for the winner
				if (i == player) {
					// if we won save the move and break ??
					// savedMove = move;
					ratings[player] = Integer.MAX_VALUE;
					return ratings;

				} else {
					// if we lost make sure to rate this config the worst possible
					ratings[player] = Integer.MIN_VALUE;
					return ratings;
				}
			}
		}
		return ratings;
	}

	int rate(int player) {
		int freeStones = 0;
		int jumps = 0;
		Config conf = board.getStateConfig();
		Board bo = board;
		for (int i = player * 5; i < (player + 1) * 5; i++) { // for all stones of this player
			if (!conf.stones[i].inStack && (conf.stones[i].x != 7 || conf.stones[i].x != 7)) { // Stone not in stack or
				freeStones++;
				jumps += conf.stones[i].canJump(bo);
			}
		}
		if (freeStones == 0) {
			int secondPlayer = nextPlayer(player);
			int thirdPlayer = nextPlayer(secondPlayer);
			int fourthPlayer = nextPlayer(thirdPlayer);
			// TODO if all my stones are blocked and nobody has won yet i lost,?

			if (bo.getScore(player) < bo.getScore(secondPlayer) || bo.getScore(player) < bo.getScore(thirdPlayer)
					|| bo.getScore(player) < bo.getScore(fourthPlayer)) {
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
				return 0; // Draw
			}
		}
		// TODO change into something better

		if (conf.stackSto[player] > 0) {

			return freeStones + jumps + bo.getScore(player);
		} else {
			return (jumps*2) + bo.getScore(player);

		}

	}

	int[] Shallow(Config Node, int Player, int Bound, int depth) throws Exception {
		// add something here so that we can check if a player has a score = 7

		// get all possible moves that can be made for this player
		List<Move> posMoves = board.calcFreeMoves(Player, board);

		if (Node.isTerminal() || posMoves.size() == 0 || depth == 0) {

			/// this should indicate best or worst case scenarios ( iE win or lose)
			return rateAll(Player);
		}
		// get the first child node
		Move firstMove = posMoves.get(0);
		// create cache and make the first possible move
		Config cache;
		cache = board.getStateConfig().clone();
		board.makeMove(firstMove);
		// populate the ratings table by evaluating the first leaf node
		
		int Best[] = Shallow(board.getStateConfig(), nextPlayer(Player), maxsum, depth-1);
		// reset the board so that it can be used again
		board.setStateConfig(cache);
		int i = 0;
		// now for the remaining child nodes	board.setStateConfig(cache);

		for (Move move : posMoves) {
			if (i != 0) {

				if (Best[Player] >= Bound) {
					if (move.player == Player && depth == targetDepth*3) {
						savedMove[Player] = move;
					}
					return Best;
				}

				cache = board.getStateConfig().clone();
				board.makeMove(move);

				int Current[] = Shallow(board.getStateConfig(), nextPlayer(Player), maxsum - Best[Player], depth-1);

				if (Current[Player] >  Best[Player]) {
					
				    backUpMove[Player] = move;
				
					Best = Current;

				}
				// reset the board so that it can be used again
				board.setStateConfig(cache);
			}

			i++;
		

		}
       
		return Best;

	}

}
