/** Author: Moritz Wiemker
 * 	Masterthesis
 *
 * Abstract Definition of a FireFighter (it's the normal FireFighter)
 *
 */

package model;

import application.Main;

//abstract class for firefighters
public abstract class AbstractFireFighter {
	public int[] chain = new int[Main.TimeInterval];
	public int ID;
	public int startVertice;
	public int currentVertice;

	//Constructor
	public AbstractFireFighter(){
		this.ID = Main.FighterID;
		Main.FighterID++;
	}

	//Getters and setters
	public int[] getChain() {
		return chain;
	}
	public void setChain(int[] chain) {
		this.chain = chain;
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

	public int getChainIndex(int index) {
		return chain[index];
	}

	public void setChainIndex(int index, int value) {
		chain[index] = value;
	}



}
