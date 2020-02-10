package KalahGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MinMaxTree {
	static int getMovesCnt = 0;
	static int generateCnt = 0;
	static int memoized = 0;
	static Map<String,ArrayList<ArrayList<Integer>>> memoization = new HashMap<>();
	Node root;
	boolean isNorth=true;
	
	
	public MinMaxTree(GameBoard curBoard){
		root = new Node();
		root.gamestate= new GameBoard(curBoard);
		root.depth=0;
	}

	public class Node{
		GameBoard gamestate;
		int score;
		int depth;
		Node parent;
		ArrayList<Node> children = new ArrayList<>();
		ArrayList<Integer> moves = new ArrayList<>();
		boolean isScored = false;
	}
	
	static int tester = 0;
	public int generate(Node n,int depthLimit){
		generateCnt++;
		if(depthLimit==4){
			System.out.println(tester++);
		}
		if(tester>64){
			int fake = 5;
		}
		int score = 0;
		if(n.depth<depthLimit){
			if(n.depth%2==0){ //Computer moves next (MAX)
				int max = -1000;
				ArrayList<ArrayList<Integer>> availMoves = getAvailMoves(n.gamestate,isNorth);
				for(int i=0;i<availMoves.size();i++){
					Node newNode = new Node();
					newNode.parent=n;
					newNode.depth=n.depth+1;
					newNode.gamestate= new GameBoard(n.gamestate);
					newNode.moves=availMoves.get(i);
					for(int j =0;j<availMoves.get(i).size();j++){
						if(isNorth){
							newNode.gamestate.moveNorth(availMoves.get(i).get(j));
						}
						else{
							newNode.gamestate.moveSouth(availMoves.get(i).get(j));
						}
					}
					n.children.add(newNode);
					int lastScore = generate(newNode,depthLimit);
					if(lastScore>max){
						max = lastScore;
					}
					n.score = max;
					n.isScored = true;
					// Beta pruning
					if(n.depth!=0&&n.parent.isScored &&(n.score>n.parent.score)){
						break;
					}
				}
				n.score = max;
			}
			else{// Player move (min)
				int min = 1000;
				ArrayList<ArrayList<Integer>> availMoves = getAvailMoves(n.gamestate,!isNorth);
				for(int i=0;i<availMoves.size();i++){
					Node newNode = new Node();
					newNode.parent=n;
					newNode.depth=n.depth+1;
					newNode.gamestate= new GameBoard(n.gamestate);
					newNode.moves=availMoves.get(i);
					for(int j =0;j<availMoves.get(i).size();j++){
						if(!isNorth){
							newNode.gamestate.moveNorth(availMoves.get(i).get(j));
						}
						else{
							newNode.gamestate.moveSouth(availMoves.get(i).get(j));
						}
					}
					n.children.add(newNode);
					int lastScore = generate(newNode,depthLimit);
					if(lastScore<min){
						min = lastScore;
					}
					// Alpha pruning
					if(n.depth!=0&&n.parent.isScored &&(n.score<n.parent.score)){
						break;
					}
					n.score = min;
					n.isScored = true;
				}
			}
		}
		else{
			score = n.gamestate.rankBoard(isNorth);
			n.score = score;
			n.isScored = true;
		}
		return n.score;
	}
	
	public ArrayList<ArrayList<Integer>> getAvailMoves(GameBoard board, boolean north){
		getMovesCnt++;
		Integer[] arr = new Integer[board.amtOfHoles];
		
		if(north){
			arr=board.NorthHoles.toArray(arr);
		}
		else{
			arr=board.SouthHoles.toArray(arr);
		}
		String s = ""+arr[0];
		for(int i=1;i<arr.length;i++){
			s+=","+arr[i];
		}
		if(memoization.containsKey(s)){
			memoized++;
			return memoization.get(s);
		}
		ArrayList<ArrayList<Integer>> result = new ArrayList<>();
		
		for(int i=0;i<board.amtOfHoles;i++){
			// north side check
			if(north){
				if(board.NorthHoles.get(i)>0){
					// Check for double move
					if(board.NorthHoles.get(i)%(2*board.amtOfHoles+2)==board.amtOfHoles-i){
						GameBoard fakeBoard = new GameBoard(board);
						fakeBoard.moveNorth(i);
						// If that move ends the game, return that move and dont check for more
						if(fakeBoard.isGameOver()){
							ArrayList<Integer> curResults = new ArrayList<>();
							curResults.add(i);
							result.add(curResults);
						}
						// Otherwise add all of those possible moves recursively
						else{
							ArrayList<ArrayList<Integer>> newResults = getAvailMoves(fakeBoard,north);
							for(int j=0;j<newResults.size();j++){
								ArrayList<Integer> curResults = new ArrayList<>();
								curResults.add(i);
								curResults.addAll(newResults.get(j));
								result.add(curResults);
							}
						}
					}
					else{
						ArrayList<Integer> curResults = new ArrayList<>();
						curResults.add(i);
						result.add(curResults);
					}
				}
			}
			// south side check
			else{
				if(board.SouthHoles.get(i)>0){
					// Check for double move
					if(board.SouthHoles.get(i)==board.amtOfHoles-i){
						GameBoard fakeBoard = new GameBoard(board);
						fakeBoard.moveSouth(i);
						// If that move ends the game, return that move and dont check for more
						if(fakeBoard.isGameOver()){
							ArrayList<Integer> curResults = new ArrayList<>();
							curResults.add(i);
							result.add(curResults);
						}
						// Otherwise add all of those possible moves recursively
						else{
							ArrayList<ArrayList<Integer>> newResults = getAvailMoves(fakeBoard,north);
							for(int j=0;j<newResults.size();j++){
								ArrayList<Integer> curResults = new ArrayList<>();
								curResults.add(i);
								curResults.addAll(newResults.get(j));
								result.add(curResults);
							}
						}
					}
					else{
						ArrayList<Integer> curResults = new ArrayList<>();
						curResults.add(i);
						result.add(curResults);
					}
				}
			}
		}
		memoization.put(s, result);
		return result;
	}
	
	public ArrayList<Integer> getBestMove(){
		generateCnt=0;
		getMovesCnt = 0;
		memoized = 0;
		generate(root,5);
		int best = -1;
		int max = -1001;
		for(int i=0;i<root.children.size();i++){
			if(root.children.get(i).score>max){
				max = root.children.get(i).score;
				best = i;
			}
		}
		if(best == -1){
			return new ArrayList<>();
		}
		System.out.println("generates:"+generateCnt+"\tGetAvailMoves:"+getMovesCnt+"\tmemoized:"+memoized);
		return root.children.get(best).moves;
	}

}