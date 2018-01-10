/** Author: Moritz Wiemker
 * 	Masterthesis
 *
 *
 *
 */

package model;

import application.Main;

public class ConnectedFireFighter {
	// chain of Timenterval many direction numbers
	// 0 == doesnt move, 1 == go north, 2 == go east, 3 == go south, 4 == go
	// west
	private int[] chain = new int[Main.TimeInterval];
	private int ID;
	private int startVertice;
	private int currentVertice;
	private ConnectedFireFighter leftNeighbour;
	private ConnectedFireFighter rightNeighbour;

	// constructor
	public ConnectedFireFighter() {
		this.ID = Main.FighterID;
		Main.FighterID++;
	}

	// getter and setter
	public int[] getChain() {
		return chain;
	}

	public void setChain(int[] chain) {
		this.chain = chain;
	}

	public int getChainIndex(int index) {
		return chain[index];
	}

	public void setChainIndex(int index, int value) {
		chain[index] = value;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStartVertice() {
		return startVertice;
	}

	public void setStartVertice(int startVertice) {
		this.startVertice = startVertice;
	}

	public int getCurrentVertice() {
		return currentVertice;
	}

	public void setCurrentVertice(int currentVertice) {
		this.currentVertice = currentVertice;
	}

	public ConnectedFireFighter getLeftNeighbour() {
		return leftNeighbour;
	}

	public void setLeftNeighbour(ConnectedFireFighter leftNeighbour) {
		this.leftNeighbour = leftNeighbour;
	}

	public ConnectedFireFighter getRightNeighbour() {
		return rightNeighbour;
	}

	public void setRightNeighbour(ConnectedFireFighter rightNeighbour) {
		this.rightNeighbour = rightNeighbour;
	}
	
	

}
