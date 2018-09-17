package org.tuomilabs.heart.core;
import java.util.ArrayList;

public class Game {
	
	public static void main(String[] args){
		ArrayList<Card> hand = new ArrayList<Card>();
		ArrayList<Card> trickcards = new ArrayList<Card>();
    }
	
	private void trick(){
		
	}
	
	private int trickwinner(ArrayList<Card> trickcards){
		Card largest = new Card(trickcards.get(0));
		for(Card i:trickcards){
			if( i.getsuit() == largest.getsuit()){
				if(i.getvalue() > largest.getvalue()){
					largest.setvalue(i.getvalue());
					largest.setplayer(i.getplayer());
				}
			}
		}
		return largest.getplayer();
	}
	
	private boolean checkifstarting(ArrayList<Card> hand){
		for(int i = 0; i < 13; i++){
			if(hand.get(i).getvalue() == 0 && hand.get(i).getsuit() == 0) return true;
		}
		return false;
	}
}
