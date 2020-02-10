package KalahGame;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard {
	// 2 array lists to represent the holes on each sides of the board
	ArrayList<Integer> NorthHoles = new ArrayList<Integer>();
	ArrayList<Integer> SouthHoles = new ArrayList<Integer>();
	// 2 ints to represent the values in each sides scoring well
	int NorthScoringWell;
	int SouthScoringWell;
	// int for the amount of holes setting
	int amtOfHoles;
	// int for the amount of seeds in each house
	int seedsPerHouse;
	public GameBoard(GameBoard gb){
		amtOfHoles=gb.amtOfHoles;
		seedsPerHouse =gb.seedsPerHouse;
		NorthScoringWell=gb.NorthScoringWell;
		SouthScoringWell=gb.SouthScoringWell;
		for(int i=0;i<amtOfHoles;i++){
			NorthHoles.add(gb.NorthHoles.get(i));
			SouthHoles.add(gb.SouthHoles.get(i));
		}
	}
	
	// constructing the gameboard, each hole starts with 4 seeds,unless randomly set and each scoring well starts with 0 seeds
	public GameBoard(int numOfHoles,int numSeeds,boolean placeRandomly) {
		NorthScoringWell = 0;
		SouthScoringWell = 0;
		amtOfHoles=numOfHoles;
		seedsPerHouse=numSeeds;
		if(!placeRandomly){
			for(int i=0; i < amtOfHoles; i++){
				NorthHoles.add(numSeeds);
				SouthHoles.add(numSeeds);
			} 
		}
		else{
			//Set all holes to 0
			for(int i=0; i < amtOfHoles; i++){
				NorthHoles.add(0);
				SouthHoles.add(0);
			} 
			//set total seeds to 4 for each hole
			int totalSeeds = amtOfHoles*seedsPerHouse;
			Random randomGen = new Random();
			while(totalSeeds>0){
				totalSeeds--;
				//get a random number between 0 and the number of holes-1, then add seed to that spot
				int ranNum = randomGen.nextInt(amtOfHoles-1);
				NorthHoles.set(ranNum,NorthHoles.get(ranNum)+1);
				SouthHoles.set(ranNum,SouthHoles.get(ranNum)+1);
			}
		}
	}
	//Constructor with number of holes set and random setting, seeds per house defaults to 4
	public GameBoard(int numOfHoles,boolean random){
		this(numOfHoles,4,random);
	}
	//Constructor with only number of holes set, random setting defaults to false and 4 seeds per house
	public GameBoard(int numOfHoles){
		this(numOfHoles,4,false);
	}
	//Constructor with only random setting, number of holes defaults to 6 and 4 seeds per house
	public GameBoard(boolean random){
		this(6,4,random);
	}
	//Default constructor of non-random setting and 6 holes and 4 seeds per house
	public GameBoard(){
		this(6,4,false);
	}
	
	// accessors
	public int getNorthHole(int index) {
		return NorthHoles.get(index);
	}
	
	public int getSouthHole(int index) {
		return SouthHoles.get(index);
	}
	
	public int getNorthWell() {
		return NorthScoringWell;
	}
	
	public int getSouthWell() {
		return SouthScoringWell;
	}

	/*
	 * Performs a move from the north side
	 * Returns -1 if move is invalid, 1 if the player goes again otherwise returns 0
	 */
	public int moveNorth(int index){
		// Validate move
		if(index<0 || index>=amtOfHoles){
			return -1;
		}
		if(NorthHoles.get(index)==0){
			return -1;
		}
		// set how many seeds need to be placed
		int count = NorthHoles.get(index);
		NorthHoles.set(index, 0);
		int curIndex = index;
		boolean isNorth = true;
		// call helper function to move all the seeds
		int lastSpot = move(count,curIndex,isNorth);
		//Check to see if it landed in the players well, if so they get another turn
		if(lastSpot==amtOfHoles){
			return 1;
		}
		// Check to see if it landed in an empty spot, if so capture all pieces
		else if(lastSpot<amtOfHoles){
			if(NorthHoles.get(lastSpot)==1 && SouthHoles.get(amtOfHoles-lastSpot-1)>0){
				int captured = SouthHoles.get(amtOfHoles-1-lastSpot);
				SouthHoles.set(amtOfHoles-1-lastSpot,0);
				NorthHoles.set(lastSpot, 0);
				NorthScoringWell+=captured+1;
			}
		}
		return 0;
	}
	/*
	 * Performs a move from the south side
	 * Returns -1 if move is invalid, 1 if the player goes again otherwise returns 0
	 */
	public int moveSouth(int index){
		// Validate move
		if(index<0 || index>=amtOfHoles){
			return -1;
		}
		if(SouthHoles.get(index)==0){
			return -1;
		}
		// set how many seeds need to be placed
		int count = SouthHoles.get(index);
		SouthHoles.set(index,0);
		int curIndex = index;
		boolean isNorth = false;
		int lastSpot = move(count,curIndex,isNorth);
		if(lastSpot==(amtOfHoles)*2+1){
			return 1;
		}
		// Check to see if it landed in an empty spot and opponent's corresponding house has seeds, if so capture all pieces
		else if(lastSpot>amtOfHoles){
			lastSpot-=amtOfHoles+1;
			if(SouthHoles.get(lastSpot)==1 && NorthHoles.get(amtOfHoles-lastSpot-1)>0){
				int captured = NorthHoles.get(amtOfHoles-1-lastSpot);
				NorthHoles.set(amtOfHoles-1-lastSpot,0);
				SouthHoles.set(lastSpot, 0);
				SouthScoringWell+=captured+1;
			}
		}
		return 0;
	}
	
	/* Mover helper function
	 * returns last index
	 */
	private int move(int count,int curIndex, boolean isNorth){
		while(count>0){
			count--;
			curIndex++;
			if(curIndex>amtOfHoles){
				// reset index and switch sides
				curIndex=0;
				isNorth=!isNorth;
			}
			if(isNorth){
				if(curIndex==amtOfHoles){
					NorthScoringWell++;
				}
				else{
					NorthHoles.set(curIndex,NorthHoles.get(curIndex)+1);
				}
			}
			else{
				if(curIndex==amtOfHoles){
					SouthScoringWell++;
				}
				else{
					SouthHoles.set(curIndex,SouthHoles.get(curIndex)+1);
				}
			}
		}
		if(!isNorth){
			return curIndex+amtOfHoles+1;
		}
		return curIndex;
	}
	
	/*
	 * Checks to see if game is over
	 * returns true if there are no more moves for a player
	 */
	public boolean isGameOver(){
		boolean result = true;
		for(int i=0;i<amtOfHoles;i++){
			if(NorthHoles.get(i)>0){
				result = false;
				break;
			}
		}
		if(result){
			return true;
		}
		result=true;
		String s = "";
		for(int i=0;i<amtOfHoles;i++){
			if(SouthHoles.get(i)>0){
				result = false;
				s+=i+",";
			}
		}
		//System.out.println(s);
		return result;
	}
	
	public int southScore(){
		int total = 0;
		for(int i=0;i<SouthHoles.size();i++){
			total+=SouthHoles.get(i);
		}
		total+=SouthScoringWell;
		return total;
	}
	public int northScore(){
		int total = 0;
		for(int i=0;i<NorthHoles.size();i++){
			total+=NorthHoles.get(i);
		}
		total+=NorthScoringWell;
		return total;
	}
	
	// Assigns a score of the boardstate to be used in the AI
	public int rankBoard(boolean isNorth){
		int score = 0;
		if(isNorth){
			// Ensured win conditions should be 1000
			if(NorthScoringWell>SouthScoringWell+seedsInNorth()+seedsInSouth() || (isGameOver() && NorthScoringWell > SouthScoringWell)){
				score = 1000;
			}
			// Ensured lose conditions should be -1000
			else if(SouthScoringWell>NorthScoringWell+seedsInNorth()+seedsInSouth() || (isGameOver() && SouthScoringWell > NorthScoringWell)){
				score = -1000;
			}
			else {
				score = NorthScoringWell-SouthScoringWell;
			}
		}
		return score;
	}
	
	// get amount of seeds in north houses
	public int seedsInNorth(){
		int amt = 0;
		for(int i=0;i<NorthHoles.size();i++){
			amt+=NorthHoles.get(i);
		}
		return amt;
	}
	// get amount of seeds in south houses
	public int seedsInSouth(){
		int amt = 0;
		for(int i=0;i<SouthHoles.size();i++){
			amt+=SouthHoles.get(i);
		}
		return amt;
	}
	
	 public void pieRule(){
    	ArrayList<Integer> tempNorth = new ArrayList<>();
    	ArrayList<Integer> tempSouth = new ArrayList<>();
    	for(int i=0;i<SouthHoles.size();i++){
    		tempNorth.add(SouthHoles.get(i));
    		tempSouth.add(NorthHoles.get(i));
    	}
    	SouthHoles=tempSouth;
    	NorthHoles=tempNorth;
	 }
}